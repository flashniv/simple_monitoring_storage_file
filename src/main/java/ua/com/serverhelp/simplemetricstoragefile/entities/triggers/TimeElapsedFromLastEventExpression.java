package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.queue.DataElement;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Deprecated(forRemoval = true)
public class TimeElapsedFromLastEventExpression implements Expression<Double>{
    private Expression<List<DataElement>> arg1;

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("arg1", arg1.getJSON());

        res.put("class", this.getClass().getName());
        res.put("parameters", params);

        return res;
    }

    @Override
    public Double getValue() throws ExpressionException {
        List<DataElement> dataElements=arg1.getValue();

        if(dataElements.isEmpty()) throw new ExpressionException("Metric data is empty",new Exception());

        DataElement dataElement=dataElements.get(dataElements.size()-1);
        Instant dataTime=Instant.ofEpochSecond(dataElement.getTimestamp());
        Duration duration=Duration.between(dataTime,Instant.now());

        return (double)duration.getSeconds();
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            JSONObject parameters = new JSONObject(parametersJson);
            JSONObject arg1Json = parameters.getJSONObject("arg1");

            Class<?> arg1Class = Class.forName(arg1Json.getString("class"));
            Expression<List<DataElement>> arg1 = (Expression<List<DataElement>>) arg1Class.getConstructor().newInstance();
            arg1.initialize(arg1Json.getJSONObject("parameters").toString());
            setArg1(arg1);
        } catch (JSONException e) {
            throw new ExpressionException("JSON decode error", e);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new ExpressionException("Class load error", e);
        }

    }
}
