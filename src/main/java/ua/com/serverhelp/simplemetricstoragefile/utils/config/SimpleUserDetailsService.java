package ua.com.serverhelp.simplemetricstoragefile.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.SimpleUserDetails;
import ua.com.serverhelp.simplemetricstoragefile.entities.account.User;
import ua.com.serverhelp.simplemetricstoragefile.storage.UserRepository;

import java.util.Optional;

@Service
public class SimpleUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(s);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("SimpleUserDetailsService::loadUserByUsername User " + s + " not found");
        }
        return new SimpleUserDetails(user.get());
    }
}
