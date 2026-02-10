package ru.practicum.shareit.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.ItemRequest;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemModelTests {
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
    }

    // Тестируем сеттеры и геттеры
    @Test
    void shouldTestSettersAndGetters() {
        item.setId(1L);
        item.setName("Диван");
        item.setDescription("Хороший диван");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(10L);
        item.setOwner(owner);

        ItemRequest request = new ItemRequest();
        request.setId(100L);
        item.setRequest(request);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Диван");
        assertThat(item.getDescription()).isEqualTo("Хороший диван");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    void shouldTestNoArgsConstructor() {
        assertThat(item).isNotNull();
        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isNull();
        assertThat(item.getDescription()).isNull();
        assertThat(item.getAvailable()).isNull();
        assertThat(item.getOwner()).isNull();
        assertThat(item.getRequest()).isNull();
    }

    // Тестируем equals()
    @Test
    void shouldTestEqualsSameInstance() {
        assertThat(item).isEqualTo(item);
    }

    @Test
    void shouldTestEqualsNull() {
        assertThat(item).isNotEqualTo(null);
    }

    @Test
    void shouldTestEqualsDifferentClass() {
        Object obj = new Object();
        assertThat(item).isNotEqualTo(obj);
    }

    @Test
    void shouldTestEqualsSameId() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertThat(item1).isEqualTo(item2);
    }

    @Test
    void shouldTestEqualsDifferentId() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(2L);

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldTestEqualsIdIsNull() {
        Item item1 = new Item();
        item1.setId(null);

        Item item2 = new Item();
        item2.setId(null);

        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldTestHashCodeSameClass() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void shouldTestHashCodeConsistency() {
        int hashCode = item.hashCode();
        assertThat(item.hashCode()).isEqualTo(hashCode);
    }

    @Test
    void shouldTestToString() {
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Электрическая дрель");
        item.setAvailable(false);

        String expected = "Item(id=1, name=Дрель, description=Электрическая дрель, available=false";
        assertThat(item.toString()).contains(expected);
    }

    // Тестируем аннотации JPA
    @Test
    void shouldTestIdAnnotation() throws NoSuchFieldException {
        var idField = item.getClass().getDeclaredField("id");
        var idAnnotation = idField.getAnnotation(Id.class);
        var genAnnotation = idField.getAnnotation(GeneratedValue.class);

        assertThat(idAnnotation).isNotNull();
        assertThat(genAnnotation).isNotNull();
        assertThat(genAnnotation.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    @Test
    void shouldTestIsAvailableColumnAnnotation() throws NoSuchFieldException {
        var field = item.getClass().getDeclaredField("available");
        var columnAnnotation = field.getAnnotation(Column.class);

        assertThat(columnAnnotation).isNotNull();
        assertThat(columnAnnotation.name()).isEqualTo("is_available");
    }

    @Test
    void shouldTestOwnerRelationship() throws NoSuchFieldException {
        var field = item.getClass().getDeclaredField("owner");
        var manyToOne = field.getAnnotation(ManyToOne.class);

        assertThat(manyToOne).isNotNull();
        assertThat(manyToOne.fetch()).isEqualTo(FetchType.LAZY);
    }

    @Test
    void shouldTestRequestRelationship() throws NoSuchFieldException {
        var field = item.getClass().getDeclaredField("request");
        var manyToOne = field.getAnnotation(ManyToOne.class);

        assertThat(manyToOne).isNotNull();
        assertThat(manyToOne.fetch()).isEqualTo(FetchType.LAZY);
    }
}
