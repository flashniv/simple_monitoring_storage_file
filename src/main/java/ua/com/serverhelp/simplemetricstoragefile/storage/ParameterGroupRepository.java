package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;

import java.util.List;
import java.util.Optional;

public interface ParameterGroupRepository extends JpaRepository<ParameterGroup,Long> {
    Optional<ParameterGroup> findByMetricAndJson(Metric metric,String json);

    List<ParameterGroup> findByMetric(Metric metric);
}
