package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

class ReadLastValueOfMetricExpressionTest extends AbstractTest {
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
        ReadLastValueOfMetricExpression readLastValueOfMetricExpression = new ReadLastValueOfMetricExpression("test.stage.db.item1", "{}",dirName);
        System.out.println(readLastValueOfMetricExpression.getJSON());
    }

    @Test
    void getValue() throws ExpressionException {
        ReadLastValueOfMetricExpression readLastValueOfMetricExpression = new ReadLastValueOfMetricExpression("test.stage.db.item1", "{}",dirName);

        Double res = readLastValueOfMetricExpression.getValue();
        Assertions.assertNotNull(res);
    }

    @Test
    void initialize() throws ExpressionException {
        ReadLastValueOfMetricExpression readLastValueOfMetricExpression = new ReadLastValueOfMetricExpression();
        readLastValueOfMetricExpression.initialize("{\"metricsDirectory\":\""+dirName+"\",\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}");
        Double data = readLastValueOfMetricExpression.getValue();
        Assertions.assertNotNull(data);
    }
}