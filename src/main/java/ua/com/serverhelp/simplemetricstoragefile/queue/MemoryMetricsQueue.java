package ua.com.serverhelp.simplemetricstoragefile.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemetricstoragefile.storage.MetricRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class MemoryMetricsQueue {
    @Autowired
    private MetricRepository metricRepository;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;

    private final ConcurrentLinkedQueue<Event> linkedQueue = new ConcurrentLinkedQueue<>();

    public Map<String, List<DataElement>> getFormattedEvents() {
        Map<String, List<DataElement>> events = new HashMap<>();

        while (!linkedQueue.isEmpty()) {
            Event event = linkedQueue.poll();
            addMetricAndParameterGroup(event);
            List<DataElement> dataElements = events.getOrDefault(event.getMetric() + event.getParameters(), new ArrayList<>());
            dataElements.add(new DataElement(event));
            events.put(event.getMetric() + event.getParameters(), dataElements);
        }
        return events;
    }

    private void addMetricAndParameterGroup(Event event) {
        Optional<Metric> optionalMetric = metricRepository.findById(event.getMetric());
        Metric metric = optionalMetric.orElse(new Metric());
        if (optionalMetric.isEmpty()) {
            metric.setPath(event.getMetric());

            metricRepository.save(metric);
            log.debug("Create metric " + event.getMetric());
        }
        Optional<ParameterGroup> optionalParameterGroup = parameterGroupRepository.findByMetricAndJson(metric, event.getParameters());
        if (optionalParameterGroup.isEmpty()) {
            ParameterGroup parameterGroup = new ParameterGroup();
            parameterGroup.setMetric(metric);
            parameterGroup.setJson(event.getParameters());

            parameterGroupRepository.save(parameterGroup);
            log.debug("Create parameter group " + event.getMetric() + " pg " + event.getParameters());
        }
    }

    public void putEvent(Event event) {
        linkedQueue.add(event);
    }
}
