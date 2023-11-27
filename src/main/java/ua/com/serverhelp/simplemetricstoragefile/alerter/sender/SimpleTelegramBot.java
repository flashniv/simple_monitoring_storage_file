package ua.com.serverhelp.simplemetricstoragefile.alerter.sender;

import org.json.JSONException;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.utils.httpdriver.HttpDriver;
import ua.com.serverhelp.simplemetricstoragefile.utils.httpdriver.HttpResponse;
import ua.com.serverhelp.simplemetricstoragefile.utils.httpdriver.SimpleHttpDriver;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class for send messages into TG
 */
public class SimpleTelegramBot implements AlertSender { //TODO add tests
    /**
     * Driver for use curl options
     */
    private final HttpDriver httpDriver = new SimpleHttpDriver();
    /**
     * Token for TG flashnivbot
     */
    private String token;
    /**
     * work chat
     */
    private long chatId;

    @Override
    public void initialize(String jsonParams) throws JSONException {
        JSONObject params = new JSONObject(jsonParams);
        token = params.getString("token");
        chatId = params.getLong("chatId");
    }

    /**
     * Function for send message to any chat
     *
     * @param alert for send
     */
    @Override
    public void sendMessage(Alert alert) throws IOException {
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

    }

    private String getText(Alert alert) {
        Trigger trigger = alert.getTrigger();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

        switch (trigger.getLastStatus()) {
            case FAILED:
            case UNCHECKED:
                return "<b>FAIL check trigger failed " + trigger.getName() + "</b>\non event time " + formatter.format(trigger.getLastStatusUpdate());
            case OK:
                return "<b>OK " + trigger.getName() + "</b>\non event time " + formatter.format(trigger.getLastStatusUpdate());
            case ERROR:
                return "<b>ERR " + trigger.getName() + "</b>\non event time " + formatter.format(trigger.getLastStatusUpdate());
        }
        return "";
    }

}