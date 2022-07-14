package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class FileDriverTest extends AbstractTest {
    private final String metric = "test.stage.db.booleanitem1";

    @Test
    void writeMetric() {
        List<DataElement> dataElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DataElement dataElement = new DataElement(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond(), Math.random()));
            dataElements.add(dataElement);
        }
        Assertions.assertDoesNotThrow(() -> fileDriver.writeMetric(metric, dataElements));


        Assertions.assertDoesNotThrow(() -> {
            List<DataElement> dataElements1 = fileDriver.readMetric(metric);
            Assertions.assertEquals(dataElements, dataElements1);
        });

    }
}