package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.constants.NamesInController.API_USERS;

@Controller
@RequestMapping(path = API_USERS)
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateTO userCreateTO) {
        log.info("Creating user {}", userCreateTO);

        return userClient.createUser(userCreateTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @Positive Long userId) {
        log.info("Get user={}", userId);

        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUserById(@PathVariable @Positive Long userId,
                                                 @Valid @RequestBody UserUpdateTO userUpdateTO) {
        log.info("Updating user {}", userUpdateTO);

        return userClient.updateUserById(userId, userUpdateTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUserById(@PathVariable @Positive Long userId) {
        log.info("Remove userId={}", userId);

        return userClient.deleteUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get All users");

        return userClient.getAllUsers();
    }

}
