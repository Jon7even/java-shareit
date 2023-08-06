package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;
import ru.practicum.shareit.user.projections.UserBooker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserBookerJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<UserBooker> json;

    @Test
    void testSerialize() throws Exception {
        initTestVariable(false, false, false);
        UserBooker userBooker = UserBooker.builder().id(id).build();
        JsonContent<UserBooker> result = json.write(userBooker);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
    }
}

