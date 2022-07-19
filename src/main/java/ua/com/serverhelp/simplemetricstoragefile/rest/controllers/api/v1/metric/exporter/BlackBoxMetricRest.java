package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerPriority;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@RestController
@RequestMapping("/apiv1/metric/exporter/blackbox")
public class BlackBoxMetricRest extends AbstractMetricRest {
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Site-Id") String siteId,
            @RequestBody String data
    ) throws InternalServerError {
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");
        Instant timestamp = Instant.now();

        for (String input : inputs) {
            if (isInvalidValidMetric(input)) continue;
            try {
                getInputQueue().add(timestamp + ";exporter." + proj + ".blackbox." + siteId + "." + input);
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
        if (path.matches("exporter.*.blackbox.*.probe_success")) {
            processTrigger(
                    path,
                    params,
                    "Web check " + path,
                    "Blackbox webcheck",
                    TriggerPriority.HIGH,
                    String.format("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\"parameters\":{\"metricsDirectory\":\"%s\",\"metricName\":\"%s\",\"parameterGroup\":\"%s\"}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}", dirName, path, params)
            );
        }
    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
                ".*probe_success.*",
                ".*probe_http_ssl.*",
                ".*probe_http_status_code.*",
                ".*probe_duration_seconds.*"
        };
    }
}
