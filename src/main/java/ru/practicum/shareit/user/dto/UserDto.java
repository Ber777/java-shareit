package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;
    @Email(message = "Email должен иметь формат адреса электронной почты - символ @")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
}
