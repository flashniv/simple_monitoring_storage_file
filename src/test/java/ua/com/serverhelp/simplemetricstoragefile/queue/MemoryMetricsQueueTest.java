package ua.com.serverhelp.simplemetricstoragefile.queue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;

import java.time.Instant;
import java.util.List;
import java.util.Map;

class MemoryMetricsQueueTest extends AbstractTest {

    @Test
    void getFormattedEvents() {
        for (int i = 0; i < 100; i++) {
            memoryMetricsQueue.putEvent(new Event("test.stage.db.booleanitem11", "{}", Instant.now().getEpochSecond() + i, 0.001 * i));
        }
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        List<DataElement> dataElements = map.get("test.stage.db.booleanitem11{}");
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