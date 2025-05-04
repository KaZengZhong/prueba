package com.prestabanco.services;

import com.prestabanco.entities.UserEntity;
import com.prestabanco.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void updatePasswordHashes() {
        // Mapa de usuarios conocidos y sus contraseñas
        Map<String, String> knownUsers = Map.of(
                "carlos.rodriguez@prestabanco.com", "admin123",
                "juan.perez@email.com", "password123",
                "maria.gonzalez@email.com", "password456",
                "ana.silva@email.com", "password789",
                "pedro.martinez@prestabanco.com", "exec123"
        );

        // Actualizar cada contraseña en la base de datos
        knownUsers.forEach((email, password) -> {
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println("Contraseña actualizada para: " + email);
            });
        });
    }


    public UserEntity createUser(UserEntity user) {
        // Cifrar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserEntity> getUserByRut(String rut) {
        return userRepository.findByRut(rut);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity updateUser(UserEntity user) {
        Optional<UserEntity> existingUserOpt = userRepository.findById(user.getId());

        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();

            // Si el campo password contiene un valor (no es null ni vacío)
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                // Encriptamos la nueva contraseña siempre, no necesitamos comparar la antigua
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // Si no se está actualizando la contraseña, mantener la que ya existe
                user.setPassword(existingUser.getPassword());
            }
        } else {
            // Si es un usuario nuevo, cifrar la contraseña
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByRut(String rut) {
        return userRepository.existsByRut(rut);
    }

    public UserEntity validateLogin(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        throw new RuntimeException("Contraseña incorrecta");
    }
}