package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractMetricRest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Getter
    private final ConcurrentLinkedQueue<String> inputQueue = new ConcurrentLinkedQueue<>();
    private final Pattern replaceE = Pattern.compile("(.*[0-9]e) ([0-9]+)$");
    private final Pattern parametersSplitToGroup = Pattern.compile("(.*)=\"(.*)\"");

    private String parseParameterGroup(String part) throws IllegalStateException, IndexOutOfBoundsException {
        JSONObject json = new JSONObject();
        String[] parameters = part.split(",");
        for (String parameter : parameters) {
            Matcher matcher = parametersSplitToGroup.matcher(parameter);
            if (matcher.matches()) {
                json.put(matcher.group(1), matcher.group(2));
            }
        }
        return json.toString();
    }

    public void processItems() {
        while (!inputQueue.isEmpty()) {
            processItem(inputQueue.poll());
        }
    }

    private void processItem(String input) throws IllegalStateException, IndexOutOfBoundsException, NumberFormatException {
        input = input.replace("\r", "");
        input = replaceE.matcher(input).replaceFirst("$1+$2");
        if (input.contains("{")) {
            input = input.replace('{', ';').replace("} ", ";");
        } else {
            input = input.replace(" ", ";;");
        }
        String[] parts = input.split(";");
        //create response container
        Event event = new Event(parts[1], parseParameterGroup(parts[2]), Instant.parse(parts[0]).getEpochSecond(), Double.parseDouble(parts[3]));
        memoryMetricsQueue.putEvent(event);
    }
}
