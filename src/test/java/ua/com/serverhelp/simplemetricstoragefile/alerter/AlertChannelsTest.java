package ua.com.serverhelp.simplemetricstoragefile.alerter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.alerter.sender.DummyAlertSender;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AlertChannelsTest extends AbstractTest {

    @Test
    void sendAlert() throws Exception{
        Trigger trigger=new Trigger();
        trigger.setTriggerId("test.stage.db.booleanitem1{}");
        trigger.setPriority(TriggerPriority.HIGH);
        trigger.setLastStatus(TriggerStatus.OK);
        trigger.setConf("");
        trigger.setLastStatusUpdate(Instant.now());
        trigger.setName("Test trigger");

        Alert alert=new Alert();
        alert.setAlertTimestamp(Instant.now());
        alert.setTrigger(trigger);

        Assertions.assertTrue(alertChannels.sendAlert(alert));
    }
}