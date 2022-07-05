package ua.com.serverhelp.simplemetricstoragefile.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemetricstoragefile.entities.Event;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;

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

    @Scheduled(fixedDelay = 1000)
    public void generateEvents() {
        String[] metrics={"aa", "bb", "cc", "dd"};
        Event event=new Event();
        event.setValue(Math.random());
        event.setMetric(metrics[getRandomNumber(0, 3)]);
        memoryMetricsQueue.putEvent(event);
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Scheduled(fixedDelay = 60000)
    public void storeMetrics(){
        try {
            Map<String, List<DataElement>> map = memoryMetricsQueue.getFormattedEvents();
            for (Map.Entry<String, List<DataElement>> entry : map.entrySet()) {
                fileDriver.writeMetric(entry.getKey(), entry.getValue());
            }
        }catch (IOException e){
            log.error(e.getMessage(), e);
        }
    }
}
