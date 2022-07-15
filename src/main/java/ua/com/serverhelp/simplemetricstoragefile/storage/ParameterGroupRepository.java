package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;

import java.util.List;
import java.util.Optional;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup, Long> {
    @Cacheable(value = "ParameterGroupMetricAndJSON", unless = "#result==null")
    Optional<ParameterGroup> findByMetricAndJson(Metric metric, String json);

    List<ParameterGroup> findByMetric(Metric metric);
}
