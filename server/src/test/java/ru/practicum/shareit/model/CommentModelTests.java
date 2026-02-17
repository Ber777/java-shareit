package ru.practicum.shareit.model;

import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.model.User;

import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentModelTests {
    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
    }

    // Тестируем сеттеры и геттеры
    @Test
    void shouldTestSettersAndGetters() {
        comment.setId(1L);
        comment.setText("Хорошая вещь!");

        Item item = new Item();
        item.setId(10L);
        comment.setItem(item);

        User author = new User();
        author.setId(100L);
        comment.setAuthor(author);

        LocalDateTime now = LocalDateTime.now();
        comment.setCreated(now);

        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Хорошая вещь!");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isEqualTo(now);
    }

    @Test
    void shouldTestNoArgsConstructor() {
        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isNull();
        assertThat(comment.getText()).isNull();
        assertThat(comment.getItem()).isNull();
        assertThat(comment.getAuthor()).isNull();
        assertThat(comment.getCreated()).isNull();
    }

    // Тестируем equals()
    @Test
    void shouldTestEqualsSameInstance() {
        assertThat(comment).isEqualTo(comment);
    }

    @Test
    void shouldTestEqualsNull() {
        assertThat(comment).isNotEqualTo(null);
    }

    @Test
    void shouldTestEqualsDifferentClass() {
        Object obj = new Object();
        assertThat(comment).isNotEqualTo(obj);
    }

    @Test
    void shouldTestEqualsSameId() {
        Comment c1 = new Comment();
        c1.setId(1L);

        Comment c2 = new Comment();
        c2.setId(1L);

        assertThat(c1).isEqualTo(c2);
    }

    @Test
    void shouldTestEqualsDifferentId() {
        Comment c1 = new Comment();
        c1.setId(1L);

        Comment c2 = new Comment();
        c2.setId(2L);

        assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    void shouldTestEqualsIdIsNull() {
        Comment c1 = new Comment();
        c1.setId(null);

        Comment c2 = new Comment();
        c2.setId(null);

        assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    void shouldTestHashCodeSameClass() {
        Comment c1 = new Comment();
        c1.setId(1L);

        Comment c2 = new Comment();
        c2.setId(1L);

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void shouldTestHashCodeConsistency() {
        int hashCode = comment.hashCode();
        assertThat(comment.hashCode()).isEqualTo(hashCode);
    }

    @Test
    void shouldTestToString() {
        comment.setId(1L);
        comment.setText("Отлично!");
        comment.setCreated(LocalDateTime.of(2026, 2, 9, 12, 0));

        String expected = "Comment(id=1, text=Отлично!, created=2026-02-09T12:00";
        assertThat(comment.toString()).contains(expected);

        assertThat(comment.toString()).doesNotContain("item=");
        assertThat(comment.toString()).doesNotContain("author=");
    }

    // Тестируем аннотации JPA
    @Test
    void shouldTestIdAnnotation() throws NoSuchFieldException {
        var idField = comment.getClass().getDeclaredField("id");
        var idAnnotation = idField.getAnnotation(Id.class);
        var genAnnotation = idField.getAnnotation(GeneratedValue.class);

        assertThat(idAnnotation).isNotNull();
        assertThat(genAnnotation).isNotNull();
        assertThat(genAnnotation.strategy()).isEqualTo(GenerationType.IDENTITY);
    }

    @Test
    void shouldTestCreatedColumnAnnotation() throws NoSuchFieldException {
        var field = comment.getClass().getDeclaredField("created");
        var columnAnnotation = field.getAnnotation(Column.class);

        assertThat(columnAnnotation).isNotNull();
        assertThat(columnAnnotation.name()).isEqualTo("created_at");
    }

    @Test
    void shouldTestItemRelationship() throws NoSuchFieldException {
        var field = comment.getClass().getDeclaredField("item");
        var manyToOne = field.getAnnotation(ManyToOne.class);

        assertThat(manyToOne).isNotNull();
        assertThat(manyToOne.fetch()).isEqualTo(FetchType.LAZY);
    }

    @Test
    void shouldTestAuthorRelationship() throws NoSuchFieldException {
        var field = comment.getClass().getDeclaredField("author");
        var manyToOne = field.getAnnotation(ManyToOne.class);

        assertThat(manyToOne).isNotNull();
        assertThat(manyToOne.fetch()).isEqualTo(FetchType.LAZY);
    }
}
