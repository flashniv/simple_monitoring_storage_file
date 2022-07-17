package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastTimestampOfMetricExpression;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

class ReadLastTimestampOfMetricExpressionTest extends AbstractTest {
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
        ReadLastTimestampOfMetricExpression readLastTimestampOfMetricExpression = new ReadLastTimestampOfMetricExpression("test.stage.db.item1", "{}", dirName);
        System.out.println(readLastTimestampOfMetricExpression.getJSON());
    }

    @Test
    void getValue() throws ExpressionException {
        ReadLastTimestampOfMetricExpression readLastTimestampOfMetricExpression = new ReadLastTimestampOfMetricExpression("test.stage.db.item1", "{}", dirName);

        Double res = readLastTimestampOfMetricExpression.getValue();
        Assertions.assertNotNull(res);
    }

    @Test
    void initialize() throws ExpressionException {
        ReadLastTimestampOfMetricExpression readLastTimestampOfMetricExpression = new ReadLastTimestampOfMetricExpression();
        readLastTimestampOfMetricExpression.initialize("{\"metricsDirectory\":\"" + dirName + "\",\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        Double data = readLastTimestampOfMetricExpression.getValue();
        Assertions.assertNotNull(data);
    }

}