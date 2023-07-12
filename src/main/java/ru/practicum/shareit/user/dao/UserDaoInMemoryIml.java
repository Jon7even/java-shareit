package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.utils.IdGeneratorUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDaoInMemoryIml {
    private final Map<Long, User> users;
    private final IdGeneratorUser idGenerator;

    public Optional<User> createUser(User user) {
        long idUser = idGenerator.generateId();
        user.setId(idUser);
        users.put(idUser, user);

        return Optional.of(users.get(idUser));
    }


    public Optional<User> findUserById(long idUser) {
        return Optional.ofNullable(users.get(idUser));
    }


    public Optional<User> updateUser(User user) {
        long idUser = user.getId();

        users.put(idUser, user);

        return Optional.of(users.get(idUser));
    }


    public boolean deleteUserById(long idUser) {
        users.remove(idUser);

        return findUserById(idUser).isEmpty();
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    public Optional<User> findUserByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();
    }

}
