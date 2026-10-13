package com.citypulse.service;

import com.citypulse.model.User;
import com.citypulse.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(
                    passwordEncoder.encode(user.getPassword())
            );
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        if (user.getActive() == null) {
            user.setActive(true);
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {

        return userRepository.findById(id)
                .map(existingUser -> {

                    existingUser.setFullName(updatedUser.getFullName());
                    existingUser.setEmail(updatedUser.getEmail());

                    if (updatedUser.getPassword() != null
                            && !updatedUser.getPassword().isBlank()) {

                        existingUser.setPassword(
                                passwordEncoder.encode(
                                        updatedUser.getPassword()
                                )
                        );
                    }

                    if (updatedUser.getRole() != null
                            && !updatedUser.getRole().isBlank()) {

                        existingUser.setRole(updatedUser.getRole());
                    }

                    existingUser.setPhone(updatedUser.getPhone());

                    if (updatedUser.getActive() != null) {
                        existingUser.setActive(updatedUser.getActive());
                    }

                    return userRepository.save(existingUser);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        ));
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException(
                    "User not found with id: " + id
            );
        }

        userRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}