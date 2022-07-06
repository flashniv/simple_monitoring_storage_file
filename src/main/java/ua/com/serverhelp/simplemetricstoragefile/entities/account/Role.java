package ua.com.serverhelp.simplemetricstoragefile.entities.account;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;
    @ManyToMany(mappedBy = "roles")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<User> users;
}
