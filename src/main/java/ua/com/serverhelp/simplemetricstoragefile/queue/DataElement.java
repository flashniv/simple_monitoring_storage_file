package ua.com.serverhelp.simplemetricstoragefile.queue;

import lombok.Data;
import ua.com.serverhelp.simplemetricstoragefile.entities.Event;

import java.io.Serializable;

@Data
public class DataElement implements Serializable {
    private long timestamp;
    private Double value;

    public DataElement(Event event) {
        this.timestamp = event.getTimestamp();
        this.value = event.getValue();
    }
}
