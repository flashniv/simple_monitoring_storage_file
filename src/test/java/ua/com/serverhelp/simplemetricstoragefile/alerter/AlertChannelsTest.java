package ua.com.serverhelp.simplemetricstoragefile.alerter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;

import java.time.Instant;

class AlertChannelsTest extends AbstractTest {

    private Alert createAlert(String name) {
        Trigger trigger = new Trigger();
        trigger.setTriggerId(name);
        trigger.setPriority(TriggerPriority.HIGH);
        trigger.setLastStatus(TriggerStatus.OK);
        trigger.setConf("");
        trigger.setLastStatusUpdate(Instant.now());
        trigger.setName("Test trigger");

        Alert alert = new Alert();
        alert.setAlertTimestamp(Instant.now());
        alert.setTrigger(trigger);
        return alert;
    }

    @Test
    void sendAlert() throws Exception {
        //insert into alert_filter(id,alert_channel_id,regexp,allow,priority) values (4,1,'.*',true,400);
        alertChannels.sendAlert(createAlert("test.stage.db.booleanitem1{}"));
        Mockito.verify(alertSender, Mockito.times(1)).sendMessage(Mockito.any());
        //insert into alert_filter(id,alert_channel_id,regexp,allow,priority) values (2,1,'.*load.*',false,200);
        alertChannels.sendAlert(createAlert("test.stage.db.load_average{}"));
        Mockito.verify(alertSender, Mockito.times(1)).sendMessage(Mockito.any());
        //insert into alert_filter(id,alert_channel_id,regexp,allow,priority) values (3,1,'.*cron.*',false,300);
        alertChannels.sendAlert(createAlert("test.stage.db.cron.asdasd{}"));
        Mockito.verify(alertSender, Mockito.times(1)).sendMessage(Mockito.any());
        //insert into alert_filter(id,alert_channel_id,regexp,allow,priority) values (1,1,'.*prod.*',true,100);
        alertChannels.sendAlert(createAlert("test.stage.db.prod.load_average{}"));
        Mockito.verify(alertSender, Mockito.times(2)).sendMessage(Mockito.any());
    }
}