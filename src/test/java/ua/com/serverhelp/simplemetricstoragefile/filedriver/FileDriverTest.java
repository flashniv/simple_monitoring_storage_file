package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class FileDriverTest extends AbstractTest {
    private final String metric = "test.stage.db.booleanitem1";

    @Test
    void writeMetric() {
        List<DataElement> dataElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DataElement dataElement = new DataElement(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond() - i, Math.random()));
            dataElements.add(dataElement);
        }
        Assertions.assertDoesNotThrow(() -> fileDriver.writeMetric(metric, dataElements));

        List<DataElement> dataElements1 = new ArrayList<>();

        Assertions.assertDoesNotThrow(() -> {
            dataElements1.addAll(fileDriver.readMetric(metric));
        });
        Assertions.assertEquals(dataElements.stream().sorted(Comparator.comparingLong(DataElement::getTimestamp)).collect(Collectors.toList()), dataElements1);
    }

    @Test
    void readMetricWithDateRange() {
        List<DataElement> dataElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DataElement dataElement = new DataElement(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond() - i * 10, Math.random()));
            dataElements.add(dataElement);
        }
        Assertions.assertDoesNotThrow(() -> fileDriver.writeMetric(metric, dataElements));

        List<DataElement> dataElements1 = new ArrayList<>();

        Assertions.assertDoesNotThrow(() -> {
            Instant begin=Instant.now().minus(5, ChronoUnit.MINUTES);
            Instant end=Instant.now().minus(10, ChronoUnit.MINUTES);
            dataElements1.addAll(fileDriver.readMetric(metric,begin,end));
        });
        Assertions.assertEquals(30, dataElements1.size());
    }
}