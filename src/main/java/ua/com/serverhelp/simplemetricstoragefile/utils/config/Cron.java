package ua.com.serverhelp.simplemetricstoragefile.utils.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemetricstoragefile.alerter.AlertSender;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter.NodeMetricRest;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.io.IOException;
import java.time.Instant;
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
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private AlertSender alertSender;

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void storeMetrics() {
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            try {
                fileDriver.writeMetric(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                log.error("Cron::storeMetrics " + entry.getKey() + " Error " + e.getMessage(), e);
            }
        }
        log.info("Metrics was store");
    }

    @Scheduled(fixedDelay = 10000)
    public void processNodeMetrics() {
        nodeMetricRest.processItems();
    }

    @Scheduled(initialDelay = 120000, fixedDelay = 60000)
    public void checkTriggers() {
        List<Trigger> triggers = triggerRepository.findAll();
        for (Trigger trigger : triggers) {
            if (!trigger.getEnabled()) continue;
            boolean modified = false;
            boolean checkFailed = true;

            try {
                Boolean status = trigger.checkTrigger();
                checkFailed = false;
                switch (trigger.getLastStatus()) {
                    case UNCHECKED:
                    case FAILED:
                        trigger.setLastStatus(status ? TriggerStatus.OK : TriggerStatus.ERROR);
                        modified = true;
                        break;
                    case OK:
                        if (!status) {
                            trigger.setLastStatus(TriggerStatus.ERROR);
                            modified = true;
                        }
                        break;
                    case ERROR:
                        if (status) {
                            trigger.setLastStatus(TriggerStatus.OK);
                            modified = true;
                        }
                        break;
                }
            } catch (Exception e) {
                log.error("Trigger check error", e);
                if (trigger.getLastStatus() != TriggerStatus.FAILED) {
                    trigger.setLastStatus(TriggerStatus.FAILED);
                    modified = true;
                }
            }
            if (modified) {
                trigger.setLastStatusUpdate(Instant.now());
                triggerRepository.save(trigger); //TODO change to save all

                if(!checkFailed) {
                    Alert alert = new Alert();
                    alert.setTrigger(trigger);

                    try {
                        alertSender.sendMessage(alert);
                    } catch (IOException e) {
                        log.error("Alert send error", e);
                    }

                    alertRepository.save(alert);
                }
            }
        }
        log.info("Triggers checked");
    }
}
