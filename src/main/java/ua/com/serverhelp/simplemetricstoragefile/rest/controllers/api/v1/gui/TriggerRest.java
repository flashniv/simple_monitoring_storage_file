package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.alert.Alert;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.AlertRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/trigger")
public class TriggerRest {
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private AlertRepository alertRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Trigger>> getTriggers() throws NotFoundError, InternalServerError {
        return ResponseEntity.ok(triggerRepository.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getTriggerDetails(@PathVariable String id) throws NotFoundError, InternalServerError {
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

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Trigger> updateTrigger(
            @RequestBody Trigger trigger
    ) throws NotFoundError, InternalServerError {
        return ResponseEntity.ok(triggerRepository.save(trigger));
    }
}
