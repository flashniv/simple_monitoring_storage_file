package ua.com.serverhelp.simplemetricstoragefile.alerter;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class AlertChannel {
    @Id
    @GeneratedValue
    private Long id;
    private String alerterClass = "ua.com.serverhelp.simplemetricstoragefile.alerter.sender.SimpleTelegramBot";
    private String alerterParameters = "{}";
    @OneToMany(mappedBy = "alertChannel", fetch = FetchType.EAGER)
    private List<AlertFilter> alertFilters = new ArrayList<>();
}
