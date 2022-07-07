package ua.com.serverhelp.simplemetricstoragefile.rest.controllers.api.v1.metric;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemetricstoragefile.entities.event.Event;
import ua.com.serverhelp.simplemetricstoragefile.queue.MemoryMetricsQueue;

import java.time.Instant;

@RestController
@RequestMapping("/apiv1/metric/collectd")   //TODO fix it
public class CollectDMetricRest {
    @Autowired
    private MemoryMetricsQueue memoryMetricsQueue;
    /*
{
  "values":[0,0],
  "dstypes":["derive","derive"],
  "dsnames":["rx","tx"],
  "time":1630108891.967,
  "interval":90.000,
  "host":"s-kvm2",
  "plugin":"interface",
  "plugin_instance":"vnet1",
  "type":"if_dropped",
  "type_instance":""
}
{
  "values":[0.381944927576563],
  "dstypes":["gauge"],
  "dsnames":["value"],
  "time":1630108891.967,
  "interval":90.000,
  "host":"s-kvm2",
  "plugin":"cpu",
  "plugin_instance":"",
  "type":"percent",
  "type_instance":"wait"
}*/
    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestBody String data
    ){
        JSONArray jsonArray=new JSONArray(data);
        String host="";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject value = jsonArray.getJSONObject(i);
            Instant timestamp=Instant.now(); //Instant.ofEpochSecond ((long) (value.getDouble("time"))); //TODO release diff
            host = value.getString("host");
            String plugin = value.getString("plugin");

            String pluginInstance = value.getString("plugin_instance");
            String type = value.getString("type");
            String typeInstance = value.getString("type_instance");
            JSONArray dsnames = value.getJSONArray("dsnames");
            JSONArray dstypes = value.getJSONArray("dstypes");
            JSONArray values = value.getJSONArray("values");
            String path = "collectd." + proj + "." + host + "." + plugin;

            for (int j = 0; j < dsnames.length(); j++) {
                JSONObject parameters=new JSONObject();
                if(!pluginInstance.isEmpty()){
                    parameters.put("instance", pluginInstance);
                }
                if(!type.isEmpty()){
                    parameters.put("type", type);
                }
                if(!typeInstance.isEmpty()){
                    parameters.put("type_instance", typeInstance);
                }
                parameters.put("ds_name", dsnames.getString(j));
                parameters.put("ds_type", dstypes.getString(j));
                Double dobValue= values.getDouble(j);
                Event event=new Event(path,parameters.toString(), timestamp.getEpochSecond(), dobValue);
                memoryMetricsQueue.putEvent(event);
            }
        }

        return ResponseEntity.ok().body("Success");
    }
}
