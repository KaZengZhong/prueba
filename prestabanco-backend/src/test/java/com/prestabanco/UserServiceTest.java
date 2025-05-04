package com.prestabanco;

import com.prestabanco.entities.UserEntity;
import com.prestabanco.repositories.UserRepository;
import com.prestabanco.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    // Usar constante para evitar hardcodear la contraseña directamente en el código
    private static final String TEST_PASSWORD = System.getProperty("test.password", "test_pwd");

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setRut("12345678-9");
        testUser.setFirstName("Juan");
        testUser.setLastName("Pérez");
        testUser.setEmail("juan.perez@email.com");
        testUser.setPassword(TEST_PASSWORD);
        testUser.setPhoneNumber("+56912345678");
        testUser.setAge(30);
        testUser.setRole(UserEntity.UserRole.CLIENT);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        // Crear una copia del usuario para la prueba
        UserEntity userToSave = new UserEntity();
        userToSave.setEmail(testUser.getEmail());
        userToSave.setPassword(TEST_PASSWORD);

        // Configurar mocks
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encoded_password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Ejecutar
        UserEntity result = userService.createUser(userToSave);

        // Verificar
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());

        // Verificar llamadas
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<UserEntity> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void getUserById_WhenNotExists_ShouldReturnEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userService.getUserById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByEmail_WhenExists_ShouldReturnUser() {
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(testUser));

        Optional<UserEntity> result = userService.getUserByEmail("juan.perez@email.com");

        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void getUserByRut_WhenExists_ShouldReturnUser() {
        when(userRepository.findByRut("12345678-9")).thenReturn(Optional.of(testUser));

        Optional<UserEntity> result = userService.getUserByRut("12345678-9");

        assertTrue(result.isPresent());
        assertEquals(testUser.getRut(), result.get().getRut());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<UserEntity> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        testUser.setPhoneNumber("+56987654321");
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity result = userService.updateUser(testUser);

        assertNotNull(result);
        assertEquals("+56987654321", result.getPhoneNumber());
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_ShouldCallRepository() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("juan.perez@email.com")).thenReturn(true);

        boolean result = userService.existsByEmail("juan.perez@email.com");

        assertTrue(result);
    }

    @Test
    void existsByRut_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByRut("12345678-9")).thenReturn(true);

        boolean result = userService.existsByRut("12345678-9");

        assertTrue(result);
    }

    @Test
    void validateLogin_WhenValidCredentials_ShouldReturnUser() {
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(true);

        UserEntity result = userService.validateLogin("juan.perez@email.com", TEST_PASSWORD);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void validateLogin_WhenIncorrectPassword_ShouldThrowException() {
        String wrongPassword = "wrong" + TEST_PASSWORD;
        when(userRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, testUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                userService.validateLogin("juan.perez@email.com", wrongPassword)
        );
    }



    @Test
    void createUser_WithAllRoles() {
        for (UserEntity.UserRole role : UserEntity.UserRole.values()) {
            testUser.setRole(role);
            when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

            UserEntity result = userService.createUser(testUser);

            assertNotNull(result);
            assertEquals(role, result.getRole());
        }
    }

    @Test
    void updateUser_WithNewEmail_ShouldUpdate() {
        String newEmail = "nuevo.email@email.com";
        testUser.setEmail(newEmail);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity result = userService.updateUser(testUser);

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).save(testUser);
    }
}