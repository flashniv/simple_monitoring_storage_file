package ua.com.serverhelp.simplemetricstoragefile.alerter;

import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;

import java.io.IOException;

public interface AlertSender {
    boolean sendMessage(Alert alert) throws IOException;
}
