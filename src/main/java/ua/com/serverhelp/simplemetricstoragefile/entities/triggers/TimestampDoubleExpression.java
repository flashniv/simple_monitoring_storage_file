package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.json.JSONObject;

import java.time.Instant;

public class TimestampDoubleExpression  implements Expression<Double>{
    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        JSONObject params = new JSONObject();

        res.put("class", this.getClass().getName());
        res.put("parameters", params);

        return res;
    }

    @Override
    public Double getValue() throws ExpressionException {
        return (double)Instant.now().getEpochSecond();
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {

    }
}
