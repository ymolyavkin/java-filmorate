package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.web.client.TestRestTemplate;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        User user = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        int userId = userStorage.addUser(user);
        User actualUser = userStorage.findUserById(1);

        assertThat(actualUser).isNotNull();
        assertEquals(userId, 1);
    }

    @Test
    public void shouldAddUser() {
        User user = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        int userId = userStorage.addUser(user);
        assertThat(user).usingRecursiveComparison().ignoringFields("userId").isEqualTo(user);
        assertEquals(1, userId);
    }

    /*@Test
    @DisplayName("Should Retrieve User by id = 1")
    public void shouldFindUserById() {

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);

        User expectedUser = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        userDbStorage.addUser(expectedUser);

        Mockito.when(userDbStorage.findUserById(1)).thenReturn(expectedUser);

        User actualUser = userDbStorage.findUserById(1);

        Assertions.assertThat(actualUser.getId()).isEqualTo(expectedUser.getId());
        Assertions.assertThat(actualUser.getLogin()).isEqualTo(expectedUser.getLogin());
    }*/

    @Test
    @DisplayName("/return empty films")
    void shouldReturnEmptyFilms() {
        URI targetUrl = UriComponentsBuilder.fromUriString("/films")

                .build()
                .encode()
                .toUri();
        String message = this.restTemplate.getForObject(targetUrl, String.class);
        System.out.println("message: " + message);

        assertEquals("[]", message);
    }

    @Test
    @DisplayName("/return empty users")
    void shouldReturnEmptyUsers() {
        URI targetUrl = UriComponentsBuilder.fromUriString("/users")

                .build()
                .encode()
                .toUri();
        String message = this.restTemplate.getForObject(targetUrl, String.class);
        System.out.println("message: " + message);

        assertEquals("[]", message);
    }
}
