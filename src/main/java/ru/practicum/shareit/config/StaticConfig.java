/*
 * Copyright (c) 2023 HomeWork. Yandex Practicum. All rights reserved.
 *
 * DangerZone!!!
 *
 * DEFAULT_STATUS_AFTER_CREATED
 * Статус Booking.status после создания значение - "По умолчанию"
 * Используется в сервисном методе createBooking
 * При изменении настройки приложение не будет работать корректно
 * Изменять при перестроении модели статусов
 *
 */

package ru.practicum.shareit.config;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.entity.BookingStatus;

@UtilityClass
public class StaticConfig {
    public static final BookingStatus DEFAULT_STATUS_AFTER_CREATED = BookingStatus.WAITING;
}
