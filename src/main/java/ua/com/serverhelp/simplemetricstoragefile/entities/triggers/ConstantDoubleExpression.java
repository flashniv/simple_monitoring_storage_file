package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConstantDoubleExpression implements Expression<Double> {
    private Double value;

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("value", value);

        res.put("class", this.getClass().getName());
        res.put("parameters", params);

        return res;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            JSONObject parameters = new JSONObject(parametersJson);
            value = parameters.getDouble("value");
        } catch (JSONException e) {
            throw new ExpressionException("JSON decode error", e);
        }
    }
}
