package ru.practicum.shareit.controllers;

import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void shouldCreateRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("Нужен ноутбук для работы")
                .build();
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен ноутбук для работы")
                .created(LocalDateTime.of(2026, 2, 16, 12, 0))
                .build();

        when(itemRequestService.createRequest(userId, inputDto)).thenReturn(outputDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужен ноутбук для работы"))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        Long userId = 1L;
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder().id(1L).description("Запрос 1").build(),
                ItemRequestDto.builder().id(2L).description("Запрос 2").build()
        );
        when(itemRequestService.getUserRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].description").value("Запрос 2"));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoUserRequests() throws Exception {
        Long userId = 999L; // пользователь без запросов
        when(itemRequestService.getUserRequests(userId)).thenReturn(List.of());


        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldGetAllRequestsWithPagination() throws Exception {
        Long userId = 1L;
        List<ItemRequestDto> allRequests = List.of(
                ItemRequestDto.builder().id(3L).description("Общий запрос 1").build(),
                ItemRequestDto.builder().id(4L).description("Общий запрос 2").build()
        );
        when(itemRequestService.getAllRequests(userId, 0, 10)).thenReturn(allRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    void shouldHandleDefaultPaginationValues() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getAllRequests(userId, 0, 10)).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                // Параметры from=0 и size=10 подставятся автоматически
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldGetRequestById() throws Exception {
        Long userId = 1L, requestId = 5L;
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(requestId)
                .description("Запрос на принтер")
                .items(List.of())
                .build();
        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(dto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Запрос на принтер"))
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
