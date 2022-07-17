package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/parameterGroup")
public class ParameterGroupRest {
    @Autowired
    private FileDriver fileDriver;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;

    @RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
    public ResponseEntity<List<DataElement>> getEventsByParameterGroup(
            @PathVariable Long id
    ) throws NotFoundError, InternalServerError {
        Optional<ParameterGroup> optionalParameterGroup = parameterGroupRepository.findById(id);
        if (optionalParameterGroup.isPresent()) {
            ParameterGroup parameterGroup = optionalParameterGroup.get();
            try {
                List<DataElement> dataElements = fileDriver.readMetric(parameterGroup.getMetric().getPath() + parameterGroup.getJson());
                return ResponseEntity.ok(dataElements);
            } catch (IOException e) {
                throw new NotFoundError("File read error" + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new InternalServerError("Class not found in FileDriver" + e.getMessage());
            }
        }
        throw new NotFoundError("Parameter group not found");
    }
}
