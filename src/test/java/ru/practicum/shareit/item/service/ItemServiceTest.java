package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.item.dto.ItemCreateTO;
import ru.practicum.shareit.item.dto.ItemResponseTO;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.setup.GenericServiceTest;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest extends GenericServiceTest {
    @Mock
    private CommentRepository commentRepository;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceIml(
                itemRepository, userRepository, bookingRepository, itemRequestRepository, commentRepository
        );
    }

    @Test
    void createItem() {
        initTestVariable(true, false, false);
        when(itemRepository.save(any()))
                .thenReturn(itemEntity);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        ItemCreateTO originalDto = ItemCreateTO.builder()
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .build();
        initOptionalVariable();
        ItemResponseTO result = itemService.createItem(originalDto, idUserOptional);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(originalDto.getName()));
        assertThat(result.getDescription(), equalTo(originalDto.getDescription()));
        assertThat(result.getAvailable(), equalTo(originalDto.getAvailable()));
        verify(itemRepository, times(1)).save(any(ItemEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

}
