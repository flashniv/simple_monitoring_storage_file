package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

class ReadAllValuesOfMetricExpressionTest extends AbstractTest {
    @BeforeEach
    void setUp2() throws Exception {
        for (int i = 0; i < 20; i++) {
            memoryMetricsQueue.putEvent(new Event("test.stage.db.item1", "{}", Instant.now().getEpochSecond(), Math.random()));
        }
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            try {
                fileDriver.writeMetric(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                throw new ExpressionException("File write error", e);
            }
        }
    }

    @Test
    void getJSON() {
        ReadAllValuesOfMetricExpression readAllValuesOfMetricExpression = new ReadAllValuesOfMetricExpression("test.stage.db.item1", "{}");
        System.out.println(readAllValuesOfMetricExpression.getJSON());
    }

    @Test
    void getValue() throws Exception {
        ReadAllValuesOfMetricExpression readAllValueOfMetricExpression = new ReadAllValuesOfMetricExpression();
        readAllValueOfMetricExpression.initialize("{\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        List<DataElement> dataElements = readAllValueOfMetricExpression.getValue();

        Assertions.assertNotNull(dataElements);
        Assertions.assertFalse(dataElements.isEmpty());
    }

    @Test
    void initialize() throws Exception {
        ReadAllValuesOfMetricExpression readAllValueOfMetricExpression = new ReadAllValuesOfMetricExpression();
        readAllValueOfMetricExpression.initialize("{\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        Assertions.assertNotNull(readAllValueOfMetricExpression.getMetricName());
        Assertions.assertNotNull(readAllValueOfMetricExpression.getParameterGroup());
    }
}