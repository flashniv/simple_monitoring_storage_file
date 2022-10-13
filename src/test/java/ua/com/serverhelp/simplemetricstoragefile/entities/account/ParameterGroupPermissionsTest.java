package ua.com.serverhelp.simplemetricstoragefile.entities.account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;

import java.util.Optional;


class ParameterGroupPermissionsTest extends AbstractTest {

    @Test
    void checkParameterGroupPermission() {
        Optional<User> optionalUser = userRepository.findByUsername("org1user");
        Assertions.assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();

        Assertions.assertTrue(parameterGroupPermissions.checkParameterGroupPermission(user, "db.organization1.test.item"));
        Assertions.assertFalse(parameterGroupPermissions.checkParameterGroupPermission(user, "db.organization2.test.item"));
    }
}