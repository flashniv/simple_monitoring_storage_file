package ua.com.serverhelp.simplemetricstoragefile.alerter;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
public class AlertFilter {
    @Id
    @GeneratedValue
    private Long id;
    private String regexp;
    private Boolean allow = true;
    @Type(type = "short")
    private short priority = 100;
    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_channel_id")
    private AlertChannel alertChannel;
}
