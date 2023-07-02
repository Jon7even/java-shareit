package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserUpdateInRepositoryDTO;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.MapperUserDTO;
import ru.practicum.shareit.user.utils.IdGeneratorUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoInMemoryIml implements UserDao {
    private final Map<Long, User> users;
    private final IdGeneratorUser idGenerator;

    @Override
    public Optional<User> createUser(User user) {
        long idUser = idGenerator.generateId();
        user.setId(idUser);
        users.put(idUser, user);

        return Optional.of(users.get(idUser));
    }

    @Override
    public Optional<User> findUserById(long idUser) {
        return Optional.ofNullable(users.get(idUser));
    }

    @Override
    public Optional<User> updateUser(UserUpdateInRepositoryDTO userUpdateInRepositoryDTO) {
        long idUser = userUpdateInRepositoryDTO.getId();

        users.put(idUser, MapperUserDTO.toUserFromUserUpdateInRepositoryDTO(userUpdateInRepositoryDTO));

        return Optional.of(users.get(idUser));
    }

    @Override
    public boolean deleteUserById(long idUser) {
        users.remove(idUser);

        return findUserById(idUser).isEmpty();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();
    }

}
