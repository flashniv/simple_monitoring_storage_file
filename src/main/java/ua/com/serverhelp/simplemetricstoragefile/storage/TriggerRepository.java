package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;

public interface TriggerRepository extends JpaRepository<Trigger, String> {

}
