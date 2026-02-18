package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.item.CommentMapper.*;
import static ru.practicum.shareit.item.ItemMapper.*;
import static ru.practicum.shareit.user.UserMapper.toUser;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = toUser(userService.getUserById(userId));
        log.info("Создание вещи: {}", itemDto);
        Item item = toItem(itemDto);
        item.setOwner(user);
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            item.setRequest(requestRepository.findById(requestId).get());
        }
        Item savedItem = itemRepository.save(item);
        log.info("Вещь создана: {}", item);
        return toItemDto(savedItem);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        Collection<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndDateIsBefore(
                itemId, userId, LocalDateTime.now()
        );

        if (bookings.isEmpty()) throw new ValidationException("Пользователь не забронировал эту вещь");

        Comment comment = toComment(commentDto, item, user);
        Comment savedComment = commentRepository.save(comment);

        return toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));

        log.info("Обновление вещи с id: {} для пользователя с id: {}", itemId, userId);
        Item updatingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        if (Objects.nonNull(itemDto.getName())) updatingItem.setName(itemDto.getName());
        if (Objects.nonNull(itemDto.getDescription())) updatingItem.setDescription(itemDto.getDescription());
        if (Objects.nonNull(itemDto.getAvailable())) updatingItem.setAvailable(itemDto.getAvailable());

        log.info("Обновленная вещь: {}", updatingItem);
        return toItemDto(updatingItem);
    }

    @Override
    public ItemDtoResponse getItem(Long userId, Long itemId) {
        log.info("Получение вещи с id: {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + itemId));

        if (!item.getOwner().getId().equals(userId)) {
            Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());
            Collection<Comment> comments = commentRepository.findByItemId(item.getId());
            return toItemDtoResponse(item, bookings, comments);
        }

        log.info("Найдена вещь: {}", item);
        return toItemDtoResponseWithBookingsAndComments(item);
    }

    @Override
    public Collection<ItemDtoResponse> getUserItems(Long userId) {
        log.info("Получение вещей пользователя с id: {}", userId);
        List<Item> items = new ArrayList<>(itemRepository.findAllByOwnerId(userId));
        List<ItemDtoResponse> itemDto = items.stream().map(this::toItemDtoResponseWithBookingsAndComments).toList();

        log.info("Найдены {} вещи пользователя с id: {}", items.size(), userId);
        return itemDto;
    }

    @Override
    public Collection<ItemDtoResponse> getItemsByText(String text) {
        if (text.trim().isEmpty()) return Collections.emptyList();

        log.info("Поиск вещей по тексту: {}", text);

        List<Item> items = itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .toList();

        List<ItemDtoResponse> itemDtos = items.stream().map(this::toItemDtoResponseWithBookingsAndComments).toList();

        log.info("Найдены {} вещи", items.size());
        return itemDtos;
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        log.info("Удаление вещи с id: {}", itemId);
        itemRepository.deleteById(itemId);
        log.info("Вещь удалена: {}", itemId);
    }

    private ItemDtoResponse toItemDtoResponseWithBookingsAndComments(Item item) {
        Collection<Booking> bookings = bookingRepository.findByItemId(item.getId());
        Collection<Comment> comments = commentRepository.findByItemId(item.getId());

        LocalDateTime lastBookingDate = bookings.stream()
                .map(Booking::getEndDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime nextBookingDate = bookings.stream()
                .map(Booking::getStartDate)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        ItemDtoResponse itemDtoResponse = toItemDtoResponse(item, bookings, comments);
        itemDtoResponse.setLastBooking(lastBookingDate);
        itemDtoResponse.setNextBooking(nextBookingDate);
        return itemDtoResponse;
    }
}
