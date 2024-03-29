package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.ParameterGroupPermissions;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.AccessDeniedError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/parameterGroup")
public class ParameterGroupRest {
    @Autowired
    private FileDriver fileDriver;
    @Autowired
    private ParameterGroupPermissions parameterGroupPermissions;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;

    @RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
    public ResponseEntity<List<DataElement>> getEventsByParameterGroup(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant begin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @PathVariable Long id,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        Optional<ParameterGroup> optionalParameterGroup = parameterGroupRepository.findById(id);
        if (optionalParameterGroup.isPresent()) {
            ParameterGroup parameterGroup = optionalParameterGroup.get();
            try {
                if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), parameterGroup.getMetric().getPath())) {
                    List<DataElement> dataElements = fileDriver.readMetric(parameterGroup.getMetric().getPath() + parameterGroup.getJson(), begin, end);
                    return ResponseEntity.ok(dataElements);
                }
                throw new AccessDeniedError("Access denied");
            } catch (IOException e) {
                throw new NotFoundError("File read error" + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new InternalServerError("Class not found in FileDriver" + e.getMessage());
            }
        }
        throw new NotFoundError("Parameter group not found");
    }
}
