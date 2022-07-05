package ua.com.serverhelp.simplemetricstoragefile.queue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.Event;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemoryMetricsQueueTest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;

    @Test
    void getFormattedEvents() {
        memoryMetricsQueue.putEvent(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond(), 0.001));
        Map<String, List<DataElement>> map=memoryMetricsQueue.getFormattedEvents();
        Assertions.assertEquals(1, map.size());

        List<DataElement> dataElements=map.get("test.stage.db.booleanitem1{}");
        Assertions.assertNotNull(dataElements);
        Assertions.assertEquals(1, dataElements.size());

        DataElement dataElement=dataElements.get(0);
        Assertions.assertEquals(0.001, dataElement.getValue());

    }

}