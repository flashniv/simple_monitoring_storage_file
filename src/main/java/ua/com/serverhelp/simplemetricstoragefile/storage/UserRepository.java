package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.User;

public interface UserRepository extends JpaRepository<User,Long> {
    @Cacheable(value = "users")
    User findByUsername(String username);
}
