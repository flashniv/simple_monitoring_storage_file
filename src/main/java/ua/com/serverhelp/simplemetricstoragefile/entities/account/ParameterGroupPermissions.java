package ua.com.serverhelp.simplemetricstoragefile.entities.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupPermissionRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParameterGroupPermissions {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParameterGroupPermissionRepository parameterGroupPermissionRepository;

    public boolean checkParameterGroupPermission(String userName, String parameterGroupFullPath) {
        Optional<User> user = userRepository.findByUsername(userName);
        if (user.isEmpty()) return false;
        List<ParameterGroupPermission> parameterGroupPermissions = parameterGroupPermissionRepository.findByUser(user.get());
        for (ParameterGroupPermission parameterGroupPermission : parameterGroupPermissions) {
            if (parameterGroupFullPath.matches(parameterGroupPermission.getExpression())) return true;
        }
        return false;
    }
}
