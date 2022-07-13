package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.filedriver.FileDriver;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadAllValuesOfMetricExpression implements Expression<List<DataElement>>{
    private String metricName;
    private String parameterGroup;

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("metricName", metricName);
        params.put("parameterGroup", parameterGroup);

        res.put("class", this.getClass().getName());
        res.put("parameters", params);

        return res;
    }

    @Override
    public List<DataElement> getValue() throws ExpressionException {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();

            properties.load(inputStream);

            String metricsDirectory = properties.getProperty("metric-storage.metrics-directory");

            FileDriver fileDriver = new FileDriver();
            fileDriver.setDirName(metricsDirectory);

            return fileDriver.readMetric(metricName + parameterGroup);
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
        } catch (JSONException e) {
            throw new ExpressionException("JSON decode error", e);
        }
    }
}