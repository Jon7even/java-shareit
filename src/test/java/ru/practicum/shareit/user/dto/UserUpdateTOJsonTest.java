package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;
import ru.practicum.shareit.user.dto.UserUpdateTO;

import static org.assertj.core.api.Assertions.assertThat;

public class UserUpdateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<UserUpdateTO> json;

    @Test
    void testSerialize() throws Exception {
        UserUpdateTO userUpdateTO = UserUpdateTO.builder().name("TestName").email("TestEmail@ya.ru").build();
        JsonContent<UserUpdateTO> result = json.write(userUpdateTO);

        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.email").extractingJsonPathStringValue("$.email");
    }
}

