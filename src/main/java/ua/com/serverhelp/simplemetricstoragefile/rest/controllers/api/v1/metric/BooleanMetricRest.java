package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/boolean") //TODO fix it
public class BooleanMetricRest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getAddEvent(@RequestParam String path, @RequestParam(defaultValue = "true") Boolean value){
        double val=0;
        if(value){
            val=1;
        }

        memoryMetricsQueue.putEvent(new Event(path,"{}",Instant.now().getEpochSecond(), val));
        log.debug("BooleanMetricRest::getAddEvent /api/v1/metric/boolean Event add:"+value);

        return ResponseEntity.ok().body("Success");
    }
}
