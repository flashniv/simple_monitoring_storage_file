package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.ParameterGroupPermissions;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.AccessDeniedError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/trigger")
public class TriggerRest {
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private ParameterGroupPermissions parameterGroupPermissions;
    @Autowired
    private AlertRepository alertRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Trigger>> getTriggers(
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        List<Trigger> triggers = triggerRepository.findAll();
        List<Trigger> resTriggers = new ArrayList<>();

        for (Trigger trigger : triggers) {
            System.out.println(trigger.getTriggerId());
            if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), trigger.getTriggerId())) {
                resTriggers.add(trigger);
            }
        }

        return ResponseEntity.ok(resTriggers);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getTriggerDetails(
            @PathVariable String id,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), id)) {

            Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
            if (optionalTrigger.isPresent()) {
                Trigger trigger = optionalTrigger.get();
                JSONObject res = new JSONObject(trigger);
                List<Alert> alerts = alertRepository.findAllByTrigger(trigger);
                res.put("alerts", alerts);
                return ResponseEntity.ok(res.toString());
            }
            throw new NotFoundError("Trigger not found");
        }
        throw new AccessDeniedError("Access denied");
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Trigger> updateTrigger(
            @RequestBody Trigger trigger
    ) throws NotFoundError, InternalServerError {
        return ResponseEntity.ok(triggerRepository.save(trigger));
    }
}
