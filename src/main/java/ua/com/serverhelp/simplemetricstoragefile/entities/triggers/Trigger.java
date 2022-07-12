package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
public class Trigger {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Type(type = "text")
    private String description = "";

    @Enumerated(EnumType.STRING)
    private TriggerStatus lastStatus=TriggerStatus.UNCHECKED;
    private Instant lastStatusUpdate=Instant.now();

    private Boolean enabled = true;

    @Column(nullable = false)
    @Type(type = "text")
    private String conf;
}
