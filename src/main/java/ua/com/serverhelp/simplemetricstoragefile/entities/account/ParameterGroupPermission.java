package ua.com.serverhelp.simplemetricstoragefile.entities.account;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ParameterGroupPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    private String expression;
}
