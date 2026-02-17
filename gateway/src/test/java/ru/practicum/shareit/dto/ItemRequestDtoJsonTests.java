package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTests {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    // десериализация JSON в DTO
    @Test
    void shouldDeserializeFromJsonCorrectly() throws Exception {
        String jsonString = """
            {
                "id": 1,
                "userId": 2,
                "description": "Тестовый запрос",
                "created": "2026-02-16T11:30:00",
                "items": [
                    {
                        "id": 10,
                        "name": "Предмет 1"
                    },
                    {
                        "id": 20,
                        "name": "Предмет 2"
                    }
                ]
            }
        """;

        ObjectContent<ItemRequestDto> parsed = json.parse(jsonString);
        ItemRequestDto dto = parsed.getObject();

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUserId()).isEqualTo(2L);
        assertThat(dto.getDescription()).isEqualTo("Тестовый запрос");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 2, 16, 11, 30, 0));

        assertThat(dto.getItems().size()).isEqualTo(2);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(10L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Предмет 1");
        assertThat(dto.getItems().get(1).getId()).isEqualTo(20L);
        assertThat(dto.getItems().get(1).getName()).isEqualTo("Предмет 2");
    }

    // десериализация неполного JSON (пропущены необязательные поля)
    @Test
    void shouldDeserializePartialJson() throws Exception {
        String partialJson = """
            {
                "id": 1,
                "description": "Частичный запрос"
            }
        """;

        ObjectContent<ItemRequestDto> parsed = json.parse(partialJson);
        ItemRequestDto dto = parsed.getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Частичный запрос");
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCreated()).isNull();
        assertThat(dto.getItems()).isNull();
    }
}
