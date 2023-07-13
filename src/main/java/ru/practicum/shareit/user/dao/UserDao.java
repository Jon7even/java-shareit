package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.entity.User;

import java.util.Optional;

@Component
public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findUserByEmailContainingIgnoreCase(String emailSearch);
}
