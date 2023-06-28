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

    private final UserControllerMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestCreateDTO userRequestCreateDTO,
                                      HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User userCreate = userService.createUser(mapper.toUserFromUserRequestCreateDTO(userRequestCreateDTO));

        return mapper.toUserResponseDTOFromUser(userCreate);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO updateUser(@PathVariable long userId,
                                      @Valid @RequestBody UserRequestUpdateDTO userRequestUpdateDTO,
                                      HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User userUpdate = userService.updateUser(mapper.toUserFromUserRequestCreateDTO(userId, userRequestUpdateDTO));

        return mapper.toUserResponseDTOFromUser(userUpdate);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getUserById(@PathVariable long userId,
                                       HttpServletRequest request) {
        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User getUserById = userService.findUserById(userId);

        return mapper.toUserResponseDTOFromUser(getUserById);
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
