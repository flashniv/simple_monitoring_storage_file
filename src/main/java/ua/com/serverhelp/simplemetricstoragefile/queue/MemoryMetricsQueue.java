package ua.com.serverhelp.simplemetricstoragefile.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemetricstoragefile.entities.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class MemoryMetricsQueue{
    private final ConcurrentLinkedQueue<Event> linkedQueue=new ConcurrentLinkedQueue<>();

    public Map<String,List<DataElement>> getFormattedEvents() {
        Map<String,List<DataElement>> events=new HashMap<>();

        while(!linkedQueue.isEmpty()){
            Event event=linkedQueue.poll();
            List<DataElement> dataElements;
            if (events.containsKey(event.getMetric())){
                dataElements=events.get(event.getMetric());
            }else{
                dataElements=new ArrayList<>();
            }
            dataElements.add(new DataElement(event));
            events.put(event.getMetric(), dataElements);
        }
        return events;
    }

    public void putEvent(Event event){
        linkedQueue.add(event);
    }
}