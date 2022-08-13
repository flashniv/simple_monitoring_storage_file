package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByTrigger(Trigger trigger);

    @Query(value = "select a.trigger_id from alert as a join trigger as t on t.id=a.trigger_id where a.alert_timestamp>now()-'7 day'\\:\\:interval and not t.suppressed group by a.trigger_id having count(*)>7", nativeQuery = true)
    List<String> getIgnoredAlerts();
}
