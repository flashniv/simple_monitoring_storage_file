package ua.com.serverhelp.simplemetricstoragefile.filedriver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
            dataElements1.addAll(fileDriver.readMetric(metric, Instant.ofEpochSecond(1), Instant.now()));
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
            Instant begin = Instant.now().minus(10, ChronoUnit.MINUTES);
            Instant end = Instant.now().minus(5, ChronoUnit.MINUTES);
            dataElements1.addAll(fileDriver.readMetric(metric, begin, end));
        });
        Assertions.assertEquals(30, dataElements1.size());
    }

    @Test
    void readMetricWithHook() throws IOException, ClassNotFoundException {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<DataElement> dataElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DataElement dataElement = new DataElement(new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond() - i * 10, Math.random()));
            dataElements.add(dataElement);
        }
        Assertions.assertDoesNotThrow(() -> fileDriver.writeMetric(metric, dataElements));

        fileDriver.readMetricWithHook(metric, dataElement -> {
            atomicInteger.incrementAndGet();
        });
        Assertions.assertEquals(100, atomicInteger.get());
    }

    @Test
    void readLastEventOfMetric() throws IOException, ClassNotFoundException, ExpressionException {
        List<DataElement> dataElements = new ArrayList<>();
        long lastTimestamp = 0;
        double lastValue = Double.MIN_VALUE;

        for (int i = 0; i < 10; i++) {
            lastTimestamp = Instant.now().getEpochSecond() - (10 - i) * 10;
            lastValue = Math.random();
            DataElement dataElement = new DataElement(new Event("test.stage.db.booleanitem1", "{}", lastTimestamp, lastValue));
            dataElements.add(dataElement);
        }
        Assertions.assertDoesNotThrow(() -> fileDriver.writeMetric(metric, dataElements));

        DataElement dataElement = fileDriver.readLastEventOfMetric(metric);
        Assertions.assertEquals(lastTimestamp, dataElement.getTimestamp());
        Assertions.assertEquals(lastValue, dataElement.getValue());
    }
}