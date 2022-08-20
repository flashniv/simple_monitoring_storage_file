package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.InternalServerError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/event")
public class EventRest {
    @Autowired
    private FileDriver fileDriver;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<DataElement>> getEventsByParameterGroup(
            @RequestParam String metric,
            @RequestParam String parameterGroupJson,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant begin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) throws NotFoundError, InternalServerError {
        try {
            List<DataElement> dataElements = fileDriver.readMetric(metric + parameterGroupJson, begin, end);
            return ResponseEntity.ok(dataElements);
        } catch (IOException e) {
            throw new NotFoundError("File read error" + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new InternalServerError("Class not found in FileDriver" + e.getMessage());
        }
    }

}
