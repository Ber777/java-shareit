package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Создание нового запроса вещи: {}", itemRequestDto);
        checkUserExists(userId);

        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepository.findById(userId).orElseThrow());
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("Сохранение запроса вещи: {}", itemRequest);

        return toItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Получение всех запросов пользователя с id: {}", userId);
        checkUserExists(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(userId, sort);

        log.info("Найдено {} запросов пользователя с id: {}", requests.size(), userId);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.info("Получение всех запросов: {}", userId);
        checkUserExists(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        int firstPage = from / size;
        PageRequest pageRequest = PageRequest.of(firstPage, size, sort);
        List<ItemRequest> requests = itemRequestRepository.findAll(pageRequest).getContent();

        log.info("Найдено {} запросов", requests.size());
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Полечение запроса по id: {}", requestId);
        checkUserExists(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id: " + requestId));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemDto = items.stream()
                .map(ItemMapper::toItemDto)
                .toList();

        ItemRequestDto requestDto = toItemRequestDto(request);
        requestDto.setItems(itemDto);

        log.info("Найден запрос: {}", requestDto);
        return requestDto;
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Не найден пользователь с id: " + userId);
    }
}
