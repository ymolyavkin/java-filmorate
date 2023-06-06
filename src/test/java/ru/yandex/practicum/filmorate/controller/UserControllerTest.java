package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate restTemplate;

    private User createTestUser() {
        User user = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        user.setId(1);
        return user;
    }

    private User createTestUserWithId9999() {
        User user = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        user.setId(9999);
        return user;
    }

    @Test
    public void updateUser_whenAdd_thenStatus200andUserReturned() throws Exception {
        User user = createTestUser();


        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Nick Name"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.login").value("dolore"));
    }

    @Test
    public void updateNotExistingUser_thenStatus404anExceptionThrown() throws Exception {
        User user = createTestUserWithId9999();

        user.setId(9999);

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404))
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(Exception.class));
    }

    @Test
    public void givenEmptyWhenGetFilmsThenStatus200() throws Exception {

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("return test user")
    void shouldReturnTestUser() {
        User user = createTestUser();
        URI targetUrl = UriComponentsBuilder.fromUriString("/users")

                .build()
                .encode()
                .toUri();
        String message = this.restTemplate.getForObject(targetUrl, String.class);
        System.out.println("message: " + message);

        assertEquals("[{\"id\":1," +
                        "\"name\":\"Nick Name\"," +
                        "\"email\":\"mail@mail.ru\"," +
                        "\"login\":\"dolore\"," +
                        "\"birthday\":\"1946-08-20\"," +
                        "\"friends\":[]}]",
                message);
    }
}