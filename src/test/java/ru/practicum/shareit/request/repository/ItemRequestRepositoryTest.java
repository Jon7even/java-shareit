package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.setup.GenericRepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestRepositoryTest extends GenericRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;

    private ItemRequestEntity itemRequestEntityInDb;

    @BeforeEach
    void setUp_Two() {
        initTestVariable(true, false, true);
        itemRequestEntityInDb = requestRepository.save(itemRequestEntity);
    }

    @Test
    void saveItemRequest() {
        assertNotNull(itemRequestEntityInDb);
        assertEquals(itemRequestEntityInDb.getId(), itemRequestEntity.getId());
        assertEquals(itemRequestEntityInDb.getRequestor(), itemRequestEntity.getRequestor());
        assertEquals(itemRequestEntityInDb.getCreated(), itemRequestEntity.getCreated());
        assertEquals(itemRequestEntityInDb.getDescription(), itemRequestEntity.getDescription());
    }

    @Test
    void findItemRequest() {
        Optional<ItemRequestEntity> result = requestRepository.findById(itemRequestEntityInDb.getId());

        assertNotNull(result);
        assertEquals(result.get().getId(), itemRequestEntityInDb.getId());
        assertEquals(result.get().getRequestor(), itemRequestEntityInDb.getRequestor());
        assertEquals(result.get().getCreated(), itemRequestEntityInDb.getCreated());
        assertEquals(result.get().getDescription(), itemRequestEntityInDb.getDescription());
    }

    @Test
    void findAllItemsRequestsByOwner() {
        List<ItemRequestEntity> result = requestRepository.findAllItemsRequestsByOwner(userEntity);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), itemRequestEntity.getId());
        assertEquals(result.get(0).getRequestor(), itemRequestEntity.getRequestor());
        assertEquals(result.get(0).getCreated(), itemRequestEntity.getCreated());
        assertEquals(result.get(0).getDescription(), itemRequestEntity.getDescription());
    }

}
