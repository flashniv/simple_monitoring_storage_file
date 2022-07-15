package ua.com.serverhelp.simplemetricstoragefile.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataElement implements Serializable {
    private long timestamp;
    private double value;

    public DataElement(Event event) {
        this.timestamp = event.getTimestamp();
        this.value = event.getValue();
    }
}
