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
@RequestMapping("/apiv1/metric/dailyboolean")    //TODO fix it
public class DailyBooleanMetricRest {
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
        createTriggerIfNotExist(path, triggerName);
        log.debug("DailyBooleanMetricRest::getAddEvent /api/v1/metric/dailyboolean Event add:" + value);

        return ResponseEntity.ok().body("Success");
    }

    private void createTriggerIfNotExist(String path, String triggerName) {
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
        String idDaily = DigestUtils.md5DigestAsHex((path + "{}" + "daily").getBytes());
        Optional<Trigger> optionalDailyTrigger = triggerRepository.findById(idDaily);
        if (optionalDailyTrigger.isEmpty()) {
            Trigger trigger = new Trigger();

            trigger.setId(idDaily);
            trigger.setTriggerId(path + "{}" + ".daily");
            trigger.setName("Data not receive 24h on " + path);
            trigger.setDescription("Check last value timestamp for 24h age");
            trigger.setPriority(TriggerPriority.HIGH);
            trigger.setConf(String.format("{\n" +
                    "  \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\n" +
                    "  \"parameters\":{\n" +
                    "    \"operation\":\"<\",\n" +
                    "    \"arg1\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.MathDoubleExpression\",\n" +
                    "      \"parameters\":{\n" +
                    "        \"arg1\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.TimestampDoubleExpression\",\n" +
                    "          \"parameters\":{}\n" +
                    "        },\n" +
                    "        \"arg2\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastTimestampOfMetricExpression\",\n" +
                    "          \"parameters\":{\n" +
                    "            \"metricsDirectory\":\"%s\"," +
                    "            \"metricName\":\"%s\",\n" +
                    "            \"parameterGroup\":\"%s\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"operation\":\"-\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"arg2\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\n" +
                    "      \"parameters\":{\"value\":90000.0}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", dirName, path, "{}"));

            triggerRepository.save(trigger);
        }
    }

}
