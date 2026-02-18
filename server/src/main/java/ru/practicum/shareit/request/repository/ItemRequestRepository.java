package ru.practicum.shareit.request.repository;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorId(Long requestorId, Sort sort);

    Page<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(
            Long requestorId, Pageable pageable);
}
