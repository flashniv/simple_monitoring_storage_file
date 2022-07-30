package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class HealthCheckRest {
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ResponseEntity<String> getOkResponse(){
        return ResponseEntity.ok("{}");
    }
}
