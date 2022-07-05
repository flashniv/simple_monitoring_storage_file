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
    private final String[] metrics={"aa", "bb", "cc", "dd"};

    @Scheduled(fixedDelay = 1000)
    public void generateEvents() {
        Event event=new Event();
        event.setValue(Math.random());
        event.setMetric(metrics[getRandomNumber(0, 4)]);
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
            log.error("Cron::storeMetrics Error "+e.getMessage(), e);
        }
    }
    @Scheduled(fixedDelay = 5000)
    public void readMetrics() {
        for (String metric:metrics){
            try {
                List<DataElement> dataElements=fileDriver.readFile(metric);
//                dataElements.forEach(dataElement -> {
//                    System.out.println(dataElement);
//                });
            }catch (IOException e){
                log.error("Cron::readMetrics "+metric+" Error "+e.getMessage(), e);
            }catch (ClassNotFoundException e){
                log.error("Cron::readMetrics "+metric+" Class Not Found "+e.getMessage(), e);
            }
        }
    }
}
