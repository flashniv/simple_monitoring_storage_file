package ua.com.serverhelp.simplemetricstoragefile.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.Role;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.User;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FileDriver fileDriver;

    @BeforeEach
    void setUp() {
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
        for (int i = 0; i < 100; i++) {
            Optional<User> optionalUser = userRepository.findByUsername("test_user");
            Assertions.assertTrue(optionalUser.isPresent());
            List<Role> roles = optionalUser.get().getRoles();
            Assertions.assertEquals(1, roles.size());
        }
    }
}