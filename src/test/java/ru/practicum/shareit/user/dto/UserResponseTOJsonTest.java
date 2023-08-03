package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.setup.GenericDTOTest;
import ru.practicum.shareit.user.dto.UserResponseTO;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResponseTOJsonTest extends GenericDTOTest {
    @Autowired
    JacksonTester<UserResponseTO> json;

    @Test
    void testSerialize() throws Exception {
        UserResponseTO userResponseTO = UserResponseTO.builder()
                .id(id)
                .name("TestName")
                .email("TestEmail@ya.ru")
                .build();
        JsonContent<UserResponseTO> result = json.write(userResponseTO);

        assertThat(result).hasJsonPath("$.id").extractingJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPath("$.name").extractingJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.email").extractingJsonPathStringValue("$.email");
    }
}
