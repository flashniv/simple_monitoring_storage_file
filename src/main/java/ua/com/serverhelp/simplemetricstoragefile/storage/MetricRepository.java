package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;

import java.util.Optional;

public interface MetricRepository extends JpaRepository<Metric, String> {
    @Cacheable(value = "Metric", unless = "#result==null")
    Optional<Metric> findById(String id);

    @CacheEvict(value = "Metric", allEntries = true)
    void deleteAll();

    @CacheEvict(value = "Metric")
    Metric save(Metric metric);
}
