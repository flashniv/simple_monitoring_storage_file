package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadValuesOfMetricExpression;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

class ReadValuesOfMetricExpressionTest extends AbstractTest {
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
        ReadValuesOfMetricExpression readValuesOfMetricExpression = new ReadValuesOfMetricExpression("test.stage.db.item1", "{}", dirName, 300, 0);
        System.out.println(readValuesOfMetricExpression.getJSON());
    }

    @Test
    void getValue() throws Exception {
        ReadValuesOfMetricExpression readValueOfMetricExpression = new ReadValuesOfMetricExpression();
        readValueOfMetricExpression.initialize("{\"metricsDirectory\":\"" + dirName + "\",\"metricName\":\"test.stage.db.item1\",\"beginDiff\":300,\"endDiff\":0,\"parameterGroup\":\"{}\"}");
        List<DataElement> dataElements = readValueOfMetricExpression.getValue();

        Assertions.assertNotNull(dataElements);
        Assertions.assertFalse(dataElements.isEmpty());
    }

    @Test
    void initialize() throws Exception {
        ReadValuesOfMetricExpression readValueOfMetricExpression = new ReadValuesOfMetricExpression();
        readValueOfMetricExpression.initialize("{\"metricsDirectory\":\"" + dirName + "\",\"metricName\":\"test.stage.db.item1\",\"beginDiff\":300,\"endDiff\":0,\"parameterGroup\":\"{}\"}");
        Assertions.assertNotNull(readValueOfMetricExpression.getMetricName());
        Assertions.assertNotNull(readValueOfMetricExpression.getParameterGroup());
    }
}