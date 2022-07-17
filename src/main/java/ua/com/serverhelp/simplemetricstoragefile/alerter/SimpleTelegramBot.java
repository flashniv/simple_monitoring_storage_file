package ua.com.serverhelp.simplemetricstoragefile.alerter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.utils.httpdriver.HttpDriver;
import ua.com.serverhelp.simplemetricstoragefile.utils.httpdriver.HttpResponse;
import ua.com.serverhelp.simplemetricstoragefile.utils.httpdriver.SimpleHttpDriver;

import java.io.IOException;

/**
 * Class for send messages into TG
 */
@Component
public class SimpleTelegramBot implements AlertSender { //TODO add tests
    /**
     * Driver for use curl options
     */
    private final HttpDriver httpDriver = new SimpleHttpDriver();
    /**
     * Token for TG flashnivbot
     */
    @Value("${tg.chat.token}")
    private String token;
    /**
     * work chat
     */
    @Value("${tg.chat.alert_chat_id}")
    private int chatId;

    /**
     * Function for send message to any chat
     *
     * @param alert for send
     * @return true if message was sent
     */
    @Override
    public boolean sendMessage(Alert alert) throws IOException {
        HttpResponse httpResponse;
        httpDriver.setURL("https://api.telegram.org");
        httpDriver.setAdditionalURL("/bot" + token + "/sendMessage");
        httpDriver.addParameter("chat_id", "" + chatId);
        httpDriver.addParameter("text", getText(alert));
        httpDriver.addParameter("parse_mode", "HTML");
        httpResponse = httpDriver.sendPost();
        if (httpResponse.getCode() != 200) {
            throw new IOException("Telegram return " + httpResponse.getCode() + " body" + httpResponse.getBody());
        }

        return true;
    }

    private String getText(Alert alert) {
        Trigger trigger = alert.getTrigger();
        switch (trigger.getLastStatus()) {
            case FAILED:
            case UNCHECKED:
                return "<b>FAIL check trigger failed " + trigger.getName() + "</b>\non event time " + trigger.getLastStatusUpdate();
            case OK:
                return "<b>OK " + trigger.getName() + "</b>\non event time " + trigger.getLastStatusUpdate();
            case ERROR:
                return "<b>ERR " + trigger.getName() + "</b>\non event time " + trigger.getLastStatusUpdate();
        }
        return "";
    }

}