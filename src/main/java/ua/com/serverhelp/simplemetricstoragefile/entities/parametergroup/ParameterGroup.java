package ua.com.serverhelp.simplemetricstoragefile.entities.parametergroup;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.json.JSONObject;
import ua.com.serverhelp.simplemetricstoragefile.entities.metric.Metric;

import javax.persistence.*;
import java.util.HashMap;

@Entity
@ToString
public class ParameterGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter
    @Setter
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "metric_id")
    @Getter
    @Setter
    private Metric metric;
    @Getter
    @Setter
    @Type(type = "text")
    private String json = "{}";

    public HashMap<String, String> getParameters() {
        HashMap<String, String> hashMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject(json);
        for (String key : jsonObject.keySet()) {
            hashMap.put(key, jsonObject.getString(key));
        }
        return hashMap;
    }
}
