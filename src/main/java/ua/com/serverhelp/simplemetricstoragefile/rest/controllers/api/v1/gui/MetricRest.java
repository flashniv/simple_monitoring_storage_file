package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.storage.MetricRepository;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/metric")
public class MetricRest {
    @Autowired
    private MetricRepository metricRepository;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ResponseEntity<List<Metric>> getAllMetrics(){
        List<Metric> metrics=metricRepository.findAll();
        return ResponseEntity.ok(metrics);
    }
}
