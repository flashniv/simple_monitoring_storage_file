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
    private String triggerId;

    @Column(nullable = false)
    private String name;

    @Type(type = "text")
    private String description = "";

    @Enumerated(EnumType.STRING)
    private TriggerStatus lastStatus = TriggerStatus.UNCHECKED;
    @Enumerated(EnumType.STRING)
    private TriggerPriority priority = TriggerPriority.NOT_CLASSIFIED;
    private Instant lastStatusUpdate = Instant.now();

    private Boolean enabled = true;
    private Boolean suppressed = false;
    @Column(name = "suppressedupdate")
    private Instant suppressedUpdate;

    @Column(nullable = false)
    @Type(type = "text")
    private String conf;

    public Boolean checkTrigger() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassCastException, ExpressionException {
        JSONObject confJson = new JSONObject(conf);

        String className = confJson.getString("class");
        Class<?> classType = Class.forName(className);
        Expression<Boolean> expression = (Expression<Boolean>) classType.getConstructor().newInstance();
        expression.initialize(confJson.getJSONObject("parameters").toString());

        return expression.getValue();
    }
}
