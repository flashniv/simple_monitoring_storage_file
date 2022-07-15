package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.Expression;
import ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ExpressionException;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;

@Entity
@Data
public class Trigger {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Type(type = "text")
    private String description = "";

    @Enumerated(EnumType.STRING)
    private TriggerStatus lastStatus = TriggerStatus.UNCHECKED;
    private Instant lastStatusUpdate = Instant.now();

    private Boolean enabled = true;

    @Column(nullable = false)
    @Type(type = "text")
    private String conf;

    //{"class":"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.LessThanDoubleExpression","parameters":{"arg2":{"class":"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression","parameters":{"metricName":"test.stage.db.booleanitem1","parameterGroup":"{}"}},"arg1":{"class":"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression","parameters":{"value":0.5}}}}
    public Boolean checkTrigger() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassCastException, ExpressionException {
        JSONObject confJson = new JSONObject(conf);

        String className = confJson.getString("class");
        Class<?> classType = Class.forName(className);
        Expression<Boolean> expression = (Expression<Boolean>) classType.getConstructor().newInstance();
        expression.initialize(confJson.getJSONObject("parameters").toString());

        return expression.getValue();
    }
}
