package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;

import static org.assertj.core.api.Assertions.assertThat;

public class UserCreateTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<UserCreateTO> json;

    @Test
    void testSerialize() throws Exception {
        UserCreateTO userCreateTO = UserCreateTO.builder().name("TestName").email("TestEmail@ya.ru").build();
        JsonContent<UserCreateTO> result = json.write(userCreateTO);

        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.email").extractingJsonPathStringValue("$.email");
    }
}
