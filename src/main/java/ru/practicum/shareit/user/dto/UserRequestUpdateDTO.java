package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestUpdateDTO {
    private String name;

    private String email;

    public UserRequestUpdateDTO(String name, String email) {
        if (name == null || name.isBlank()) {
            this.name = "empty";
        } else {
            this.name = name;
        }

        if (email == null || email.isBlank()) {
            this.email = "empty";
        } else {
            this.email = email;
        }
    }
}
