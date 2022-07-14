package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.Role;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.User;

import java.util.List;
import java.util.Optional;

class UserRepositoryTest extends AbstractTest {
    @BeforeEach
    void setUp2() {
        Role role = new Role();
        role.setName("Administrator");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("test_user");
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    @Test
    void findByUsername() {
        //setUp2();
        for (int i = 0; i < 100; i++) {
            Optional<User> optionalUser = userRepository.findByUsername("test_user");
            Assertions.assertTrue(optionalUser.isPresent());
            List<Role> roles = optionalUser.get().getRoles();
            Assertions.assertEquals(1, roles.size());
        }
    }
}