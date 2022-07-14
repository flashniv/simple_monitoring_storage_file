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

@Deprecated(forRemoval = true)
class TimeElapsedFromLastEventExpressionTest extends AbstractTest {
    @BeforeEach
    void setUp2() throws Exception {
        for (int i = 0; i < 20; i++) {
            memoryMetricsQueue.putEvent(new Event("test.stage.db.item1", "{}", Instant.now().getEpochSecond() - (20 - i) * 10, Math.random()));
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
        TimeElapsedFromLastEventExpression timeElapsedFromLastEventExpression = new TimeElapsedFromLastEventExpression(readAllValuesOfMetricExpression);
        System.out.println(timeElapsedFromLastEventExpression.getJSON());
    }

    @Test
    void getValue() throws Exception {
        ReadAllValuesOfMetricExpression readAllValuesOfMetricExpression = new ReadAllValuesOfMetricExpression("test.stage.db.item1", "{}");
        TimeElapsedFromLastEventExpression timeElapsedFromLastEventExpression = new TimeElapsedFromLastEventExpression(readAllValuesOfMetricExpression);

        Double dur = timeElapsedFromLastEventExpression.getValue();
        Assertions.assertTrue(dur > 0);
    }

    @Test
    void initialize() throws ExpressionException {
        TimeElapsedFromLastEventExpression timeElapsedFromLastEventExpression = new TimeElapsedFromLastEventExpression();
        timeElapsedFromLastEventExpression.initialize("{\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ReadAllValuesOfMetricExpression\",\"parameters\":{\"metricName\":\"test.stage.db.item1\",\"parameterGroup\":\"{}\"}}}");

        Expression<List<DataElement>> arg1 = timeElapsedFromLastEventExpression.getArg1();
        Assertions.assertNotNull(arg1);
    }
}