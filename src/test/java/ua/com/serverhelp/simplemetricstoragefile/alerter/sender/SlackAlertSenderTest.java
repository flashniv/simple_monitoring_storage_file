package ua.com.serverhelp.simplemetricstoragefile.alerter.sender;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlackAlertSenderTest {
    private AlertSender alertSender;

    @BeforeEach
    void setUp() {
        alertSender = new SlackAlertSender();

        JSONObject params = new JSONObject();
        params.put("slackWebHook", "https://hooks.slack.com/services/KEY");
        params.put("slackChannel", "dept_dev_admin_alerts");
        params.put("slackUserName", "Test user");

        alertSender.initialize(params.toString());
    }

    @Test
    void sendMessage() throws Exception {
//        Trigger trigger=new Trigger();
//        trigger.setId("db.test-trigger");
//        trigger.setName("Test trigger");
//        trigger.setLastStatus(TriggerStatus.OK);
//        trigger.setLastStatusUpdate(Instant.now());
//
//        Alert alert=new Alert();
//        alert.setTrigger(trigger);
//        alert.setAlertTimestamp(Instant.now());
//        alert.setTriggerStatus(TriggerStatus.OK);
//
//        alertSender.sendMessage(alert);
    }
}