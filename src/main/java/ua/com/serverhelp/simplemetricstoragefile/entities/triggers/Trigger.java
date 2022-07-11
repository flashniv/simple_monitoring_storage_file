package ua.com.serverhelp.simplemetricstoragefile.entities.triggers;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Trigger {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Type(type = "text")
    private String description = "";

    private Boolean enabled = true;

    @Column(nullable = false)
    @Type(type = "text")
    private String conf;
}
