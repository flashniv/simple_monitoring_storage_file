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
@RequestMapping("/api/v1/event")
public class EventRest {
    @Autowired
    private FileDriver fileDriver;
    @Autowired
    private ParameterGroupPermissions parameterGroupPermissions;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<DataElement>> getEventsByParameterGroup(
            @RequestParam String metric,
            @RequestParam String parameterGroupJson,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant begin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), metric)) {
            try {
                List<DataElement> dataElements = fileDriver.readMetric(metric + parameterGroupJson, begin, end);
                return ResponseEntity.ok(dataElements);
            } catch (IOException e) {
                throw new NotFoundError("File read error" + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new InternalServerError("Class not found in FileDriver" + e.getMessage());
            }
        }
        throw new AccessDeniedError("Access denied");
    }

    @RequestMapping(value = "/{parameterGroupId}", method = RequestMethod.GET)
    public ResponseEntity<List<DataElement>> getEventsByParameterGroupId(
            @PathVariable Long parameterGroupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant begin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            Authentication authentication
    ) throws NotFoundError, InternalServerError {
        try {
            Optional<ParameterGroup> optionalParameterGroup = parameterGroupRepository.findById(parameterGroupId);
            if (optionalParameterGroup.isPresent()) {
                ParameterGroup parameterGroup = optionalParameterGroup.get();
                if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), parameterGroup.getMetric().getPath())) {
                    List<DataElement> dataElements = fileDriver.readMetric(parameterGroup.getMetric().getPath() + parameterGroup.getJson(), begin, end);
                    return ResponseEntity.ok(dataElements);
                }
                throw new AccessDeniedError("Access denied");
            }
            throw new NotFoundError("Parameter group not found");
        } catch (IOException e) {
            throw new NotFoundError("File read error" + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new InternalServerError("Class not found in FileDriver" + e.getMessage());
        }
    }

}
