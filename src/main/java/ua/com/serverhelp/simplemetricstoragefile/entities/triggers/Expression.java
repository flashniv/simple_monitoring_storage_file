package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import org.json.JSONObject;

public interface Expression<T> {
    JSONObject getJSON();

    /**
     *
     * @return For type Boolean: "true" if trigger status is ok or "false" if failed
     * @throws ExpressionException throw if any of Expressions in recurse can not get value
     */
    T getValue() throws ExpressionException;

    void initialize(String parametersJson) throws ExpressionException;
}
