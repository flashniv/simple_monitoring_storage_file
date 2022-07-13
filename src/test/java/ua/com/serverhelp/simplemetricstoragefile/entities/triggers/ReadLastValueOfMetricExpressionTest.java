package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.MetricRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReadLastValueOfMetricExpressionTest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private FileDriver fileDriver;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;
    @Autowired
    private MetricRepository metricRepository;
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    @BeforeEach
    void setUp() throws Exception {
        for (int i = 0; i < 20; i++) {
            memoryMetricsQueue.putEvent(new Event("test.stage.db.item1", "{}", Instant.now().getEpochSecond(), Math.random()));
        }
        Map<String, List<DataElement>> map=memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            try {
                fileDriver.writeMetric(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                throw new ExpressionException("File write error", e);
            }
        }
    }

    @AfterEach
    void tearDown() {
        parameterGroupRepository.deleteAll();
        metricRepository.deleteAll();
        File file = new File(dirName);
        Assertions.assertTrue(deleteDirectory(file));
    }

    @Test
    void getJSON() {
        ReadLastValueOfMetricExpression readLastValueOfMetricExpression=new ReadLastValueOfMetricExpression("test.stage.db.item1","{}");
        System.out.println(readLastValueOfMetricExpression.getJSON());
    }

    @Test
    void getValue() throws ExpressionException {
        ReadLastValueOfMetricExpression readLastValueOfMetricExpression=new ReadLastValueOfMetricExpression("test.stage.db.item1","{}");

        Double res=readLastValueOfMetricExpression.getValue();
        Assertions.assertNotNull(res);
    }

    @Test
    void initialize() throws ExpressionException {
        ReadLastValueOfMetricExpression readLastValueOfMetricExpression=new ReadLastValueOfMetricExpression();
        readLastValueOfMetricExpression.initialize("{\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        Double data=readLastValueOfMetricExpression.getValue();
        Assertions.assertNotNull(data);
    }
}