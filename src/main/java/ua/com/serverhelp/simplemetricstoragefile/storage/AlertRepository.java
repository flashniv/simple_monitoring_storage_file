package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
