/*
 * Copyright (c) 2023 HomeWork. Yandex Practicum. All rights reserved.
 *
 * DangerZone!!!
 *
 * DEFAULT_STATUS_AFTER_CREATED
 * Статус BookingStatus после создания значение - "По умолчанию"
 * Используется в сервисном методе createBooking
 * При изменении настройки приложение не будет работать корректно
 * Изменять при перестроении модели статусов
 *
 * DEFAULT_STATE_IN_CONTROLLER
 * Статус BookingState используется в контроллере BookingController
 * Если пользователь не присылает нужный параметр state в методах getListBooking
 * Используется вариант "По умолчанию" - отобразить все бронирования
 * При высокой нагрузки на сервер и тестах параметр можно менять на необходимый
 * Статус пишется в строке String
 *
 */

package ru.practicum.shareit.config;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.BookingStatus;

@UtilityClass
public class StaticConfig {
    public static final BookingStatus DEFAULT_STATUS_AFTER_CREATED = BookingStatus.WAITING;

    public static final String DEFAULT_STATE_IN_CONTROLLER = "ALL";
}
