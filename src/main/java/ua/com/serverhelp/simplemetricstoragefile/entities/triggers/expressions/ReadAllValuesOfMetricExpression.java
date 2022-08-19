package ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadAllValuesOfMetricExpression implements Expression<List<DataElement>> {
    private String metricName;
    private String parameterGroup;
    private String metricsDirectory;

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("metricName", metricName);
        params.put("parameterGroup", parameterGroup);
        params.put("metricsDirectory", metricsDirectory);

        res.put("class", this.getClass().getName());
        res.put("parameters", params);

        return res;
    }

    @Override
    public List<DataElement> getValue() throws ExpressionException {
        try {
            FileDriver fileDriver = new FileDriver();
            fileDriver.setDirName(metricsDirectory);

            return fileDriver.readMetric(metricName + parameterGroup, Instant.ofEpochSecond(1),Instant.now());
        } catch (Exception e) {
            throw new ExpressionException("Load metric " + metricName + parameterGroup + " error", e);
        }
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            JSONObject parameters = new JSONObject(parametersJson);
            metricName = parameters.getString("metricName");
            parameterGroup = parameters.getString("parameterGroup");
            metricsDirectory = parameters.getString("metricsDirectory");
        } catch (JSONException e) {
            throw new ExpressionException("JSON decode error", e);
        }
    }
}
