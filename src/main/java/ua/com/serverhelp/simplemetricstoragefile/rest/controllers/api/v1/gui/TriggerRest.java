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

        Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
        if (optionalTrigger.isPresent()) {
            Trigger trigger = optionalTrigger.get();
            if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), trigger.getTriggerId())) {
                JSONObject res = new JSONObject(trigger);
                List<Alert> alerts = alertRepository.findAllByTrigger(trigger);
                res.put("alerts", alerts);
                return ResponseEntity.ok(res.toString());
            }
            throw new AccessDeniedError("Access denied");
        }
        throw new NotFoundError("Trigger not found");
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Trigger> updateTrigger(
            @RequestBody Trigger trigger,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), trigger.getTriggerId())) {
            return ResponseEntity.ok(triggerRepository.save(trigger));
        }
        throw new AccessDeniedError("Access denied");
    }

    @RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
    public ResponseEntity<String> deleteAll(
            @RequestBody String[] ids,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        List<Trigger> triggerList = triggerRepository.findAllById(List.of(ids));
        if (triggerList.size() == ids.length) {
            for (Trigger trigger : triggerList) {
                if (!parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), trigger.getTriggerId())) {
                    throw new AccessDeniedError("Access denied");
                }
            }
            for (Trigger trigger : triggerList) {
                List<Alert> alertList=alertRepository.findAllByTrigger(trigger);
                alertRepository.deleteAll(alertList);
                triggerRepository.delete(trigger);
            }
            return ResponseEntity.ok("Success");
        }
        throw new NotFoundError("Not found");
    }

    @RequestMapping(value = "/suppressAll", method = RequestMethod.POST)
    public ResponseEntity<String> suppressAll(
            @RequestParam boolean suppress,
            @RequestBody String[] ids,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        List<Trigger> triggerList = triggerRepository.findAllById(List.of(ids));
        if (triggerList.size() == ids.length) {
            for (Trigger trigger : triggerList) {
                if (!parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), trigger.getTriggerId())) {
                    throw new AccessDeniedError("Access denied");
                }
                trigger.setSuppressed(suppress);
            }
            triggerRepository.saveAll(triggerList);
            return ResponseEntity.ok("Success");
        }
        throw new NotFoundError("Not found");
    }

    @RequestMapping(value = "/enableAll", method = RequestMethod.POST)
    public ResponseEntity<String> enableAll(
            @RequestParam boolean enable,
            @RequestBody String[] ids,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        List<Trigger> triggerList = triggerRepository.findAllById(List.of(ids));
        if (triggerList.size() == ids.length) {
            for (Trigger trigger : triggerList) {
                if (!parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), trigger.getTriggerId())) {
                    throw new AccessDeniedError("Access denied");
                }
                trigger.setEnabled(enable);
            }
            triggerRepository.saveAll(triggerList);
            return ResponseEntity.ok("Success");
        }
        throw new NotFoundError("Not found");
    }
}
