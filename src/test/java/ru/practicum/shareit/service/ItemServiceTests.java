package ru.practicum.shareit.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        // Создаём User через new + сеттеры
        owner = new User();
        owner.setId(1L);
        owner.setName("Алексей");
        owner.setEmail("alex@example.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Иван");
        booker.setEmail("ivan@example.com");

        // Item
        item = new Item();
        item.setId(1L);
        item.setName("Ноутбук");
        item.setDescription("Игровой ноутбук");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Игровой ноутбук")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .text("Отличный товар!")
                .authorName("Иван")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateItem() {
        // Создаём UserDto (важно: заполняем id!)
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Алексей")
                .email("alex@example.com")
                .build();

        // Мокируем сервисы
        when(userService.getUserById(eq(1L))).thenReturn(userDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // Вызываем метод сервиса
        ItemDto result = itemService.createItem(1L, itemDto);

        // Проверяем результат
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        // Верифицируем вызовы
        verify(userService).getUserById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void shouldAddComment() {
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));

        when(bookingRepository.findByItemIdAndBookerIdAndEndDateIsBefore(
                eq(1L),
                eq(2L),
                any(LocalDateTime.class)
        )).thenReturn(List.of(new Booking()));

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setText("Отличный товар!");
        savedComment.setAuthor(booker);
        savedComment.setCreated(LocalDateTime.now());
        savedComment.setItem(item);

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(2L, 1L, commentDto);

        assertNotNull(result.getId());
        assertEquals(commentDto.getText(), result.getText());
        assertEquals("Иван", result.getAuthorName());

        verify(userRepository).findById(eq(2L));
        verify(itemRepository).findById(eq(1L));
        verify(bookingRepository).findByItemIdAndBookerIdAndEndDateIsBefore(
                eq(1L),
                eq(2L),
                any(LocalDateTime.class)
        );
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.addComment(999L, 1L, commentDto);
        });

        assertTrue(exception.getMessage().contains("Не найден пользователь с id: 999"));
        verify(userRepository).findById(999L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemDoesNotExist() {
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.addComment(2L, 999L, commentDto);
        });

        assertTrue(exception.getMessage().contains("Не найдена вещь с id: 999"));
        verify(itemRepository).findById(999L);
    }

    @Test
    void shouldThrowValidationExceptionWhenNoBookings() {
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));

        when(bookingRepository.findByItemIdAndBookerIdAndEndDateIsBefore(
                eq(1L),
                eq(2L),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            itemService.addComment(2L, 1L, commentDto);
        });

        assertTrue(exception.getMessage().contains("Пользователь не забронировал эту вещь"));

        verify(bookingRepository).findByItemIdAndBookerIdAndEndDateIsBefore(
                eq(1L),
                eq(2L),
                any(LocalDateTime.class)
        );
    }

    @Test
    void shouldUpdateItem() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(owner));
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));

        ItemDto updatedDto = ItemDto.builder()
                .name("Планшет")
                .description("Новый планшет")
                .available(false)
                .build();

        ItemDto result = itemService.updateItem(1L, 1L, updatedDto);

        assertEquals("Планшет", result.getName());
        assertEquals("Новый планшет", result.getDescription());
        assertFalse(result.getAvailable());

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByUpdateItem() {
        when(userRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(999L, 1L, itemDto);
        });

        assertTrue(exception.getMessage().contains("Не найден пользователь с id: 999"));
        verify(userRepository).findById(999L);
    }

    @Test
    void shouldGetItem() {
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(any())).thenReturn(Collections.emptyList());

        ItemDtoResponse result = itemService.getItem(1L, 1L);

        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

        verify(itemRepository).findById(1L);
        verify(bookingRepository).findByItemId(1L);
        verify(commentRepository).findByItemId(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemDoesNotExistByGetItem() {
        when(itemRepository.findById(eq(999L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.getItem(1L, 999L);
        });

        assertTrue(exception.getMessage().contains("Не найдена вещь с id: 999"));
        verify(itemRepository).findById(999L);
    }

    @Test
    void shouldGetUserItems() {
        List<Item> items = List.of(item);
        when(itemRepository.findAllByOwnerId(eq(1L))).thenReturn(items);
        when(bookingRepository.findByItemId(any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(any())).thenReturn(Collections.emptyList());

        Collection<ItemDtoResponse> result = itemService.getUserItems(1L);

        assertEquals(1, result.size());
        ItemDtoResponse dto = result.iterator().next();
        assertEquals(itemDto.getId(), dto.getId());
        assertEquals(itemDto.getName(), dto.getName());

        verify(itemRepository).findAllByOwnerId(1L);
    }

    @Test
    void shouldGetItemsByText() {
        Item availableItem = new Item();
        availableItem.setId(1L);
        availableItem.setName("Ноутбук");
        availableItem.setDescription("Игровой ноутбук");
        availableItem.setAvailable(true);
        availableItem.setOwner(owner);

        List<Item> items = List.of(availableItem);
        when(itemRepository.search(eq("Ноутбук"))).thenReturn(items);
        when(bookingRepository.findByItemId(any())).thenReturn(Collections.emptyList());
        when(commentRepository.findByItemId(any())).thenReturn(Collections.emptyList());

        Collection<ItemDtoResponse> result = itemService.getItemsByText("Ноутбук");

        assertEquals(1, result.size());
        ItemDtoResponse dto = result.iterator().next();
        assertEquals("Ноутбук", dto.getName());

        verify(itemRepository).search("Ноутбук");
    }

    @Test
    void shouldReturnEmptyListWhenTextIsEmpty() {
        Collection<ItemDtoResponse> result = itemService.getItemsByText("");
        assertTrue(result.isEmpty());

        result = itemService.getItemsByText("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoAvailableItems() {
        Item unavailableItem = new Item();
        unavailableItem.setId(1L);
        unavailableItem.setName("Ноутбук");
        unavailableItem.setDescription("Игровой ноутбук");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);

        List<Item> items = List.of(unavailableItem);
        when(itemRepository.search(eq("Ноутбук"))).thenReturn(items);

        Collection<ItemDtoResponse> result = itemService.getItemsByText("Ноутбук");

        assertTrue(result.isEmpty());
        verify(itemRepository).search("Ноутбук");
    }

    @Test
    void shouldDeleteItem() {
        doNothing().when(itemRepository).deleteById(1L);

        itemService.deleteItem(1L);

        verify(itemRepository).deleteById(1L);
    }

    @Test
    void shouldNotThrowExceptionWhenItemDoesNotExistByDeleteItem() {
        doNothing().when(itemRepository).deleteById(999L);

        itemService.deleteItem(999L);  // Не должно быть исключения

        verify(itemRepository).deleteById(999L);
    }
}
