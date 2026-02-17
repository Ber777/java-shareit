package ru.practicum.shareit.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceImplTests {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager em;

    private Long ownerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        // Создаём владельца
        UserDto ownerDto = makeUserDto("owner@example.com", "Алексей", "Иванов");
        ownerDto = userService.createUser(ownerDto);
        ownerId = ownerDto.getId();

        // Создаём вещь
        ItemDto itemDto = makeItemDto("Ноутбук", "Мощный игровой ноутбук", true);
        itemDto = itemService.createItem(ownerId, itemDto);
        itemId = itemDto.getId();
    }

    // Вспомогательные методы
    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(firstName + " " + lastName);
        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemDtoResponse makeItemDtoResponse(Long id, String name, String description) {
        ItemDtoResponse dto = new ItemDtoResponse();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }

    @Test
    void shouldCreateItem() {
        ItemDto newItemDto = makeItemDto("Смартфон", "Новый iPhone", true);

        ItemDto created = itemService.createItem(ownerId, newItemDto);

        // Проверка DTO
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Смартфон");
        assertThat(created.getDescription()).isEqualTo("Новый iPhone");
        assertThat(created.getAvailable()).isTrue();

        // Проверка в БД
        Item itemInDb = em.find(Item.class, created.getId());
        assertThat(itemInDb).isNotNull();
        assertThat(itemInDb.getName()).isEqualTo("Смартфон");
        assertThat(itemInDb.getOwner().getId()).isEqualTo(ownerId);
    }

    @Test
    void shouldGetItemAsOwner() {
        ItemDtoResponse response = itemService.getItem(ownerId, itemId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
        assertThat(response.getName()).isEqualTo("Ноутбук");
        assertThat(response.getDescription()).isEqualTo("Мощный игровой ноутбук");
        assertThat(response.getAvailable()).isTrue();
        // Для владельца должны быть заполнены last/next booking (если есть бронирования)
        // (в этом тесте бронирований нет — поля могут быть null)
    }

    @Test
    void shouldGetItemAsNonOwner() {
        // Создаём другого пользователя (не владельца)
        UserDto viewerDto = makeUserDto("viewer@example.com", "Мария", "Петрова");
        viewerDto = userService.createUser(viewerDto);
        Long viewerId = viewerDto.getId();

        ItemDtoResponse response = itemService.getItem(viewerId, itemId);

        // Проверка (для невладельца поля last/next booking не заполняются)
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(itemId);
        assertThat(response.getLastBooking()).isNull();
        assertThat(response.getNextBooking()).isNull();
    }

    @Test
    void shouldGetUserItems() {
        Collection<ItemDtoResponse> items = itemService.getUserItems(ownerId);

        assertThat(items).isNotEmpty();
        assertThat(items).anyMatch(i -> i.getId().equals(itemId));
        assertThat(items.size()).isGreaterThanOrEqualTo(1);

        ItemDtoResponse item = items.iterator().next();
        assertThat(item.getName()).isEqualTo("Ноутбук");
        assertThat(item.getDescription()).isEqualTo("Мощный игровой ноутбук");
    }

    @Test
    void shouldSearchItemsByText() {
        Collection<ItemDtoResponse> results = itemService.getItemsByText("ноутбук");

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(i -> i.getName().contains("Ноутбук"));
        assertThat(results).allMatch(ItemDtoResponse::getAvailable);
    }

    @Test
    void shouldReturnEmptyListForEmptySearch() {
        Collection<ItemDtoResponse> results = itemService.getItemsByText("");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldUpdateItem() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Обновлённый ноутбук");
        updateDto.setDescription("Теперь с SSD");
        updateDto.setAvailable(false);

        ItemDto updated = itemService.updateItem(ownerId, itemId, updateDto);

        // Проверка DTO
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Обновлённый ноутбук");
        assertThat(updated.getDescription()).isEqualTo("Теперь с SSD");
        assertThat(updated.getAvailable()).isFalse();

        // Проверка в БД
        Item itemInDb = em.find(Item.class, itemId);
        assertThat(itemInDb.getName()).isEqualTo("Обновлённый ноутбук");
        assertThat(itemInDb.getAvailable()).isFalse();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemDoesNotExistOnUpdate() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Новое имя");

        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(ownerId, 999L, updateDto);
        });
    }

    @Test
    void shouldAddComment() {
        // Сначала создаём бронирование (чтобы пройти проверку)
        Booking booking = new Booking();
        booking.setItem(em.getReference(Item.class, itemId));
        booking.setBooker(em.getReference(User.class, ownerId));
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        em.persist(booking);
        em.flush();

        // Подготовка комментария
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличный товар!");

        CommentDto savedComment = itemService.addComment(ownerId, itemId, commentDto);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Отличный товар!");
        assertThat(savedComment.getAuthorName()).isEqualTo("Алексей Иванов");

        // Проверка в БД
        Comment commentInDb = em.createQuery("SELECT c FROM Comment c WHERE c.item.id = :itemId", Comment.class)
                .setParameter("itemId", itemId)
                .getSingleResult();

        assertThat(commentInDb.getText()).isEqualTo("Отличный товар!");
        assertThat(commentInDb.getAuthor().getId()).isEqualTo(ownerId);
        assertThat(commentInDb.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Тест комментария");

        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.addComment(999L, itemId, commentDto);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemDoesNotExistOnAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Тест комментария");

        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.addComment(ownerId, 999L, commentDto);
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenUserHasNoBookingOnAddComment() {
        // Создаём пользователя без бронирований
        UserDto userDto = makeUserDto("no-booking@example.com", "Сергей", "Сидоров");
        userDto = userService.createUser(userDto);
        Long userId = userDto.getId();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Нет бронирования!");

        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.addComment(userId, itemId, commentDto);
        });
    }

    @Test
    void shouldDeleteItem() {
        itemService.deleteItem(itemId);

        // Проверяем, что вещь больше не существует
        boolean exists = em.createQuery("SELECT COUNT(i) FROM Item i WHERE i.id = :id", Long.class)
                .setParameter("id", itemId)
                .getSingleResult() > 0;

        assertThat(exists).isFalse();

        // Попытка получить удалённую вещь должна вызвать исключение
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getItem(ownerId, itemId);
        });
    }

    @Test
    void shouldNotDeleteItem() {
        // Удаление несуществующей вещи не должно вызывать ошибку
        Assertions.assertDoesNotThrow(() -> {
            itemService.deleteItem(999L);
        });
    }
}
