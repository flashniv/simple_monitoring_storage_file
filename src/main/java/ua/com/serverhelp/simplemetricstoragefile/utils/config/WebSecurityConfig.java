package ua.com.serverhelp.simplemetricstoragefile.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.formLogin().loginPage("/login").permitAll();
        http.logout().permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/apiv1/metric/**").hasAuthority("Metrics");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/apiv1/metric/collectd/").hasAuthority("Metrics");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/apiv1/metric/exporter/**").hasAuthority("Metrics");
        http.authorizeRequests().antMatchers(HttpMethod.GET,
                "/api/v1/",
                "/api/v1/metric/**",
                "/api/v1/parameterGroup/**",
                "/api/v1/event/**"
        ).hasAuthority("GUI");
        http.authorizeRequests().antMatchers("/error").permitAll();
        http.authorizeRequests().anyRequest().hasAuthority("Administrator");

        return http.build();
    }
}
