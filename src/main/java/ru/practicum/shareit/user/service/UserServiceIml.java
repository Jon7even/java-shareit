package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserUpdateInRepositoryDTO;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_FROM_DB;
import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_IN_DB;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserDao userDao;

    @Override
    public User createUser(User user) {
        checkEmail(user.getEmail());

        log.debug("Add new [user={}] {}", user, SERVICE_IN_DB);
        Optional<User> createdUser = userDao.createUser(user);

        if (createdUser.isPresent()) {
            log.debug("New user has returned [user={}] {}", createdUser.get(), SERVICE_FROM_DB);
            return createdUser.get();
        } else {
            log.error("[user={}] was not created", user);
            throw new EntityNotCreatedException("New user");
        }
    }

    @Override
    public User findUserById(long idUser) {
        log.debug("Get user by [id={}] {}", idUser, SERVICE_IN_DB);
        Optional<User> foundUser = userDao.findUserById(idUser);

        if (foundUser.isPresent()) {
            log.debug("Found [user={}] {}", foundUser.get(), SERVICE_FROM_DB);
            return foundUser.get();
        } else {
            log.warn("User by [id={}] was not found", idUser);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", idUser));
        }
    }

    @Override
    public User updateUser(User user) {
        long userId = user.getId();

        User getUserById = findUserById(userId);
        UserUpdateInRepositoryDTO updateUserInRepository = UserUpdateInRepositoryDTO.builder().id(userId).build();

        if (user.getEmail() == null) {
            updateUserInRepository.setEmail(getUserById.getEmail());
        } else {
            Optional<User> checkedEmailUser = userDao.findUserByEmail(user.getEmail());

            if (checkedEmailUser.isPresent()) {
                boolean isEqualsEmailThisUser = checkedEmailUser.get().getId() == userId;

                if (isEqualsEmailThisUser) {
                    updateUserInRepository.setEmail(checkedEmailUser.get().getEmail());
                } else {
                    log.error("User with [email={}] already exist [idUser={}]", user.getEmail(), userId);
                    throw new EntityAlreadyExistsException("This email");
                }

            } else {
                updateUserInRepository.setEmail(user.getEmail());
            }
        }

        boolean isEqualsEmail = getUserById.getEmail().equals(user.getEmail());
        boolean isEqualsName = getUserById.getName().equals(user.getName());

        if (isEqualsEmail && isEqualsName) {
            log.warn("No need to update user data \n[userUpdate={}]\n[userResult={}]", user, getUserById);
            return getUserById;

        } else {

            if (isEqualsName || user.getName() == null) {
                updateUserInRepository.setName(getUserById.getName());
            } else {
                updateUserInRepository.setName(user.getName());
            }

            log.debug("Update [user={}] {}", updateUserInRepository, SERVICE_IN_DB);
            Optional<User> updatedUser = userDao.updateUser(updateUserInRepository);

            if (updatedUser.isPresent()) {
                log.debug("Updated user has returned [user={}] {}", updatedUser.get(), SERVICE_FROM_DB);
                return updatedUser.get();
            } else {
                log.error("[user={}] was not updated", user);
                throw new EntityNotUpdatedException(String.format("User with [idUser=%d]", userId));
            }
        }
    }

    @Override
    public void deleteUserById(long idUser) {
        User getUserById = findUserById(idUser);

        log.debug("Remove [user={}] {}", getUserById, SERVICE_IN_DB);
        boolean isRemoved = userDao.deleteUserById(idUser);

        if (isRemoved) {
            log.debug("User by [id={}] has removed {}", idUser, SERVICE_FROM_DB);
        } else {
            log.error("User by [id={}] was not removed", idUser);
            throw new EntityNotDeletedException(String.format("User with [idUser=%d]", idUser));
        }
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Get all users {}", SERVICE_IN_DB);
        List<User> listUsers = userDao.getAllUsers();

        if (listUsers.isEmpty()) {
            log.info("Has returned empty list users {}", SERVICE_FROM_DB);
        } else {
            log.info("Found list users [count={}] {}", listUsers.size(), SERVICE_FROM_DB);
        }

        return listUsers;
    }

    private void checkEmail(String email) {
        if (userDao.findUserByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("This email");
        }
    }

}
