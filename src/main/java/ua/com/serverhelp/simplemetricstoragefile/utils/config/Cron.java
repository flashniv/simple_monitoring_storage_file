package ua.com.serverhelp.simplemetricstoragefile.utils.config;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemetricstoragefile.alerter.AlertChannels;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;
import ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter.BlackBoxMetricRest;
import ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter.NodeMetricRest;
import ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric.exporter.ProcessMetricRest;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;
import ua.com.serverhelp.simplemetricstoragefile.utils.maintenance.ClearFileStorageDB;

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
    private BlackBoxMetricRest blackBoxMetricRest;
    @Autowired
    private ProcessMetricRest processMetricRest;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private AlertChannels alertChannels;
    @Autowired
    private ClearFileStorageDB clearFileStorageDB;

    @Scheduled(initialDelay = 60000, fixedDelay = 30000)
    public void storeMetrics() {
        Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
        for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
            try {
                fileDriver.writeMetric(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                log.error("Cron::storeMetrics " + entry.getKey() + " Error " + e.getMessage(), e);
                Sentry.captureException(e);
            }
        }
        log.info("Metrics was store");
    }

    @Scheduled(fixedDelay = 10000)
    public void processNodeMetrics() {
        nodeMetricRest.processItems();
        log.debug("Node metrics processed");
    }

    @Scheduled(fixedDelay = 10000)
    public void processProcessMetrics() {
        processMetricRest.processItems();
        log.debug("Process metrics processed");
    }

    @Scheduled(fixedDelay = 10000)
    public void processBlackBoxMetrics() {
        blackBoxMetricRest.processItems();
        log.debug("Blackbox metrics processed");
    }

    @Scheduled(initialDelay = 60000, fixedDelay = 7200000)
    public void suppressIgnoredTriggers() {
        List<String> triggerIds = alertRepository.getIgnoredAlerts();
        List<Trigger> triggers = triggerRepository.findAllById(triggerIds);
        for (Trigger trigger : triggers) {
            trigger.setSuppressed(true);
            trigger.setSuppressedUpdate(Instant.now());

            log.info("Trigger suppressed " + trigger.getName());
        }
        triggerRepository.saveAll(triggers);
        log.info("Trigger suppressed done");
    }

    @Scheduled(initialDelay = 120000, fixedDelay = 90000)
    public void checkTriggers() {
        List<Trigger> triggers = triggerRepository.findAll();
        for (Trigger trigger : triggers) {
            log.debug("Check trigger " + trigger.getName());

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

                //Sentry.captureException(e);

                if (trigger.getLastStatus() != TriggerStatus.FAILED) {
                    trigger.setLastStatus(TriggerStatus.FAILED);
                    modified = true;
                }
            }

            log.debug("Check trigger result modified=" + modified + " status=" + trigger.getLastStatus() + " failed status=" + checkFailed + " trigger=" + trigger.getName());

            if (modified) {
                trigger.setLastStatusUpdate(Instant.now());
                triggerRepository.save(trigger); //TODO change to save all

                if (!checkFailed) {
                    Alert alert = new Alert();
                    alert.setTrigger(trigger);
                    alert.setTriggerStatus(trigger.getLastStatus());

                    try {
                        if (!trigger.getSuppressed()) {
                            alertChannels.sendAlert(alert);
                        }
                    } catch (Exception e) {
                        log.error("Alert send error", e);
                        Sentry.captureException(e);
                    }

                    alertRepository.save(alert);
                }
            }
        }
        log.info("Triggers checked");
    }

    @Scheduled(fixedDelay = 86400000L, initialDelay = 300000)
    public void clearFileStorageDBRun() {
        clearFileStorageDB.clearFiles();
    }
}
