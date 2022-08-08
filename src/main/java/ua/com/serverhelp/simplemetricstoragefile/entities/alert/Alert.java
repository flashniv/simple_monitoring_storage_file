package ua.com.serverhelp.simplemetricstoragefile.entities.alert;

import lombok.Data;
import lombok.ToString;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.TriggerStatus;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@ToString
public class Alert {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(optional = false)
    private Trigger trigger;
    private Instant alertTimestamp = Instant.now();
    private String operationData;
    @Enumerated(EnumType.STRING)
    private TriggerStatus triggerStatus;
}
