package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @JsonInclude
    private Long id;

    @NotNull(message = "не должно равняться null")
    private Long itemId;

    @NotNull(message = "не должно равняться null")
    @FutureOrPresent(message = "должно содержать сегодняшнее число или дату, которая еще не наступила")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "не должно равняться null")
    @Future(message = "должно содержать дату, которая еще не наступила")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
}
