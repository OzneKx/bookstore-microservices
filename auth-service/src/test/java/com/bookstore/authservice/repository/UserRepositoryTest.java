package com.bookstore.authservice.repository;

import com.bookstore.authservice.data.entity.User;
import com.bookstore.authservice.data.enums.Role;
import com.bookstore.authservice.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Kenzo");
        user.setEmail("kenzoalbuqk@gmail.com");
        user.setPassword("123456");
        user.setRole(Role.ROLE_USER);
    }

    @Test
    void shouldSaveUserSuccessfully() {
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        userRepository.save(user);
        Optional<User> userFound = userRepository.findByEmail("kenzoalbuqk@gmail.com");
        assertThat(userFound).isPresent();
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        userRepository.save(user);
        assertThat(userRepository.existsByEmail("kenzoalbuqk@gmail.com")).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        assertThat(userRepository.findByEmail("notfound@email.com")).isEmpty();
    }
}
