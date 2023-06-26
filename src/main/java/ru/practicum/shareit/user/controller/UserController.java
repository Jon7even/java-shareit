package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    //private final UserControllerMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestCreateDTO userRequestCreateDTO,
                                      HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User userCreate = userService.createUser(
                User.builder()
                        .name(userRequestCreateDTO.getName())
                        .email(userRequestCreateDTO.getEmail())
                        .build()
        );

        return UserResponseDTO.builder()
                .id(userCreate.getId())
                .name(userCreate.getName())
                .email(userCreate.getEmail())
                .build();
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO updateUser(@PathVariable long userId,
                                      @Valid @RequestBody UserRequestUpdateDTO userRequestUpdateDTO,
                                      HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User userUpdate = userService.updateUser(
                User.builder()
                        .id(userId)
                        .name(userRequestUpdateDTO.getName())
                        .email(userRequestUpdateDTO.getEmail())
                        .build()
        );

        return UserResponseDTO.builder()
                .id(userUpdate.getId())
                .name(userUpdate.getName())
                .email(userUpdate.getEmail())
                .build();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getUserById(@PathVariable long userId,
                                       HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User getUserById = userService.findUserById(userId);

        return UserResponseDTO.builder()
                .id(getUserById.getId())
                .name(getUserById.getName())
                .email(getUserById.getEmail())
                .build();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserById(@PathVariable long userId,
                               HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        userService.deleteUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers(HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        return userService.getAllUsers();
    }

}
