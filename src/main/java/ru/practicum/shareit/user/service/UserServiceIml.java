package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotCreatedException;
import ru.practicum.shareit.exception.EntityNotDeletedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotUpdatedException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.model.User;

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
    public UserResponseDTO createUser(UserRequestCreateDTO userRequestCreateDTO) {
        log.debug("New user came {} [UserRequestCreateDTO={}]", SERVICE_FROM_CONTROLLER, userRequestCreateDTO);
        User user = UserMapper.INSTANCE.toEntityFromDTOCreate(userRequestCreateDTO);

        try {
            log.debug("Add new [user={}] {}", user, SERVICE_IN_DB);
            User createdUser = repositoryUser.save(user);
            Optional<User> foundUserAfterCreation = repositoryUser.findById(createdUser.getId());

            if (foundUserAfterCreation.isPresent() && createdUser.equals(foundUserAfterCreation.get())) {
                log.debug("New user has returned [user={}] {}", createdUser, SERVICE_FROM_DB);
                return UserMapper.INSTANCE.toDTOResponseFromEntity(createdUser);
            } else {
                log.error("[user={}] was not created", user);
                throw new EntityNotCreatedException("New user");
            }
        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("This email");
        }

    }

    @Override
    public UserResponseDTO findUserById(Optional<Long> idUser) {
        Long checkedUserId = checkParameterUserId(idUser);

        log.debug("Get user by [id={}] {}", idUser, SERVICE_IN_DB);
        Optional<User> foundUser = repositoryUser.findById(checkedUserId);

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
    public UserResponseDTO updateUser(UserRequestUpdateDTO userRequestUpdateDTO, Optional<Long> idUser) {
        log.debug("User for update came {} [UserRequestUpdateDTO={}]", SERVICE_FROM_CONTROLLER, userRequestUpdateDTO);
        Long checkedUserId = checkParameterUserId(idUser);
        User user = UserMapper.INSTANCE.toEntityFromDTOUpdate(userRequestUpdateDTO, checkedUserId);

        User checkedUserFromRepository = findUserEntityById(checkedUserId);
        User updateUserInRepository = User.builder().id(checkedUserId).build();

        if (user.getEmail() == null) {
            updateUserInRepository.setEmail(checkedUserFromRepository.getEmail());
        } else {
            Optional<User> checkedEmailUser = repositoryUser.findUserByEmailContainingIgnoreCase(user.getEmail());

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

            log.debug("Update [user={}] {}", updateUserInRepository, SERVICE_IN_DB);
            User updatedUser = repositoryUser.save(updateUserInRepository);
            Optional<User> foundUserAfterUpdate = repositoryUser.findById(checkedUserId);

            if (foundUserAfterUpdate.isPresent() && updatedUser.equals(foundUserAfterUpdate.get())) {
                log.debug("Updated user has returned [user={}] {}", updatedUser, SERVICE_FROM_DB);
                return UserMapper.INSTANCE.toDTOResponseFromEntity(updatedUser);
            } else {
                log.error("[user={}] was not updated", user);
                throw new EntityNotUpdatedException(String.format("User with [idUser=%d]", checkedUserId));
            }
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Optional<Long> idUser) {
        Long checkedUserId = checkParameterUserId(idUser);
        User checkedUserFromRepository = findUserEntityById(checkedUserId);

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
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Get all users {}", SERVICE_IN_DB);
        List<User> listUsers = repositoryUser.findAll();

        if (listUsers.isEmpty()) {
            log.debug("Has returned empty list users {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list users [count={}] {}", listUsers.size(), SERVICE_FROM_DB);
        }

        return listUsers.stream().map(UserMapper.INSTANCE::toDTOResponseFromEntity).collect(Collectors.toList());
    }

    private User findUserEntityById(Long checkedUserId) {
        log.debug("Get user entity for checking by [idUser={}] {}", checkedUserId, SERVICE_IN_DB);
        Optional<User> foundCheckUser = repositoryUser.findById(checkedUserId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [id={}] was not found", checkedUserId);
            throw new EntityNotFoundException(String.format("User with [idUser=%d]", checkedUserId));
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
