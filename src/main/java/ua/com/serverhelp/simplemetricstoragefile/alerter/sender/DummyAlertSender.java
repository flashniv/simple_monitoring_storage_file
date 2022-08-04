package ua.com.serverhelp.simplemetricstoragefile.alerter.sender;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;

import java.io.IOException;

@Slf4j
public class DummyAlertSender implements AlertSender {
    @Override
    public void initialize(String jsonParams) throws JSONException {

    }

    @Override
    public void sendMessage(Alert alert) throws IOException {
        log.error("Alert: " + alert.toString());
    }
}
