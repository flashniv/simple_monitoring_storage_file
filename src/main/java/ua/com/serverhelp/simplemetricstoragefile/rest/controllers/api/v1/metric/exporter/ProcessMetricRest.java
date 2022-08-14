package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/process")
public class ProcessMetricRest extends AbstractMetricRest {
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Hostname") String hostname,
            @RequestBody String data
    ) throws InternalServerError {
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");
        Instant timestamp = Instant.now();

        for (String input : inputs) {
            if (isInvalidValidMetric(input)) continue;
            try {
                getInputQueue().add(timestamp + ";exporter." + proj + ".process." + hostname + "." + input);
            } catch (NumberFormatException e) {
                throw new InternalServerError("Number format error" + input);
            } catch (IllegalStateException | IndexOutOfBoundsException e) {
                throw new InternalServerError("regexp match error " + input);
            }
        }

        return ResponseEntity.ok().body("Success");
    }

    @Override
    protected void createTriggerIfNotExist(String path, String params) {
        if (path.matches("exporter.*.process.*.namedprocess_namegroup_num_procs")) {
            String triggerJson = String.format("{\n" +
                    "  \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\n" +
                    "  \"parameters\":{\n" +
                    "    \"operation\":\"==\",\n" +
                    "    \"arg1\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\n" +
                    "          \"parameters\":{\n" +
                    "            \"metricsDirectory\":\"%s\"," +
                    "            \"metricName\":\"%s\",\n" +
                    "            \"parameterGroup\":\"%s\"\n" +
                    "          }\n" +
                    "      },\n" +
                    "      \"arg2\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\n" +
                    "      \"parameters\":{\"value\":1.0}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", dirName, path, params.replace("\"", "\\\""));
            processTrigger(path, params, "Process was done on " + path + params, "Number of process less than 1", TriggerPriority.HIGH, triggerJson);
            String triggerJson2 = String.format("{\n" +
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
                    "      \"parameters\":{\"value\":900.0}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", dirName, path, params.replace("\"", "\\\""));
            processTrigger(path + "15min", params, "Not receive data 15 min on " + path + params, "Data of process not received 15 min", TriggerPriority.HIGH, triggerJson2);
        }

    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
                "namedprocess_namegroup_cpu_seconds_total.*",
                "namedprocess_namegroup_memory_bytes.*",
                "namedprocess_namegroup_num_procs.*",
                "namedprocess_namegroup_oldest_start_time_seconds.*",
                "namedprocess_namegroup_read_bytes_total.*",
                "namedprocess_namegroup_write_bytes_total.*",
                "namedprocess_namegroup_states.*",
                "namedprocess_namegroup_num_threads.*"
        };
    }
}
