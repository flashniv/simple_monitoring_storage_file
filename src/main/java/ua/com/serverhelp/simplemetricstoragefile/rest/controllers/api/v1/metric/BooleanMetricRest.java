package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/boolean") //TODO fix it
public class BooleanMetricRest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private TriggerRepository triggerRepository;
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getAddEvent(
            @RequestParam String path,
            @RequestParam(defaultValue = "Boolean trigger on %s") String triggerName,
            @RequestParam(defaultValue = "true") Boolean value
    ) {
        memoryMetricsQueue.putEvent(new Event(path, "{}", Instant.now().getEpochSecond(), (value ? 1.0 : 0.0)));
        createTriggerIfNotExist(path,triggerName);
        log.debug("BooleanMetricRest::getAddEvent /api/v1/metric/boolean Event add:" + value);

        return ResponseEntity.ok().body("Success");
    }

    private void createTriggerIfNotExist(String path,String triggerName) {
        String id = DigestUtils.md5DigestAsHex((path + "{}").getBytes());
        Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
        if (optionalTrigger.isEmpty()) {
            Trigger trigger = new Trigger();

            trigger.setId(id);
            trigger.setTriggerId(path);
            trigger.setName(String.format(triggerName, path));
            trigger.setDescription("Check last value to true or false");
            trigger.setPriority(TriggerPriority.HIGH);
            trigger.setConf(String.format("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\"parameters\":{\"metricsDirectory\":\"%s\",\"metricName\":\"%s\",\"parameterGroup\":\"%s\"}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}", dirName, path, "{}"));

            triggerRepository.save(trigger);
        }
    }
}
