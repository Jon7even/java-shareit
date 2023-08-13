package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.projections.ItemShort;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.setup.GenericServiceTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest extends GenericServiceTest {
    @Mock
    private CommentRepository commentRepository;

    private ItemService itemService;

    private ItemUpdateTO itemUpdateTO;

    private ItemRequestListTO itemRequestListTO;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceIml(
                itemRepository, userRepository, bookingRepository, itemRequestRepository, commentRepository
        );
    }

    private void initItemRequestList() {
        initOptionalVariable();
        itemRequestListTO = ItemRequestListTO.builder()
                .idUser(id)
                .from(fromOptional)
                .size(sizeOptional)
                .build();
    }

    private void initItemUpdateTO() {
        initTestVariable(true, false, false);
        itemUpdateTO = ItemUpdateTO.builder()
                .name("UpdateItem")
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .build();
    }

    @Test
    void createItem_whenIdUserNull() {
        initTestVariable(true, false, false);
        ItemCreateTO originalDto = ItemCreateTO.builder()
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .build();
        initOptionalVariable();

        assertThrows(IncorrectParameterException.class, () -> itemService.createItem(
                originalDto, Optional.empty()));

        verify(itemRepository, never()).save(any(ItemEntity.class));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void createItem_valid_withoutRequest() {
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

    @Test
    void createItem_valid_withRequest() {
        initTestVariable(true, false, true);
        itemEntity.setRequest(itemRequestEntity);
        when(itemRepository.save(any()))
                .thenReturn(itemEntity);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestEntity));

        ItemCreateTO originalDto = ItemCreateTO.builder()
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .requestId(itemRequestEntity.getId())
                .build();
        initOptionalVariable();
        ItemResponseTO result = itemService.createItem(originalDto, idUserOptional);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(originalDto.getName()));
        assertThat(result.getDescription(), equalTo(originalDto.getDescription()));
        assertThat(result.getAvailable(), equalTo(originalDto.getAvailable()));
        assertThat(result.getRequestId(), equalTo(itemRequestEntity.getId()));
        verify(itemRepository, times(1)).save(any(ItemEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void createItem_whenRequestNotExist() {
        initTestVariable(true, false, true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ItemCreateTO originalDto = ItemCreateTO.builder()
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .requestId(itemRequestEntity.getId())
                .build();
        initOptionalVariable();
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> itemService.createItem(
                originalDto, idUserOptional));
        String errorMessage = "[ItemRequest with [idItemRequest=" + itemRequestEntity.getId() + "]] not found";

        assertThat(ex.getMessage(), equalTo(errorMessage));
        verify(itemRepository, never()).save(any(ItemEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void findItemById_whenIdUserNull() {
        initOptionalVariable();
        assertThrows(IncorrectParameterException.class, () -> itemService.findItemById(
                Optional.empty(), idItemOptional));

        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findAllCommentsByItem(any());
        verify(bookingRepository, never()).findByItemOrderByStart(any());
    }

    @Test
    void findItemById_whenItemNull() {
        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.findItemById(
                idUserOptional, Optional.empty()));

        verify(userRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findAllCommentsByItem(any());
        verify(bookingRepository, never()).findByItemOrderByStart(any());
    }

    @Test
    void findItemById_whenItemAndUserExist() {
        initTestVariable(true, true, false);
        List<CommentEntity> comments = List.of(commentEntity);
        List<BookingEntity> listBookingByItem = List.of(bookingEntity);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));
        when(commentRepository.findAllCommentsByItem(itemEntity))
                .thenReturn(comments);
        when(bookingRepository.findByItemOrderByStart(itemEntity))
                .thenReturn(listBookingByItem);

        initOptionalVariable();
        ItemResponseBookingAndCommentTO result = itemService.findItemById(idUserOptional, idItemOptional);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(itemEntity.getId()));
        assertThat(result.getName(), equalTo(itemEntity.getName()));
        assertThat(result.getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemEntity.isAvailable()));
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), notNullValue());
        assertThat(result.getLastBooking().getBookerId(), equalTo(bookingEntity.getId()));
        assertThat(result.getComments().size(), equalTo(1));
        assertThat(result.getComments().get(0).getId(), equalTo(commentEntity.getId()));
        assertThat(result.getComments().get(0).getText(), equalTo(commentEntity.getText()));
        assertThat(result.getComments().get(0).getAuthorName(), equalTo(commentEntity.getUser().getName()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllCommentsByItem(any());
        verify(bookingRepository, times(1)).findByItemOrderByStart(any());
    }

    @Test
    void findItemById_whenUserNotExist() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.findItemById(idUserOptional, idItemOptional));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findAllCommentsByItem(any());
        verify(bookingRepository, never()).findByItemOrderByStart(any());
    }

    @Test
    void findItemById_whenUserExistButItemNotExist() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.findItemById(idUserOptional, idItemOptional));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).findAllCommentsByItem(any());
        verify(bookingRepository, never()).findByItemOrderByStart(any());
    }

    @Test
    void updateItem_whenUserNotExist() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        initItemUpdateTO();
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(
                idUserOptional, idItemOptional, itemUpdateTO
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(ItemEntity.class));
    }

    @Test
    void updateItem_whenUserExistButItemNotExist() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        initItemUpdateTO();
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(
                idUserOptional, idItemOptional, itemUpdateTO
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any(ItemEntity.class));
    }

    @Test
    void updateItem_whenUserNotOwnerItem() {
        initTestVariable(false, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        initTestVariable(true, false, false);
        userEntity.setId(2L);
        itemEntity.setUser(userEntity);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));

        initOptionalVariable();
        initItemUpdateTO();
        assertThrows(AccessDeniedException.class, () -> itemService.updateItem(
                idUserOptional, idItemOptional, itemUpdateTO
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any(ItemEntity.class));
    }

    @Test
    void updateItem_whenUpdateName() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));

        initItemUpdateTO();
        itemEntity.setName(itemUpdateTO.getName());
        when(itemRepository.save(any()))
                .thenReturn(itemEntity);

        initOptionalVariable();
        ItemResponseTO result = itemService.updateItem(idUserOptional, idItemOptional, itemUpdateTO);

        assertThat(result.getId(), equalTo(idUserOptional.get()));
        assertThat(result.getName(), equalTo(itemUpdateTO.getName()));
        assertThat(result.getDescription(), equalTo(itemUpdateTO.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemUpdateTO.getAvailable()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(ItemEntity.class));
    }

    @Test
    void updateItem_whenUpdateDescription() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));

        initItemUpdateTO();
        itemEntity.setName(itemUpdateTO.getName());
        itemUpdateTO.setName(itemEntity.getName());
        itemUpdateTO.setDescription("UpdateDescription");
        itemEntity.setDescription("UpdateDescription");
        when(itemRepository.save(any()))
                .thenReturn(itemEntity);

        initOptionalVariable();
        ItemResponseTO result = itemService.updateItem(idUserOptional, idItemOptional, itemUpdateTO);

        assertThat(result.getId(), equalTo(idUserOptional.get()));
        assertThat(result.getName(), equalTo(itemUpdateTO.getName()));
        assertThat(result.getDescription(), equalTo(itemUpdateTO.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemUpdateTO.getAvailable()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(ItemEntity.class));
    }

    @Test
    void updateItem_whenUpdateAvailable() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));

        initItemUpdateTO();
        itemEntity.setName(itemUpdateTO.getName());
        itemUpdateTO.setName(itemEntity.getName());
        itemUpdateTO.setAvailable(false);
        itemEntity.setAvailable(false);
        when(itemRepository.save(any()))
                .thenReturn(itemEntity);

        initOptionalVariable();
        ItemResponseTO result = itemService.updateItem(idUserOptional, idItemOptional, itemUpdateTO);

        assertThat(result.getId(), equalTo(idUserOptional.get()));
        assertThat(result.getName(), equalTo(itemUpdateTO.getName()));
        assertThat(result.getDescription(), equalTo(itemUpdateTO.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemUpdateTO.getAvailable()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(ItemEntity.class));
    }

    @Test
    void getAllItemsByUserId_whenUserNotExist() {
        initTestVariable(false, false, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        initItemRequestList();
        assertThrows(EntityNotFoundException.class, () -> itemService.getAllItemsByUserId(itemRequestListTO));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).findByUserId(anyLong(), any());
        verify(commentRepository, never()).findAllCommentsByItem(any());
        verify(bookingRepository, never()).findByItemOrderByStart(any());
    }

    @Test
    void getAllItemsByUserId_whenListEmpty() {
        initTestVariable(false, false, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByUserId(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        initItemRequestList();
        List<ItemResponseBookingAndCommentTO> result = itemService.getAllItemsByUserId(itemRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByUserId(anyLong(), any());
        verify(commentRepository, never()).findAllCommentsByItem(any());
        verify(bookingRepository, never()).findByItemOrderByStart(any());
    }

    @Test
    void getAllItemsByUserId_whenListExistWithoutCommentAndBooking() {
        initTestVariable(true, false, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByUserId(anyLong(), any()))
                .thenReturn(List.of(itemEntity));
        when(commentRepository.findAllCommentsByItem(any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemOrderByStart(any()))
                .thenReturn(Collections.emptyList());

        initItemRequestList();
        List<ItemResponseBookingAndCommentTO> result = itemService.getAllItemsByUserId(itemRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        assertThat(result.get(0).getNextBooking(), nullValue());
        assertThat(result.get(0).getLastBooking(), nullValue());
        assertThat(result.get(0).getComments().size(), equalTo(0));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByUserId(anyLong(), any());
        verify(commentRepository, times(1)).findAllCommentsByItem(any());
        verify(bookingRepository, times(1)).findByItemOrderByStart(any());
    }

    @Test
    void getAllItemsByUserId_whenListExistWithBookingWithoutComment() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByUserId(anyLong(), any()))
                .thenReturn(List.of(itemEntity));
        when(commentRepository.findAllCommentsByItem(any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findByItemOrderByStart(any()))
                .thenReturn(List.of(bookingEntity));

        initItemRequestList();
        List<ItemResponseBookingAndCommentTO> result = itemService.getAllItemsByUserId(itemRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        assertThat(result.get(0).getNextBooking(), nullValue());
        assertThat(result.get(0).getLastBooking(), notNullValue());
        assertThat(result.get(0).getLastBooking().getBookerId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getComments().size(), equalTo(0));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByUserId(anyLong(), any());
        verify(commentRepository, times(1)).findAllCommentsByItem(any());
        verify(bookingRepository, times(1)).findByItemOrderByStart(any());
    }

    @Test
    void getAllItemsByUserId_whenListExistWithCommentWithoutBooking() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByUserId(anyLong(), any()))
                .thenReturn(List.of(itemEntity));
        when(commentRepository.findAllCommentsByItem(any()))
                .thenReturn(List.of(commentEntity));
        when(bookingRepository.findByItemOrderByStart(any()))
                .thenReturn(Collections.emptyList());

        initItemRequestList();
        List<ItemResponseBookingAndCommentTO> result = itemService.getAllItemsByUserId(itemRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        assertThat(result.get(0).getNextBooking(), nullValue());
        assertThat(result.get(0).getLastBooking(), nullValue());
        assertThat(result.get(0).getComments().size(), equalTo(1));
        assertThat(result.get(0).getComments().get(0).getId(), equalTo(commentEntity.getId()));
        assertThat(result.get(0).getComments().get(0).getText(), equalTo(commentEntity.getText()));
        assertThat(result.get(0).getComments().get(0).getAuthorName(), equalTo(commentEntity.getUser().getName()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByUserId(anyLong(), any());
        verify(commentRepository, times(1)).findAllCommentsByItem(any());
        verify(bookingRepository, times(1)).findByItemOrderByStart(any());
    }

    @Test
    void getAllItemsByUserId_whenListExistWithCommentAndBooking() {
        initTestVariable(true, true, false);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findByUserId(anyLong(), any()))
                .thenReturn(List.of(itemEntity));
        when(commentRepository.findAllCommentsByItem(any()))
                .thenReturn(List.of(commentEntity));
        when(bookingRepository.findByItemOrderByStart(any()))
                .thenReturn(List.of(bookingEntity));

        initItemRequestList();
        List<ItemResponseBookingAndCommentTO> result = itemService.getAllItemsByUserId(itemRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        assertThat(result.get(0).getNextBooking(), nullValue());
        assertThat(result.get(0).getLastBooking(), notNullValue());
        assertThat(result.get(0).getLastBooking().getBookerId(), equalTo(bookingEntity.getId()));
        assertThat(result.get(0).getComments().size(), equalTo(1));
        assertThat(result.get(0).getComments().get(0).getId(), equalTo(commentEntity.getId()));
        assertThat(result.get(0).getComments().get(0).getText(), equalTo(commentEntity.getText()));
        assertThat(result.get(0).getComments().get(0).getAuthorName(), equalTo(commentEntity.getUser().getName()));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findByUserId(anyLong(), any());
        verify(commentRepository, times(1)).findAllCommentsByItem(any());
        verify(bookingRepository, times(1)).findByItemOrderByStart(any());
    }

    @Test
    void getListSearchItem_whenTextNull() {
        initItemRequestList();
        itemRequestListTO.setText(Optional.empty());

        assertThrows(IncorrectParameterException.class, () -> itemService.getListSearchItem(itemRequestListTO));

        verify(userRepository, never()).existsById(anyLong());
        verify(itemRepository, never()).getListSearchItemShort(anyString(), any());
    }

    @Test
    void getListSearchItem_whenUserNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        initItemRequestList();
        itemRequestListTO.setText(Optional.of("test"));

        assertThrows(EntityNotFoundException.class, () -> itemService.getListSearchItem(itemRequestListTO));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).getListSearchItemShort(anyString(), any());
    }

    @Test
    void getListSearchItem_whenTextEmpty() {
        initItemRequestList();
        itemRequestListTO.setText(Optional.of(" "));

        List<ItemShort> listItemShort = itemService.getListSearchItem(itemRequestListTO);

        assertThat(listItemShort, notNullValue());
        assertThat(listItemShort.size(), equalTo(0));
        verify(userRepository, never()).existsById(anyLong());
        verify(itemRepository, never()).getListSearchItemShort(anyString(), any());
    }

    @Test
    void getListSearchItem_whenListEmpty() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.getListSearchItemShort(anyString(), any()))
                .thenReturn(Collections.emptyList());
        initItemRequestList();
        itemRequestListTO.setText(Optional.of("text"));

        List<ItemShort> listItemShort = itemService.getListSearchItem(itemRequestListTO);

        assertThat(listItemShort, notNullValue());
        assertThat(listItemShort.size(), equalTo(0));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).getListSearchItemShort(anyString(), any());
    }

    @Test
    void getListSearchItem__whenListOfOne() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        initTestVariable(true, false, false);
        ItemShort itemShort = ItemShort.builder()
                .id(itemEntity.getId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .available(itemEntity.isAvailable())
                .build();

        when(itemRepository.getListSearchItemShort(anyString(), any()))
                .thenReturn(List.of(itemShort));
        initItemRequestList();
        itemRequestListTO.setText(Optional.of(itemEntity.getName()));

        List<ItemShort> result = itemService.getListSearchItem(itemRequestListTO);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).isAvailable(), equalTo(itemEntity.isAvailable()));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).getListSearchItemShort(anyString(), any());
    }

    @Test
    void deleteItemById_whenUserNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItemById(idUserOptional, idItemOptional));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteItemById_whenUserExistButItemNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsById(anyLong()))
                .thenReturn(false);

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItemById(idUserOptional, idItemOptional));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).existsById(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteItemById_whenUserAndItemExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsById(anyLong()))
                .thenReturn(true, false);


        initOptionalVariable();
        itemService.deleteItemById(idUserOptional, idItemOptional);

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(2)).existsById(anyLong());
        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteItemById_whenUserAndItemExistButErrorDb() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsById(anyLong()))
                .thenReturn(true);

        initOptionalVariable();
        assertThrows(EntityNotDeletedException.class, () -> itemService.deleteItemById(idUserOptional, idItemOptional));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(2)).existsById(anyLong());
        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void createComment_whenUserNotExist() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(
                idUserOptional, idItemOptional, CommentCreateTO.builder().text("test").build()
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).getBookingByOwnerBeforeCurrentTime(any(), any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenUserExistButItemNotExist() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(
                idUserOptional, idItemOptional, CommentCreateTO.builder().text("test").build()
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).getBookingByOwnerBeforeCurrentTime(any(), any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenUserAndItemExistButNotCompletedBookings() {
        initTestVariable(true, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));
        when(bookingRepository.getBookingByOwnerBeforeCurrentTime(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        initOptionalVariable();
        assertThrows(NoCompletedBookingsException.class, () -> itemService.createComment(
                idUserOptional, idItemOptional, CommentCreateTO.builder().text("test").build()
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingByOwnerBeforeCurrentTime(any(), any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_whenAllFieldValidated() {
        initTestVariable(true, true, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemEntity));
        when(bookingRepository.getBookingByOwnerBeforeCurrentTime(any(), any(), any()))
                .thenReturn(List.of(bookingEntity));
        when(commentRepository.save(any()))
                .thenReturn(commentEntity);

        initOptionalVariable();
        CommentResponseTO result = itemService.createComment(
                idUserOptional, idItemOptional, CommentCreateTO.builder().text("test").build()
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(commentEntity.getId()));
        assertThat(result.getText(), equalTo(commentEntity.getText()));
        assertThat(result.getAuthorName(), equalTo(commentEntity.getUser().getName()));
        assertThat(result.getCreated(), notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).getBookingByOwnerBeforeCurrentTime(any(), any(), any());
        verify(commentRepository, times(1)).save(any());
    }

}
