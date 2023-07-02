package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.MapperUserDTO;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constants.NamesLogsInController.IN_CONTROLLER_METHOD;
import static ru.practicum.shareit.constants.NamesParametersInController.X_COUNT_ITEMS;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestCreateDTO userRequestCreateDTO,
                                                      HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User userCreate = userService.createUser(MapperUserDTO.toUserFromUserRequestCreateDTO(userRequestCreateDTO));

        return ResponseEntity.status(HttpStatus.CREATED).body(MapperUserDTO.toUserResponseDTOFromUser(userCreate));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable long userId,
                                                       HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User getUserById = userService.findUserById(userId);

        return ResponseEntity.ok().body(MapperUserDTO.toUserResponseDTOFromUser(getUserById));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUserById(@PathVariable long userId,
                                                          @Valid @RequestBody UserRequestUpdateDTO userRequestUpdateDTO,
                                                          HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());
        User userUpdate = userService.updateUser(
                MapperUserDTO.toUserFromUserRequestUpdateDTO(userRequestUpdateDTO, userId)
        );

        return ResponseEntity.ok().body(MapperUserDTO.toUserResponseDTOFromUser(userUpdate));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUserById(@PathVariable long userId,
                                               HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        userService.deleteUserById(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        List<User> listAllUsers = userService.getAllUsers();

        return ResponseEntity.ok()
                .header(X_COUNT_ITEMS, String.valueOf(listAllUsers.size()))
                .body(listAllUsers);
    }

}
