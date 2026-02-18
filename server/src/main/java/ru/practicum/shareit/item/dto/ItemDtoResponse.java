package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<BookingDto> bookings;
    private List<CommentDto> comments;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}
