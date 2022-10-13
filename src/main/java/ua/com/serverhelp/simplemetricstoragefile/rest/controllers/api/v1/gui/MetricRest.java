package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.gui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.ParameterGroupPermissions;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;
import ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.AccessDeniedError;
import ua.com.serverhelp.simplemetricstoragefile.rest.exceptions.NotFoundError;
import ua.com.serverhelp.simplemetricstoragefile.storage.MetricRepository;
import ua.com.serverhelp.simplemetricstoragefile.storage.ParameterGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/metric")
@Slf4j
public class MetricRest {
    @Autowired
    private MetricRepository metricRepository;
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;
    @Autowired
    private ParameterGroupPermissions parameterGroupPermissions;
    @Autowired
    private FileDriver fileDriver;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Metric>> getAllMetrics(
            Authentication authentication
    ) {
        List<Metric> metrics = metricRepository.findAll();
        List<Metric> metricsRes = new ArrayList<>();
        for (Metric metric : metrics) {
            if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), metric.getPath())) {
                metricsRes.add(metric);
            }
        }
        return ResponseEntity.ok(metricsRes);
    }

    @RequestMapping(value = "/{id}/parameterGroups", method = RequestMethod.GET)
    public ResponseEntity<List<ParameterGroup>> getParameterGroupsByMetric(
            @PathVariable String id,
            Authentication authentication
    ) throws NotFoundError {
        Optional<Metric> optionalMetric = metricRepository.findById(id);
        if (optionalMetric.isPresent()) {
            if (parameterGroupPermissions.checkParameterGroupPermission(authentication.getName(), optionalMetric.get().getPath())) {
                List<ParameterGroup> parameterGroupList = parameterGroupRepository.findByMetric(optionalMetric.get());
                return ResponseEntity.ok(parameterGroupList);
            }
            throw new AccessDeniedError("Access denied");
        }
        throw new NotFoundError("Metric not found");
    }

    /*@RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
    public ResponseEntity<String> getEventsByMetric(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant begin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) throws NotFoundError {
        Optional<Metric> optionalMetric = metricRepository.findById(id);
        if (optionalMetric.isPresent()) {
            JSONArray res = new JSONArray();
            List<ParameterGroup> parameterGroupList = parameterGroupRepository.findByMetric(optionalMetric.get());
            for (ParameterGroup parameterGroup : parameterGroupList) {
                try {
                    List<DataElement> dataElements = fileDriver.readMetric(optionalMetric.get().getPath() + parameterGroup.getJson(),begin,end);
                    for (DataElement dataElement:dataElements){
                        JSONObject item=new JSONObject();
                        item.put("parameterGroup", parameterGroup.getJson());
                        item.put("timestamp", dataElement.getTimestamp());
                        item.put("value", dataElement.getValue());
                        res.put(item);
                    }
                } catch (IOException e) {
                    throw new NotFoundError("File read error" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new InternalServerError("Class not found in FileDriver" + e.getMessage());
                }
            }
            return ResponseEntity.ok(res.toString());
        }
        throw new NotFoundError("Metric not found");
    }*/
}
