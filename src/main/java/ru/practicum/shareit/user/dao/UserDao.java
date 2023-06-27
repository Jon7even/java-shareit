package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserUpdateInRepositoryDTO;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> createUser(User user);

    Optional<User> findUserById(long idUser);

    Optional<User> updateUser(UserUpdateInRepositoryDTO userUpdateInRepositoryDTO);

    boolean deleteUserById(long idUser);

    List<User> getAllUsers();

    Optional<User> findUserByEmail(String email);
}
