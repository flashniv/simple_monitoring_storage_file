package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
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
class ReadAllValuesOfMetricExpressionTest {
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
        ReadAllValuesOfMetricExpression readAllValuesOfMetricExpression=new ReadAllValuesOfMetricExpression("test.stage.db.item1", "{}");
        System.out.println(readAllValuesOfMetricExpression.getJSON());
    }

    @Test
    void getValue() throws Exception{
        ReadAllValuesOfMetricExpression readAllValueOfMetricExpression=new ReadAllValuesOfMetricExpression();
        readAllValueOfMetricExpression.initialize("{\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        List<DataElement> dataElements=readAllValueOfMetricExpression.getValue();

        Assertions.assertNotNull(dataElements);
        Assertions.assertFalse(dataElements.isEmpty());
    }

    @Test
    void initialize() throws Exception{
        ReadAllValuesOfMetricExpression readAllValueOfMetricExpression=new ReadAllValuesOfMetricExpression();
        readAllValueOfMetricExpression.initialize("{\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        Assertions.assertNotNull(readAllValueOfMetricExpression.getMetricName());
        Assertions.assertNotNull(readAllValueOfMetricExpression.getParameterGroup());
    }
}