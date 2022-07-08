package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable(value = "users")
    Optional<User> findByUsername(String username);
}
