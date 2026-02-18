package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<CommentDto> comments;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastBooking;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime nextBooking;
}
