package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.projections.ItemShort;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.setup.GenericRepositoryTest;
import ru.practicum.shareit.utils.CommonValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRepositoryTest extends GenericRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private ItemEntity itemEntityInDb;

    private final Pageable page = CommonValidator.getPageRequest(
            Optional.of(0), Optional.of(20), Optional.empty()
    );

    @BeforeEach
    void setUp_Two() {
        initTestVariable(true, false, true);
        itemEntityInDb = itemRepository.save(itemEntity);
    }

    @Test
    void saveItem() {
        assertNotNull(itemEntityInDb);
        assertEquals(itemEntityInDb.getId(), itemEntity.getId());
        assertEquals(itemEntityInDb.getName(), itemEntity.getName());
        assertEquals(itemEntityInDb.getDescription(), itemEntity.getDescription());
        assertEquals(itemEntityInDb.getUser(), itemEntity.getUser());
        assertEquals(itemEntityInDb.isAvailable(), itemEntity.isAvailable());
    }

    @Test
    void findItem() {
        Optional<ItemEntity> result = itemRepository.findById(itemEntityInDb.getId());

        assertNotNull(result);
        assertEquals(result.get().getId(), itemEntity.getId());
        assertEquals(result.get().getName(), itemEntity.getName());
        assertEquals(result.get().getDescription(), itemEntity.getDescription());
        assertEquals(result.get().getUser(), itemEntity.getUser());
        assertEquals(result.get().isAvailable(), itemEntity.isAvailable());
    }

    @Test
    void findByUserId() {
        List<ItemEntity> result = itemRepository.findByUserId(id, page);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), itemEntity.getId());
        assertEquals(result.get(0).getName(), itemEntity.getName());
        assertEquals(result.get(0).getDescription(), itemEntity.getDescription());
        assertEquals(result.get(0).getUser(), itemEntity.getUser());
        assertEquals(result.get(0).isAvailable(), itemEntity.isAvailable());
    }

    @Test
    void findAllItemsByRequest() {
        itemRequestRepository.save(itemRequestEntity);
        itemEntity.setRequest(itemRequestEntity);
        itemRepository.save(itemEntity);
        List<ItemEntity> result = itemRepository.findAllItemsByRequest(itemRequestEntity);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), itemEntity.getId());
        assertEquals(result.get(0).getName(), itemEntity.getName());
        assertEquals(result.get(0).getDescription(), itemEntity.getDescription());
        assertEquals(result.get(0).getUser(), itemEntity.getUser());
        assertEquals(result.get(0).isAvailable(), itemEntity.isAvailable());
        assertEquals(result.get(0).getRequest(), itemRequestEntity);
    }

    @Test
    void getListSearchItemShort() {
        itemRequestRepository.save(itemRequestEntity);
        itemEntity.setRequest(itemRequestEntity);
        itemRepository.save(itemEntity);
        List<ItemShort> result = itemRepository.getListSearchItemShort(itemEntity.getName(), page);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), itemEntity.getId());
        assertEquals(result.get(0).getName(), itemEntity.getName());
        assertEquals(result.get(0).getDescription(), itemEntity.getDescription());
        assertEquals(result.get(0).isAvailable(), itemEntity.isAvailable());
    }

}
