package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/node")
public class NodeMetricRest extends AbstractMetricRest {
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Hostname") String hostname,
            @RequestBody String data
    ) {
        Instant timestamp = Instant.now();
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");

        for (String input : inputs) {
            if (isInvalidValidMetric(input)) continue;
            try {
                getInputQueue().add(timestamp + ";exporter." + proj + "." + hostname + ".node." + input.replace("node_", ""));
            } catch (NumberFormatException e) {
                log.warn("NodeMetricRest::receiveData number format error " + input);
                return ResponseEntity.badRequest().body("number format error " + input);
            } catch (IllegalStateException | IndexOutOfBoundsException e) {
                log.warn("NodeMetricRest::receiveData regexp match error " + input);
                return ResponseEntity.badRequest().body("regexp match error " + input);
            }
        }

        return ResponseEntity.ok().body("Success");
    }

    @Override
    protected void createTriggerIfNotExist(String path, String params) {
        if (path.matches("exporter.*node.load15")) {
            String triggerJson = String.format("{\n" +
                    "  \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\n" +
                    "  \"parameters\":{\n" +
                    "    \"operation\":\"<\",\n" +
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
                    "      \"parameters\":{\"value\":5.0}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", dirName, path, params);
            processTrigger(path, params, "Load average too high on " + path, "Load avg 15 greater than 5", TriggerPriority.HIGH, triggerJson);
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
                    "}\n", dirName, path, params);
            processTrigger(path + "15min", params, "Not receive data 15 min on " + path, "Load avg 15 not received 15 min", TriggerPriority.HIGH, triggerJson2);
        }
        if (path.matches("exporter.*.node.filesystem_size_bytes")) {
            String triggerJson = String.format("{\n" +
                    "  \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\n" +
                    "  \"parameters\":{\n" +
                    "    \"operation\":\">\",\n" +
                    "    \"arg1\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.MathDoubleExpression\",\n" +
                    "      \"parameters\":{\n" +
                    "        \"arg1\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\n" +
                    "          \"parameters\":{\n" +
                    "            \"metricsDirectory\":\"%s\"," +
                    "            \"metricName\":\"%s\",\n" +
                    "            \"parameterGroup\":\"%s\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"arg2\":{\n" +
                    "          \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\n" +
                    "          \"parameters\":{\n" +
                    "            \"metricsDirectory\":\"%s\"," +
                    "            \"metricName\":\"%s\",\n" +
                    "            \"parameterGroup\":\"%s\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"operation\":\"/\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"arg2\":{\n" +
                    "      \"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\n" +
                    "      \"parameters\":{\"value\":0.15}\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", dirName, path.replace("filesystem_size_bytes", "filesystem_avail_bytes"), params.replace("\"", "\\\""), dirName, path, params.replace("\"", "\\\""));
            processTrigger(path, params, "Free disk space less than 15% on " + path.replace(".filesystem_size_bytes", "") + params, "Free disk space too low", TriggerPriority.HIGH, triggerJson);
        }
    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
                ".*node_load1.*",
                ".*node_load5.*",
                ".*node_load15.*",
                ".*node_memory_MemAvailable_bytes.*",
                ".*node_memory_MemTotal_bytes.*",
                ".*node_cpu_seconds_total.*",
                ".*node_filesystem_avail_bytes.*",
                ".*node_filesystem_size_bytes.*",
                ".*node_filesystem_files.*",
                ".*node_vmstat_pswp.*",
                ".*node_memory_Swap.*",
                ".*node_network_transmit_bytes_total.*",
                ".*node_network_receive_bytes_total.*"
        };
    }

}
