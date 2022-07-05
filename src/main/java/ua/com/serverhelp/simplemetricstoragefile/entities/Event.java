package ua.com.serverhelp.simplemetricstoragefile.entities;

import lombok.Data;

import java.time.Instant;

@Data
public class Event{
    private String metric;
    private long timestamp=Instant.now().getEpochSecond();
    private Double value;
}
