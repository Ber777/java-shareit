package ru.practicum.shareit.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.repository.UserRepository;

import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceImplTests {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager em;

    private Long userId;
    private Long requestId;

    @BeforeEach
    void setUp() {
        // Создаём пользователя
        UserDto userDto = makeUserDto("requestor@example.com", "Иван", "Петров");
        userDto = userService.createUser(userDto);
        userId = userDto.getId();

        // Создаём запрос
        ItemRequestDto requestDto = makeItemRequestDto("Нужен ноутбук для работы");
        ItemRequestDto createdRequest = itemRequestService.createRequest(userId, requestDto);
        requestId = createdRequest.getId();
    }

    // Вспомогательные методы
    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(firstName + " " + lastName);
        return dto;
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        return dto;
    }

    @Test
    void shouldCreateRequest() {
        ItemRequestDto newRequestDto = makeItemRequestDto("Требуется монитор 27\"");

        ItemRequestDto created = itemRequestService.createRequest(userId, newRequestDto);

        // Проверка DTO
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Требуется монитор 27\"");
        assertThat(created.getCreated()).isNotNull();

        // Проверка в БД
        ItemRequest requestInDb = em.find(ItemRequest.class, created.getId());
        assertThat(requestInDb).isNotNull();
        assertThat(requestInDb.getDescription()).isEqualTo("Требуется монитор 27\"");
        assertThat(requestInDb.getRequestor().getId()).isEqualTo(userId);
        assertThat(requestInDb.getCreated()).isNotNull();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnCreateRequest() {
        ItemRequestDto requestDto = makeItemRequestDto("Тест запроса");

        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.createRequest(999L, requestDto);
        });
    }

    @Test
    void shouldGetUserRequests() {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);

        assertThat(requests).isNotEmpty();
        assertThat(requests.size()).isGreaterThanOrEqualTo(1);

        ItemRequestDto foundRequest = requests.getFirst();
        assertThat(foundRequest.getId()).isEqualTo(requestId);
        assertThat(foundRequest.getDescription()).contains("Нужен ноутбук");
        assertThat(foundRequest.getCreated()).isNotNull();
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoRequests() {
        // Создаём нового пользователя без запросов
        UserDto newUserDto = makeUserDto("new@example.com", "Анна", "Сидорова");
        newUserDto = userService.createUser(newUserDto);
        Long newUserId = newUserDto.getId();

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(newUserId);

        assertThat(requests).isEmpty();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnGetUserRequests() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getUserRequests(999L);
        });
    }

    @Test
    void shouldGetAllRequests() {
        // Создаём другого пользователя (не userId)
        UserDto otherUserDto = makeUserDto("other@email.com", "Другой", "Пользователь");
        otherUserDto = userService.createUser(otherUserDto);
        Long otherUserId = otherUserDto.getId();

        // Создаём запрос от другого пользователя (он должен попасть в "все запросы")
        ItemRequestDto requestDto = makeItemRequestDto("Нужен ноутбук");
        ItemRequestDto createdRequest = itemRequestService.createRequest(otherUserId, requestDto);
        Long requestId = createdRequest.getId();

        // Получаем "все запросы" (исключая userId)
        List<ItemRequestDto> allRequests = itemRequestService.getAllRequests(userId, 0, 10);

        // Проверки
        assertThat(allRequests).isNotEmpty();
        assertThat(allRequests).anyMatch(r -> r.getId().equals(requestId));

        ItemRequestDto matchedRequest = allRequests.stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Запрос с ID " + requestId + " не найден в результатах"));

        assertThat(matchedRequest.getDescription()).contains("Нужен ноутбук");
    }

    @Test
    void shouldPaginateAllRequests() {
        // Создаём другого пользователя
        UserDto otherUserDto = makeUserDto("other2@email.com", "Другой", "Пользователь2");
        otherUserDto = userService.createUser(otherUserDto);
        Long otherUserId = otherUserDto.getId();

        // Создаём 4 запроса от другого пользователя
        List<Long> expectedRequestIds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ItemRequestDto requestDto = makeItemRequestDto("Запрос #" + i);
            requestDto.setCreated(LocalDateTime.now().minusMinutes(i));
            ItemRequestDto created = itemRequestService.createRequest(otherUserId, requestDto);
            expectedRequestIds.add(created.getId());
        }

        // Получаем первую страницу (2 элемента)
        List<ItemRequestDto> page1 = itemRequestService.getAllRequests(userId, 0, 2);
        assertThat(page1).hasSize(2);

        // Проверяем, что все id из page1 есть в expectedRequestIds
        Set<Long> page1Ids = page1.stream().map(ItemRequestDto::getId).collect(Collectors.toSet());
        assertThat(page1Ids).containsAnyOf(expectedRequestIds.toArray(new Long[0]));
        assertThat(page1Ids).isSubsetOf(expectedRequestIds);

        // Получаем вторую страницу (2 элемента)
        List<ItemRequestDto> page2 = itemRequestService.getAllRequests(userId, 2, 2);
        assertThat(page2).hasSize(2);

        // Проверяем id второй страницы
        Set<Long> page2Ids = page2.stream().map(ItemRequestDto::getId).collect(Collectors.toSet());
        assertThat(page2Ids).containsAnyOf(expectedRequestIds.toArray(new Long[0]));
        assertThat(page2Ids).isSubsetOf(expectedRequestIds);

        // Проверяем отсутствие пересечений между страницами
        Set<Long> intersection = new HashSet<>(page1Ids);
        intersection.retainAll(page2Ids);
        assertThat(intersection).isEmpty();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnGetAllRequests() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllRequests(999L, 0, 10);
        });
    }

    @Test
    void shouldGetRequestById() {
        ItemRequestDto found = itemRequestService.getRequestById(userId, requestId);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(requestId);
        assertThat(found.getDescription()).contains("Нужен ноутбук");
        assertThat(found.getCreated()).isNotNull();
        assertThat(found.getItems()).isNotNull();  // Поле items должно быть инициализировано
    }

    @Test
    void shouldIncludeItemsInRequestResponse() {
        // Сначала создаём вещь с requestId
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Ноутбук Lenovo");
        itemDto.setDescription("Игровой ноутбук");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId);

        itemService.createItem(userId, itemDto);

        // Получаем запрос по id
        ItemRequestDto response = itemRequestService.getRequestById(userId, requestId);

        // Проверяем, что вещи добавлены в ответ
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().getFirst().getName()).isEqualTo("Ноутбук Lenovo");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequestDoesNotExist() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(userId, 999L);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnGetRequestById() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequestById(999L, requestId);
        });
    }
}
