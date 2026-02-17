package ru.practicum.shareit.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestModelTests {

    @Test
    void shouldTestNoArgsConstructor() {
        ItemRequest request = new ItemRequest();

        assertNull(request.getId());
        assertNull(request.getDescription());
        assertNull(request.getRequestor());
        assertNull(request.getCreated());
        assertNotNull(request.getItems());
        assertEquals(0, request.getItems().size());
    }

    @Test
    void shouldTestSettersAndGetters() {
        ItemRequest request = new ItemRequest();

        Long id = 1L;
        String description = "Нужна дрель";
        User requestor = new User();
        LocalDateTime created = LocalDateTime.now();
        List<Item> items = new ArrayList<>();

        request.setId(id);
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(created);
        request.setItems(items);

        assertEquals(id, request.getId());
        assertEquals(description, request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(created, request.getCreated());
        assertEquals(items, request.getItems());
    }

    @Test
    void shouldTestItemsInitialization() {
        ItemRequest request = new ItemRequest();

        assertNotNull(request.getItems());
        assertEquals(0, request.getItems().size());

        List<Item> items = new ArrayList<>();
        items.add(new Item());
        request.setItems(items);

        assertEquals(1, request.getItems().size());
    }

    @Test
    void shouldCreateTimestamp() {
        ItemRequest request = new ItemRequest();
        assertNull(request.getCreated());

        LocalDateTime now = LocalDateTime.now();
        request.setCreated(now);
        assertEquals(now, request.getCreated());
    }

    @Test
    void shouldTestEquals() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        assertEquals(request1, request1);

        assertEquals(request1, request2);
        assertEquals(request2, request1);

        assertNotEquals(request1, request3);
        assertNotEquals(request3, request1);

        assertNotEquals(null, request1);

        // Сравнение с объектом другого класса
        assertNotEquals(new Object(), request1);
    }

    @Test
    void shouldTestHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        // Все объекты ItemRequest имеют одинаковый hashCode
        assertEquals(request1.hashCode(), request2.hashCode());

        // Проверка консистентности
        int initialHashCode = request1.hashCode();
        assertEquals(initialHashCode, request1.hashCode());
        assertEquals(initialHashCode, request1.hashCode());
    }

    @Test
    void shouldTestToString() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Описание");

        String toString = request.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("description=Описание"));
    }

    @Test
    void shouldTestEqualsWithNullId() {
        ItemRequest request1 = new ItemRequest();
        ItemRequest request2 = new ItemRequest();

        assertNotEquals(request1, request2);
    }
}
