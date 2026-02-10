package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.shareit.booking.dto.BookingDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<BookingDto> bookings;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<CommentDto> comments;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastBooking;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime nextBooking;
}
