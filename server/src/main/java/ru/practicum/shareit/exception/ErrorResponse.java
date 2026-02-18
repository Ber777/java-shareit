package ru.practicum.shareit.exception;

import lombok.*;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final String error;
}
