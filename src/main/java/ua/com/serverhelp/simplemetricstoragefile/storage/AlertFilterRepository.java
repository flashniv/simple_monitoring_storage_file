package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.alerter.AlertFilter;

public interface AlertFilterRepository extends JpaRepository<AlertFilter,Long> {
}
