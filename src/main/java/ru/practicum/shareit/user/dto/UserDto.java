package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.exception.Create;
import ru.practicum.shareit.exception.Update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String name;

    @Email(message = "Email должен иметь формат адреса электронной почты - символ @",
            groups = {Create.class, Update.class})
    @NotBlank(message = "Email не должен быть пустым", groups = {Create.class})
    private String email;
}