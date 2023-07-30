package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotDeletedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateTO;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.NamesLogsInService.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository repositoryUser;

    @Transactional
    @Override
    public UserResponseTO createUser(UserCreateTO userRequestCreateTO) {
        log.debug("New userTO came {} [UserRequestCreateTO={}]", SERVICE_FROM_CONTROLLER, userRequestCreateTO);
        UserEntity user = UserMapper.INSTANCE.toEntityFromDTOCreate(userRequestCreateTO);

        try {
            log.debug("Add new entity [user={}] {}", user, SERVICE_IN_DB);
            UserEntity createdUser = repositoryUser.save(user);

            log.debug("New user has returned [user={}] {}", createdUser, SERVICE_FROM_DB);
            return UserMapper.INSTANCE.toDTOResponseFromEntity(createdUser);

        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("This email");
        }

    }

    @Override
    public UserResponseTO findUserById(Optional<Long> idUser) {
        Long checkedUserId = checkParameterUserId(idUser);

        log.debug("Get user by [id={}] {}", idUser, SERVICE_IN_DB);
        Optional<UserEntity> foundUser = repositoryUser.findById(checkedUserId);

        if (foundUser.isPresent()) {
            log.debug("Found [user={}] {}", foundUser.get(), SERVICE_FROM_DB);
            return UserMapper.INSTANCE.toDTOResponseFromEntity(foundUser.get());
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    @Transactional
    @Override
    public UserResponseTO updateUser(UserUpdateTO userRequestUpdateDTO, Optional<Long> idUser) {
        log.debug("User for update came {} [UserRequestUpdateDTO={}]", SERVICE_FROM_CONTROLLER, userRequestUpdateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        UserEntity user = UserMapper.INSTANCE.toEntityFromDTOUpdate(userRequestUpdateDTO, checkedUserId);

        UserEntity checkedUserFromRepository = findUserEntityById(checkedUserId);
        UserEntity updateUserInRepository = UserEntity.builder().id(checkedUserId).build();

        if (user.getEmail() == null) {
            updateUserInRepository.setEmail(checkedUserFromRepository.getEmail());
        } else {
            Optional<UserEntity> checkedEmailUser =
                    repositoryUser.findUserEntityByEmailContainingIgnoreCase(user.getEmail());

            if (checkedEmailUser.isPresent()) {
                boolean isEqualsEmailThisUser = Objects.equals(checkedEmailUser.get().getId(), checkedUserId);

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

            log.debug("Update entity [user={}] {}", updateUserInRepository, SERVICE_IN_DB);
            UserEntity updatedUser = repositoryUser.save(updateUserInRepository);

            Optional<UserEntity> foundUserAfterUpdate = repositoryUser.findById(checkedUserId);
            log.debug("Updated user has returned [user={}] {}", updatedUser, SERVICE_FROM_DB);

            return UserMapper.INSTANCE.toDTOResponseFromEntity(updatedUser);
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Optional<Long> idUser) {
        Long checkedUserId = checkParameterUserId(idUser);
        UserEntity checkedUserFromRepository = findUserEntityById(checkedUserId);

        log.debug("Remove [user={}] {}", checkedUserFromRepository, SERVICE_IN_DB);
        repositoryUser.deleteById(checkedUserId);
        boolean isRemoved = repositoryUser.existsById(checkedUserId);

        if (!isRemoved) {
            log.debug("User by [id={}] has removed {}", checkedUserId, SERVICE_FROM_DB);
        } else {
            log.error("User by [id={}] was not removed", checkedUserId);
            throw new EntityNotDeletedException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    @Override
    public List<UserResponseTO> getAllUsers() {
        log.debug("Get all users {}", SERVICE_IN_DB);
        List<UserEntity> listUsers = repositoryUser.findAll();

        if (listUsers.isEmpty()) {
            log.debug("Has returned empty list users {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list users [count={}] {}", listUsers.size(), SERVICE_FROM_DB);
        }

        return listUsers.stream().map(UserMapper.INSTANCE::toDTOResponseFromEntity).collect(Collectors.toList());
    }

    private UserEntity findUserEntityById(Long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<UserEntity> foundCheckUser = repositoryUser.findById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
        }
    }

    public Long checkParameterUserId(Optional<Long> idUser) {
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
