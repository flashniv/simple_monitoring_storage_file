package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractMetricRest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    protected TriggerRepository triggerRepository;
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
        createTriggerIfNotExist(parts[1], parseParameterGroup(parts[2]));
    }

    protected abstract void createTriggerIfNotExist(String path, String params);

    protected void processTrigger(String path, String params, String triggerName, String triggerDescription, TriggerPriority triggerPriority, String triggerJson) {
        String id = DigestUtils.md5DigestAsHex((path + params).getBytes());
        Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
        if (optionalTrigger.isEmpty()) {
            Trigger trigger = new Trigger();

            trigger.setId(id);
            trigger.setName(triggerName);
            trigger.setDescription(triggerDescription);
            trigger.setPriority(triggerPriority);
            trigger.setConf(triggerJson);

            triggerRepository.save(trigger);
            log.debug("Trigger was created "+triggerName+" path "+path+" params "+params+" prio "+triggerPriority.name()+" JSON "+triggerJson);
        }

    }

    protected boolean isInvalidValidMetric(String input) {
        if (input.charAt(0) == '#') {
            return true;
        }
        String[] allowedMetrics = getAllowedMetrics();
        for (String metricRegexp : allowedMetrics) {
            if (input.matches(metricRegexp)) {
                return false;
            }
        }

        return true;
    }

    protected String[] getAllowedMetrics() {
        return new String[]{".*"};
    }
}
