package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.ParameterGroupPermissions;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertRepository;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/alert")
public class AlertRest {
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private ParameterGroupPermissions parameterGroupPermissions;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Alert>> getAlerts(
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        List<Alert> allAlerts = alertRepository.findAll(Sort.by("alertTimestamp"));
        List<Alert> resAlerts = new ArrayList<>();

        for (Alert alert : allAlerts) {
            if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), alert.getTrigger().getTriggerId())) {
                resAlerts.add(alert);
            }
        }

        return ResponseEntity.ok(resAlerts);
    }
}