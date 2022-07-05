package ua.com.serverhelp.simplemetricstoragefile.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter.NodeMetricRest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Cron {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    @Autowired
    private FileDriver fileDriver;
    @Autowired
    private NodeMetricRest nodeMetricRest;

    @Scheduled(fixedDelay = 60000)
    public void storeMetrics() {
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            try {
                fileDriver.writeMetric(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                log.error("Cron::storeMetrics "+entry.getKey()+" Error " + e.getMessage(), e);
            }
        }
        log.info("Metrics was store");
    }

    @Scheduled(fixedDelay = 10000)
    public void processNodeMetrics(){
        nodeMetricRest.processItems();
    }
}
