package ru.practicum.shareit.model;

import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class BookingModelTests {
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
    }

    // Тестируем сеттеры и геттеры
    @Test
    void shouldTestSettersAndGetters() {
        booking.setId(1L);

        LocalDateTime start = LocalDateTime.of(2026, 2, 5, 10, 0);
        booking.setStartDate(start);

        LocalDateTime end = LocalDateTime.of(2026, 2, 9, 18, 0);
        booking.setEndDate(end);

        Item item = new Item();
        item.setId(10L);
        booking.setItem(item);

        User booker = new User();
        booker.setId(100L);
        booking.setBooker(booker);

        booking.setStatus(BookingStatus.APPROVED);

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getStartDate()).isEqualTo(start);
        assertThat(booking.getEndDate()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldTestNoArgsConstructor() {
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNull();
        assertThat(booking.getStartDate()).isNull();
        assertThat(booking.getEndDate()).isNull();
        assertThat(booking.getItem()).isNull();
        assertThat(booking.getBooker()).isNull();
        assertThat(booking.getStatus()).isNull();
    }

    // Тестируем equals()
    @Test
    void shouldTestEqualsSameInstance() {
        assertThat(booking).isEqualTo(booking);
    }

    @Test
    void shouldTestEqualsNull() {
        assertThat(booking).isNotEqualTo(null);
    }

    @Test
    void shouldTestEqualsDifferentClass() {
        Object obj = new Object();
        assertThat(booking).isNotEqualTo(obj);
    }

    @Test
    void shouldTestEqualsSameId() {
        Booking b1 = new Booking();
        b1.setId(1L);

        Booking b2 = new Booking();
        b2.setId(1L);

        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void shouldTestEqualsDifferentId() {
        Booking b1 = new Booking();
        b1.setId(1L);

        Booking b2 = new Booking();
        b2.setId(2L);

        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void shouldTestEqualsIdIsNull() {
        Booking b1 = new Booking();
        b1.setId(null);

        Booking b2 = new Booking();
        b2.setId(null);

        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void shouldTestHashCodeSameClass() {
        Booking b1 = new Booking();
        b1.setId(1L);

        Booking b2 = new Booking();
        b2.setId(1L);

        assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
    }

    @Test
    void shouldTestHashCodeConsistency() {
        int hashCode = booking.hashCode();
        assertThat(booking.hashCode()).isEqualTo(hashCode);
    }

    @Test
    void shouldTestToString() {
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.of(2026, 2, 5, 10, 0));
        booking.setEndDate(LocalDateTime.of(2026, 2, 9, 18, 0));
        booking.setStatus(BookingStatus.WAITING);

        String expected = "Booking(id=1, startDate=2026-02-05T10:00, endDate=2026-02-09T18:00, status=WAITING";
        assertThat(booking.toString()).contains(expected);

        assertThat(booking.toString()).doesNotContain("item=");
        assertThat(booking.toString()).doesNotContain("booker=");
    }

    // Тестируем аннотации JPA
    @Test
    void shouldTestIdAnnotation() throws NoSuchFieldException {
        var idField = booking.getClass().getDeclaredField("id");
        var idAnnotation = idField.getAnnotation(Id.class);
        var genAnnotation = idField.getAnnotation(GeneratedValue.class);

        assertThat(idAnnotation).isNotNull();
        assertThat(genAnnotation).isNotNull();
        assertThat(genAnnotation.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    @Test
    void shouldTestItemRelationship() throws NoSuchFieldException {
        var field = booking.getClass().getDeclaredField("item");
        var manyToOne = field.getAnnotation(ManyToOne.class);

        assertThat(manyToOne).isNotNull();
        assertThat(manyToOne.fetch()).isEqualTo(FetchType.LAZY);
    }

    @Test
    void shouldTestBookerRelationship() throws NoSuchFieldException {
        var field = booking.getClass().getDeclaredField("booker");
        var manyToOne = field.getAnnotation(ManyToOne.class);

        assertThat(manyToOne).isNotNull();
        assertThat(manyToOne.fetch()).isEqualTo(FetchType.LAZY);
    }

    @Test
    void shouldTestStatusEnumeratedAnnotation() throws NoSuchFieldException {
        var field = booking.getClass().getDeclaredField("status");
        var enumAnnotation = field.getAnnotation(Enumerated.class);

        assertThat(enumAnnotation).isNotNull();
        assertThat(enumAnnotation.value()).isEqualTo(EnumType.STRING);
    }
}
