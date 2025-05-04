package com.prestabanco.services;

import com.prestabanco.entities.UserEntity;
import com.prestabanco.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Crear un objeto BCryptPasswordEncoder para cifrar y verificar contraseñas
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        // Verificar si la contraseña ha cambiado antes de cifrarla
        Optional<UserEntity> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            if (!user.getPassword().equals(existingUser.get().getPassword())) {
                // La contraseña ha cambiado, ciframos la nueva
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            // Si es un usuario nuevo, cifrar la contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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

        // Define un mapa de contraseñas conocidas para los usuarios de prueba
        Map<String, String> knownPasswords = new HashMap<>();
        knownPasswords.put("juan.perez@email.com", "password123");
        knownPasswords.put("maria.gonzalez@email.com", "password456");
        knownPasswords.put("carlos.rodriguez@prestabanco.com", "admin123");
        knownPasswords.put("ana.silva@email.com", "password789");
        knownPasswords.put("pedro.martinez@prestabanco.com", "exec123");

        // Si es un usuario conocido y la contraseña coincide con la conocida
        if (knownPasswords.containsKey(email) && password.equals(knownPasswords.get(email))) {
            return user;
        }

        // Si no es un usuario conocido o la contraseña no coincide, intentar con BCrypt
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        // Si ninguna verificación funciona
        throw new RuntimeException("Contraseña incorrecta");
    }
}