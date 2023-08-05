package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.setup.GenericServiceTest;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemRequestServiceTest extends GenericServiceTest {
    private ItemRequestService itemRequestService;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void createItemRequest() {
        initTestVariable(true, false, true);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequestEntity);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        ItemRequestCreateTO originalDto = ItemRequestCreateTO.builder()
                .description(itemRequestEntity.getDescription())
                .build();

        initOptionalVariable();
        ItemRequestResponseTO result = itemRequestService.createItemRequest(originalDto, idUserOptional);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo(originalDto.getDescription()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), equalTo((Collections.emptyList())));
        verify(itemRequestRepository, times(1)).save(any(ItemRequestEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

}
