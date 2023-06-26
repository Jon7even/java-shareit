package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoInMemoryIml implements UserDao {

    private final Map<Long, User> users;
    private static long generatorId;

    public Optional<User> createUser(User user) {
        long idUser = ++generatorId;
        user.setId(idUser);
        users.put(idUser, user);
        return Optional.of(users.get(idUser));
    }

    public Optional<User> findUserById(long idUser) {
        return Optional.ofNullable(users.get(idUser));
    }

    public Optional<User> updateUser(User user) {
        long idUser = user.getId();

        if (user.getEmail().equals("empty")) {
            User updateUser = users.get(idUser);

            updateUser.setName(user.getName());
            users.put(idUser, updateUser);

        } else if (user.getName().equals("empty")) {
            User updateUser = users.get(idUser);

            updateUser.setEmail(user.getEmail());
            users.put(idUser, updateUser);

        } else {
            users.put(idUser, user);
        }

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

    /*public boolean isExistUserByEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }*/
}
