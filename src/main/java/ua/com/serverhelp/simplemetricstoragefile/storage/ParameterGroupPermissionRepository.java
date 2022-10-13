package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.ParameterGroupPermission;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.User;

import java.util.List;

public interface ParameterGroupPermissionRepository extends JpaRepository<ParameterGroupPermission, Long> {
    List<ParameterGroupPermission> findByUser(User user);
}
