package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestCreateDTO {
    @NotBlank
    @NonNull
    private String name;

    @Email
    @NonNull
    @NotBlank
    private String email;
}