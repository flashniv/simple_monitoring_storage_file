package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1/metric/double")
public class DoubleMetricRest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private TriggerRepository triggerRepository;
    @Value("${metric-storage.metrics-directory}")
    private String dirName;

    @GetMapping("/")
    public ResponseEntity<String> getAddEvent(
            @RequestParam String path,
            @RequestParam Double value
    ) {
        memoryMetricsQueue.putEvent(new Event(path, "{}", Instant.now().getEpochSecond(), value));
        log.debug("DoubleMetricRest::getAddEvent /api/v1/metric/double Event add:" + value);

        return ResponseEntity.ok().body("Success");
    }

}
