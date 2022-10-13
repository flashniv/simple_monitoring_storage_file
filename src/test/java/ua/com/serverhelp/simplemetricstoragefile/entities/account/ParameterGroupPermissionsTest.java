package ua.com.serverhelp.simplemetricstoragefile.entities.account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemetricstoragefile.AbstractTest;


class ParameterGroupPermissionsTest extends AbstractTest {

    @Test
    void checkParameterGroupPermission() {
        Assertions.assertTrue(parameterGroupPermissions.checkParameterGroupPermission("org1user", "db.organization1.test.item"));
        Assertions.assertFalse(parameterGroupPermissions.checkParameterGroupPermission("org1user", "db.organization2.test.item"));
    }
}