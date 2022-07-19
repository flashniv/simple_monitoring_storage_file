package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.alerter.AlertChannel;

public interface AlertChannelRepository extends JpaRepository<AlertChannel,Long> {
}
