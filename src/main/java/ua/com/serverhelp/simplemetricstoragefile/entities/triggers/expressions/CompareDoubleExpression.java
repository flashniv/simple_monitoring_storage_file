package ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareDoubleExpression implements Expression<Boolean> {
    private Expression<Double> arg1;
    private Expression<Double> arg2;
    private String operation;

    @Override
    public JSONObject getJSON() {
        JSONObject res = new JSONObject();
        JSONObject params = new JSONObject();

        params.put("arg1", arg1.getJSON());
        params.put("arg2", arg2.getJSON());
        params.put("operation", operation);

        res.put("class", this.getClass().getName());
        res.put("parameters", params);

        return res;
    }

    @Override
    public Boolean getValue() throws ExpressionException {
        if (operation == null) throw new ExpressionException("Operation is null", new Exception());
        switch (operation) {
            case "<":
                return arg1.getValue() < arg2.getValue();
            case ">":
                return arg1.getValue() > arg2.getValue();
            case "<=":
                return arg1.getValue() <= arg2.getValue();
            case ">=":
                return arg1.getValue() >= arg2.getValue();
            case "==":
                return Objects.equals(arg1.getValue(), arg2.getValue());
            case "!=":
                return !Objects.equals(arg1.getValue(), arg2.getValue());
            default:
                throw new ExpressionException("Operation is invalid", new Exception());
        }
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            JSONObject parameters = new JSONObject(parametersJson);
            JSONObject arg1Json = parameters.getJSONObject("arg1");
            JSONObject arg2Json = parameters.getJSONObject("arg2");
            operation = parameters.getString("operation");

            Class<?> arg1Class = Class.forName(arg1Json.getString("class"));
            Expression<Double> arg1 = (Expression<Double>) arg1Class.getConstructor().newInstance();
            arg1.initialize(arg1Json.getJSONObject("parameters").toString());
            setArg1(arg1);

            Class<?> arg2Class = Class.forName(arg2Json.getString("class"));
            Expression<Double> arg2 = (Expression<Double>) arg2Class.getConstructor().newInstance();
            arg2.initialize(arg2Json.getJSONObject("parameters").toString());
            setArg2(arg2);
        } catch (JSONException e) {
            throw new ExpressionException("JSON decode error", e);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new ExpressionException("Class load error", e);
        }
    }
}
