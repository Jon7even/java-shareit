package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.setup.GenericServiceTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void findItemRequestById_whenUserNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findItemRequestById(
                idUserOptional, idRequestOptional
        ));

        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void findItemRequestById_whenUserExistButItemRequestNotExist() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findItemRequestById(
                idUserOptional, idRequestOptional
        ));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void findItemRequestById_whenListItemsEmpty() {
        initTestVariable(true, false, true);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestEntity));
        when(itemRepository.findAllItemsByRequest(any()))
                .thenReturn(Collections.emptyList());

        initOptionalVariable();
        ItemRequestResponseTO result = itemRequestService.findItemRequestById(
                idUserOptional, idRequestOptional
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(itemRequestEntity.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestEntity.getDescription()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), notNullValue());
        assertThat(result.getItems(), equalTo((Collections.emptyList())));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllItemsByRequest(any());
    }

    @Test
    void findItemRequestById_whenListItemsOfOne() {
        initTestVariable(true, false, true);
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestEntity));
        when(itemRepository.findAllItemsByRequest(any()))
                .thenReturn(List.of(itemEntity));

        initOptionalVariable();
        ItemRequestResponseTO result = itemRequestService.findItemRequestById(
                idUserOptional, idRequestOptional
        );

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(itemRequestEntity.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestEntity.getDescription()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), notNullValue());
        assertThat(result.getItems().size(), equalTo((1)));
        assertThat(result.getItems().get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.getItems().get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.getItems().get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.getItems().get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllItemsByRequest(any());
    }

    @Test
    void getAllItemRequestByIdOwner_whenUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getAllItemRequestByIdOwner(
                idUserOptional
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findAllItemsRequestsByOwner(any());
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void getAllItemRequestByIdOwner_whenListItemRequestsEmpty() {
        initTestVariable(false, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAllItemsRequestsByOwner(any()))
                .thenReturn(Collections.emptyList());

        initOptionalVariable();
        List<ItemRequestResponseTO> result = itemRequestService.getAllItemRequestByIdOwner(idUserOptional);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAllItemsRequestsByOwner(any());
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void getAllItemRequestByIdOwner_whenListItemRequestsOfOneButListItemsEmpty() {
        initTestVariable(false, false, true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAllItemsRequestsByOwner(any()))
                .thenReturn(List.of(itemRequestEntity));
        when(itemRepository.findAllItemsByRequest(any()))
                .thenReturn(Collections.emptyList());

        initOptionalVariable();
        List<ItemRequestResponseTO> result = itemRequestService.getAllItemRequestByIdOwner(idUserOptional);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequestEntity.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestEntity.getDescription()));
        assertThat(result.get(0).getCreated(), notNullValue());
        assertThat(result.get(0).getItems(), notNullValue());
        assertThat(result.get(0).getItems().size(), equalTo(0));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAllItemsRequestsByOwner(any());
        verify(itemRepository, times(1)).findAllItemsByRequest(any());
    }

    @Test
    void getAllItemRequestByIdOwner_whenListItemRequestsAndListItemOfOne() {
        initTestVariable(true, false, true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAllItemsRequestsByOwner(any()))
                .thenReturn(List.of(itemRequestEntity));
        when(itemRepository.findAllItemsByRequest(any()))
                .thenReturn(List.of(itemEntity));

        initOptionalVariable();
        List<ItemRequestResponseTO> result = itemRequestService.getAllItemRequestByIdOwner(idUserOptional);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequestEntity.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestEntity.getDescription()));
        assertThat(result.get(0).getItems(), notNullValue());
        assertThat(result.get(0).getItems().size(), equalTo((1)));
        assertThat(result.get(0).getItems().get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getItems().get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).getItems().get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAllItemsRequestsByOwner(any());
        verify(itemRepository, times(1)).findAllItemsByRequest(any());
    }

    @Test
    void getListItemRequestByAnyUser_whenUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        initOptionalVariable();
        ItemRequestRequestListTO itemRequestRequestListTO = ItemRequestRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .build();

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getListItemRequestByAnyUser(
                itemRequestRequestListTO
        ));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findAll();
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void getListItemRequestByAnyUser_whenUserOwner() {
        initTestVariable(true, false, true);
        Page<ItemRequestEntity> itemRequestEntities = new PageImpl<ItemRequestEntity>(
                Collections.singletonList(itemRequestEntity)
        );

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAll((Pageable) any()))
                .thenReturn(itemRequestEntities);

        initOptionalVariable();
        ItemRequestRequestListTO itemRequestRequestListTO = ItemRequestRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .build();

        List<ItemRequestResponseTO> result = itemRequestService.getListItemRequestByAnyUser(
                itemRequestRequestListTO
        );

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void getListItemRequestByAnyUser_whenListItemRequestEmpty() {
        initTestVariable(true, false, true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAll((Pageable) any()))
                .thenReturn(Page.empty());

        initOptionalVariable();
        ItemRequestRequestListTO itemRequestRequestListTO = ItemRequestRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .build();

        List<ItemRequestResponseTO> result = itemRequestService.getListItemRequestByAnyUser(
                itemRequestRequestListTO
        );

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
        verify(itemRepository, never()).findAllItemsByRequest(any());
    }

    @Test
    void getListItemRequestByAnyUser_whenAnyUserWithoutItemList() {
        initTestVariable(true, false, true);
        Page<ItemRequestEntity> itemRequestEntities = new PageImpl<ItemRequestEntity>(
                Collections.singletonList(itemRequestEntity)
        );

        initTestVariable(true, false, true);
        userEntity.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAll((Pageable) any()))
                .thenReturn(itemRequestEntities);
        when(itemRepository.findAllItemsByRequest(any()))
                .thenReturn(Collections.emptyList());

        initOptionalVariable();
        idUserOptional = Optional.of(2L);
        ItemRequestRequestListTO itemRequestRequestListTO = ItemRequestRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .build();

        List<ItemRequestResponseTO> result = itemRequestService.getListItemRequestByAnyUser(
                itemRequestRequestListTO
        );

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequestEntity.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestEntity.getDescription()));
        assertThat(result.get(0).getCreated(), notNullValue());
        assertThat(result.get(0).getItems(), notNullValue());
        assertThat(result.get(0).getItems().size(), equalTo((0)));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
        verify(itemRepository, times(1)).findAllItemsByRequest(any());
    }

    @Test
    void getListItemRequestByAnyUser_whenAnyUserWitItemList() {
        initTestVariable(true, false, true);
        Page<ItemRequestEntity> itemRequestEntities = new PageImpl<ItemRequestEntity>(
                Collections.singletonList(itemRequestEntity)
        );

        initTestVariable(true, false, true);
        userEntity.setId(2L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(itemRequestRepository.findAll((Pageable) any()))
                .thenReturn(itemRequestEntities);
        when(itemRepository.findAllItemsByRequest(any()))
                .thenReturn(List.of(itemEntity));

        initOptionalVariable();
        idUserOptional = Optional.of(2L);
        ItemRequestRequestListTO itemRequestRequestListTO = ItemRequestRequestListTO.builder()
                .idUser(idUserOptional)
                .from(fromOptional)
                .size(sizeOptional)
                .build();

        List<ItemRequestResponseTO> result = itemRequestService.getListItemRequestByAnyUser(
                itemRequestRequestListTO
        );

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequestEntity.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestEntity.getDescription()));
        assertThat(result.get(0).getCreated(), notNullValue());
        assertThat(result.get(0).getItems(), notNullValue());
        assertThat(result.get(0).getItems().size(), equalTo((1)));
        assertThat(result.get(0).getItems().get(0).getId(), equalTo(itemEntity.getId()));
        assertThat(result.get(0).getItems().get(0).getName(), equalTo(itemEntity.getName()));
        assertThat(result.get(0).getItems().get(0).getDescription(), equalTo(itemEntity.getDescription()));
        assertThat(result.get(0).getItems().get(0).getAvailable(), equalTo(itemEntity.isAvailable()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
        verify(itemRepository, times(1)).findAllItemsByRequest(any());
    }

}
