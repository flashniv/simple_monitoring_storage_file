package ua.com.serverhelp.simplemetricstoragefile.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event{
    private String metric;
    private String parameters;
    private long timestamp;
    private double value;
}
