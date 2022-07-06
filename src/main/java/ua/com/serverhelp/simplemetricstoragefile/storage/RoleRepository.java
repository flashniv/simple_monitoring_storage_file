package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
