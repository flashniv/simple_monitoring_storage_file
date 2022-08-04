package ua.com.serverhelp.simplemetricstoragefile.alerter.sender;

import org.json.JSONException;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;

import java.io.IOException;

public interface AlertSender {
    void initialize(String jsonParams) throws JSONException;

    void sendMessage(Alert alert) throws IOException;
}
