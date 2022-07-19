package ua.com.serverhelp.simplemetricstoragefile.utils.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;

import java.time.Instant;
import java.util.Optional;

class CronTest extends AbstractTest {

    @Test
    void checkTriggers() {
        String id = DigestUtils.md5DigestAsHex("test.stage.db.booleanitem1{}".getBytes());
        Trigger trigger = new Trigger();

        trigger.setId(id);
        trigger.setTriggerId("test.stage.db.booleanitem1{}");
        trigger.setName("Test trigger");
        trigger.setConf("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\"parameters\":{\"metricsDirectory\":\"" + dirName + "\",\"metricName\":\"test.stage.db.booleanitem1\",\"parameterGroup\":\"{}\"}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}");

        triggerRepository.save(trigger);
        //check new trigger state
        Assertions.assertEquals(TriggerStatus.UNCHECKED, trigger.getLastStatus());

        //Check trigger without data
        cron.checkTriggers();

        Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
        Assertions.assertTrue(optionalTrigger.isPresent());
        Assertions.assertEquals(TriggerStatus.FAILED, optionalTrigger.get().getLastStatus());

        //check trigger to error
        Event event = new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond(), 0.0);
        memoryMetricsQueue.putEvent(event);
        cron.storeMetrics();
        cron.checkTriggers();

        optionalTrigger = triggerRepository.findById(id);
        Assertions.assertTrue(optionalTrigger.isPresent());
        Assertions.assertEquals(TriggerStatus.ERROR, optionalTrigger.get().getLastStatus());

        //check trigger to ok
        event = new Event("test.stage.db.booleanitem1", "{}", Instant.now().getEpochSecond(), 1.0);
        memoryMetricsQueue.putEvent(event);
        cron.storeMetrics();
        cron.checkTriggers();

        optionalTrigger = triggerRepository.findById(id);
        Assertions.assertTrue(optionalTrigger.isPresent());
        Assertions.assertEquals(TriggerStatus.OK, optionalTrigger.get().getLastStatus());
    }
}