package ua.com.serverhelp.simplemetricstoragefile.alerter.sender;

import org.json.JSONException;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SlackAlertSender implements AlertSender {
    private String slackWebHook;
    private String slackChannel;
    private String slackUserName;

    @Override
    public void initialize(String jsonParams) throws JSONException {
        JSONObject params = new JSONObject(jsonParams);
        slackWebHook = params.getString("slackWebHook");
        slackChannel = params.getString("slackChannel");
        slackUserName = params.getString("slackUserName");
    }

    @Override
    public void sendMessage(Alert alert) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("channel", slackChannel);
        payload.put("username", slackUserName);
        payload.put("text", getText(alert));

        // create a client
        HttpClient client = HttpClient.newHttpClient();

        // create a request
        HttpRequest request = HttpRequest.newBuilder(URI.create(slackWebHook))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        // use the client to send the request
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200){
                throw new Exception("Slack response error "+response.statusCode()+" "+response.body());
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private String getText(Alert alert) {
        Trigger trigger = alert.getTrigger();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

        switch (trigger.getLastStatus()) {
            case FAILED:
            case UNCHECKED:
                return "*FAIL: check trigger failed " + trigger.getName() + "*\non event time " + formatter.format(trigger.getLastStatusUpdate());
            case OK:
                return "*OK: " + trigger.getName() + "*\non event time " + formatter.format(trigger.getLastStatusUpdate());
            case ERROR:
                return "*ERR: " + trigger.getName() + "*\non event time " + formatter.format(trigger.getLastStatusUpdate());
        }
        return "";
    }
}
