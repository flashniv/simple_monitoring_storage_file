package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
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

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getAddEvent(@RequestParam String path, @RequestParam(defaultValue = "true") Boolean value) {
        memoryMetricsQueue.putEvent(new Event(path, "{}", Instant.now().getEpochSecond(), (value ? 1.0 : 0.0)));
        createTriggerIfNotExist(path, "{}");
        log.debug("DailyBooleanMetricRest::getAddEvent /api/v1/metric/dailyboolean Event add:" + value);

        return ResponseEntity.ok().body("Success");
    }

    private void createTriggerIfNotExist(String path, String params) {
        String id = DigestUtils.md5DigestAsHex((path + params).getBytes());
        Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
        if (optionalTrigger.isEmpty()) {
            Trigger trigger = new Trigger();

            trigger.setId(id);
            trigger.setName("Boolean trigger " + path + params + " receive false");
            trigger.setDescription("Check last value to true or false");
            trigger.setConf(String.format("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ReadLastValueOfMetricExpression\",\"parameters\":{\"metricName\":\"%s\",\"parameterGroup\":\"%s\"}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}", path, params));

            triggerRepository.save(trigger);
        }
        String idDaily = DigestUtils.md5DigestAsHex((path + params + "daily").getBytes());
        Optional<Trigger> optionalDailyTrigger = triggerRepository.findById(idDaily);
        if (optionalDailyTrigger.isEmpty()) {
            Trigger trigger = new Trigger();

            trigger.setId(idDaily);
            trigger.setName("Data not receive 24h on " + path + params);
            trigger.setDescription("Check last value timestamp for 24h age");
            trigger.setConf(String.format("{\n" +
                    "  \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.CompareDoubleExpression\",\n" +
                    "  \"parameters\":{\n" +
                    "    \"operation\":\"<\",\n" +
                    "    \"arg1\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.MathDoubleExpression\",\n" +
                    "      \"parameters\":{\n" +
                    "        \"arg1\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TimestampDoubleExpression\",\n" +
                    "          \"parameters\":{}\n" +
                    "        },\n" +
                    "        \"arg2\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ReadLastTimestampOfMetricExpression\",\n" +
                    "          \"parameters\":{\n" +
                    "            \"metricName\":\"%s\",\n" +
                    "            \"parameterGroup\":\"%s\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"operation\":\"-\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"arg2\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.ConstantDoubleExpression\",\n" +
                    "      \"parameters\":{\"value\":86400.0}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", path, params));

            triggerRepository.save(trigger);
        }
    }

}
