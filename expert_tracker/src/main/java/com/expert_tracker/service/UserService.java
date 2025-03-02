package com.expert_tracker.service;

import com.expert_tracker.entity.User;
import com.expert_tracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService { 
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
