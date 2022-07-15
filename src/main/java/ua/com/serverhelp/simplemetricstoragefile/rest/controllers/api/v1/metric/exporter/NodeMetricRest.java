package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/node")
public class NodeMetricRest extends AbstractMetricRest {
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
