package ua.com.serverhelp.simplemetricstoragefile.entities.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupPermissionRepository;

import java.util.List;

@Service
public class ParameterGroupPermissions {
    @Autowired
    private ParameterGroupPermissionRepository parameterGroupPermissionRepository;

    public boolean checkParameterGroupPermission(User user, String parameterGroupFullPath) {
        List<ParameterGroupPermission> parameterGroupPermissions = parameterGroupPermissionRepository.findByUser(user);
        for (ParameterGroupPermission parameterGroupPermission : parameterGroupPermissions) {
            if (parameterGroupFullPath.matches(parameterGroupPermission.getExpression())) return true;
        }
        return false;
    }
}
