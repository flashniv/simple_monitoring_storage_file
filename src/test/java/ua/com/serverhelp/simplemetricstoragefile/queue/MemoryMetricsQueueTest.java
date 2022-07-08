package ua.com.serverhelp.simplemetricstoragefile.queue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemetricstoragefile.storage.MetricRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemoryMetricsQueueTest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private MetricRepository metricRepository;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;

    @AfterEach
    void tearDown() {
        parameterGroupRepository.deleteAll();
        metricRepository.deleteAll();
    }

    @Test
    void getFormattedEvents() {
        for (int i = 0; i < 100; i++) {
            memoryMetricsQueue.putEvent(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond() + i, 0.001 * i));
        }
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        List<DataElement> dataElements = map.get("test.stage.db.booleanitem1{}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(100, dataElements.size());

        DataElement dataElement = dataElements.get(1);
        Assertions.assertEquals(0.001, dataElement.getValue());

        List<Metric> metrics = metricRepository.findAll();
        Assertions.assertEquals(1, metrics.size());

        List<ParameterGroup> parameterGroups = parameterGroupRepository.findAll();
        Assertions.assertEquals(1, parameterGroups.size());
    }

}