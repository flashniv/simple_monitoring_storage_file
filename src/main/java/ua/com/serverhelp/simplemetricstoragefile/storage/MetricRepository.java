package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;

public interface MetricRepository extends JpaRepository<Metric,String> {
}
