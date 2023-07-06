package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotCreatedException;
import ru.practicum.shareit.exception.EntityNotDeletedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotUpdatedException;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_FROM_DB;
import static ru.practicum.shareit.constants.NamesLogsInService.SERVICE_IN_DB;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserDao repositoryUser;

    @Override
    public UserResponseDTO createUser(UserRequestCreateDTO userRequestCreateDTO) {
        User user = UserMapper.INSTANCE.toEntityFromDTOCreate(userRequestCreateDTO);
        checkEmail(user.getEmail());

        log.debug("Add new [user={}] {}", user, SERVICE_IN_DB);
        Optional<User> createdUser = repositoryUser.createUser(user);

        if (createdUser.isPresent()) {
            log.debug("New user has returned [user={}] {}", createdUser.get(), SERVICE_FROM_DB);
            return UserMapper.INSTANCE.toDTOResponseFromEntity(createdUser.get());
        } else {
            log.error("[user={}] was not created", user);
            throw new EntityNotCreatedException("New user");
        }
    }

    @Override
    public UserResponseDTO findUserById(Optional<Long> idUser) {
        long checkedUserId = checkParameterUserId(idUser);

        log.debug("Get user by [id={}] {}", idUser, SERVICE_IN_DB);
        Optional<User> foundUser = repositoryUser.findUserById(checkedUserId);

        if (foundUser.isPresent()) {
            log.debug("Found [user={}] {}", foundUser.get(), SERVICE_FROM_DB);
            return UserMapper.INSTANCE.toDTOResponseFromEntity(foundUser.get());
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    @Override
    public UserResponseDTO updateUser(UserRequestUpdateDTO userRequestUpdateDTO, Optional<Long> idUser) {
        Long checkedUserId = checkParameterUserId(idUser);
        User user = UserMapper.INSTANCE.toEntityFromDTOUpdate(userRequestUpdateDTO, checkedUserId);

        User checkedUserFromRepository = findUserEntityById(checkedUserId);
        User updateUserInRepository = User.builder().id(checkedUserId).build();

        if (user.getEmail() == null) {
            updateUserInRepository.setEmail(checkedUserFromRepository.getEmail());
        } else {
            Optional<User> checkedEmailUser = repositoryUser.findUserByEmail(user.getEmail());

            if (checkedEmailUser.isPresent()) {
                boolean isEqualsEmailThisUser = checkedEmailUser.get().getId() == checkedUserId;

                if (isEqualsEmailThisUser) {
                    updateUserInRepository.setEmail(checkedEmailUser.get().getEmail());
                } else {
                    log.error("User with [email={}] already exist [idUser={}]", user.getEmail(), checkedUserId);
                    throw new EntityAlreadyExistsException("This email");
                }

            } else {
                updateUserInRepository.setEmail(user.getEmail());
            }
        }

        boolean isEqualsEmail = checkedUserFromRepository.getEmail().equals(user.getEmail());
        boolean isEqualsName = checkedUserFromRepository.getName().equals(user.getName());

        if (isEqualsEmail && isEqualsName) {
            log.warn("No need to update user data \n[userUpdate={}]\n[userResult={}]", user, checkedUserFromRepository);
            return UserMapper.INSTANCE.toDTOResponseFromEntity(checkedUserFromRepository);
        } else {

            if (isEqualsName || user.getName() == null) {
                updateUserInRepository.setName(checkedUserFromRepository.getName());
            } else {
                updateUserInRepository.setName(user.getName());
            }

            log.debug("Update [user={}] {}", updateUserInRepository, SERVICE_IN_DB);
            Optional<User> updatedUser = repositoryUser.updateUser(updateUserInRepository);

            if (updatedUser.isPresent()) {
                log.debug("Updated user has returned [user={}] {}", updatedUser.get(), SERVICE_FROM_DB);
                return UserMapper.INSTANCE.toDTOResponseFromEntity(updatedUser.get());
            } else {
                log.error("[user={}] was not updated", user);
                throw new EntityNotUpdatedException(String.format("User with [idUser=%d]", checkedUserId));
            }
        }
    }

    @Override
    public void deleteUserById(Optional<Long> idUser) {
        long checkedUserId = checkParameterUserId(idUser);
        User checkedUserFromRepository = findUserEntityById(checkedUserId);

        log.debug("Remove [user={}] {}", checkedUserFromRepository, SERVICE_IN_DB);
        boolean isRemoved = repositoryUser.deleteUserById(checkedUserId);

        if (isRemoved) {
            log.debug("User by [id={}] has removed {}", checkedUserId, SERVICE_FROM_DB);
        } else {
            log.error("User by [id={}] was not removed", checkedUserId);
            throw new EntityNotDeletedException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Get all users {}", SERVICE_IN_DB);
        List<User> listUsers = repositoryUser.getAllUsers();

        if (listUsers.isEmpty()) {
            log.debug("Has returned empty list users {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list users [count={}] {}", listUsers.size(), SERVICE_FROM_DB);
        }

        return listUsers.stream().map(UserMapper.INSTANCE::toDTOResponseFromEntity).collect(Collectors.toList());
    }

    private User findUserEntityById(long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<User> foundCheckUser = repositoryUser.findUserById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    private void checkEmail(String email) {
        if (repositoryUser.findUserByEmail(email).isPresent()) {
            throw new EntityAlreadyExistsException("This email");
        }
    }

    private Long checkParameterUserId(Optional<Long> idUser) {
        if (idUser.isPresent()) {
            if (idUser.get() > 0) {
                log.debug("Checking [idUser={}] is ok", idUser.get());
            }
        } else {
            throw new EntityNotFoundException("User with [idUser=null]");
        }

        return idUser.get();
    }

}
