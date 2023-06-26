package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserRequestCreateDTO {
    @NotBlank
    @NonNull
    private String name;

    @Email
    @NonNull
    @NotBlank
    private String email;
}