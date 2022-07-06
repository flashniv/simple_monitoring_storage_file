package ua.com.serverhelp.simplemetricstoragefile.entities.metric;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ToString
public class Metric {
    @Id
    @Setter
    @Getter
    private String path;
}
