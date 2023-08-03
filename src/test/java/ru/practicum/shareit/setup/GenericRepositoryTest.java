package ru.practicum.shareit.setup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenericRepositoryTest extends GenericInitEntity {
    @Autowired
    protected UserRepository userRepository;

    protected UserEntity userInDB;

    @BeforeEach
    void setUp() {
        initTestVariable(false, false, false);
        userInDB = userRepository.save(userEntity);
    }

    @AfterEach
    void clearRepository() {
        userRepository.deleteAll();
    }
}
