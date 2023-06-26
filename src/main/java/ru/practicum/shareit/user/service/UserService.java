package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User findUserById(long idUser);

    User updateUser(User user);

    void deleteUserById(long idUser);

    List<User> getAllUsers();
}
