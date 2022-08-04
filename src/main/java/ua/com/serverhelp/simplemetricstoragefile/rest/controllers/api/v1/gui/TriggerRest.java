package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.Trigger;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.TriggerRepository;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/trigger")
public class TriggerRest {
    @Autowired
    private TriggerRepository triggerRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Trigger>> getTriggers() throws NotFoundError, InternalServerError {
        return ResponseEntity.ok(triggerRepository.findAll());
    }
}
