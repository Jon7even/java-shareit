package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;
import static ru.practicum.shareit.constants.NamesParametersInController.X_COUNT_ITEMS;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseTO> createUser(@Valid @RequestBody UserCreateTO userRequestCreateDTO,
                                                     HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestCreateDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseTO> getUserById(@PathVariable Optional<Long> userId,
                                                      HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(userService.findUserById(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseTO> updateUserById(@PathVariable Optional<Long> userId,
                                                         @Valid @RequestBody UserUpdateTO userRequestUpdateDTO,
                                                         HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(userService.updateUser(userRequestUpdateDTO, userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUserById(@PathVariable Optional<Long> userId,
                                               HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        userService.deleteUserById(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseTO>> getAllUsers(HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        List<UserResponseTO> listAllUsers = userService.getAllUsers();

        return ResponseEntity.ok().header(X_COUNT_ITEMS, String.valueOf(listAllUsers.size())).body(listAllUsers);
    }

}
