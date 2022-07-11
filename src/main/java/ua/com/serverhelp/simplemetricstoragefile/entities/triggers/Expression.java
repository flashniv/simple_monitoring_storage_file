package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.json.JSONObject;

public interface Expression<T> {
    JSONObject getJSON();

    T getValue();

    void initialize(String parametersJson) throws ExpressionException;
}
