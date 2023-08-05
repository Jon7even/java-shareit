package ru.practicum.shareit.setup;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class GenericServiceTest extends GenericInitEntity {
    @Mock
    protected UserRepository userRepository;

    @Mock
    protected ItemRepository itemRepository;

    @Mock
    protected BookingRepository bookingRepository;

    @Mock
    protected ItemRequestRepository itemRequestRepository;
}
