package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private User createtestUser() {
        User user = new User("Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        return user;
    }

    @Test
    public void updateUser_whenAdd_thenStatus200andUserReturned() throws Exception {
        User user = createtestUser();

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
    public void givenId_whenGetNotExistingUser_thenStatus404anExceptionThrown() throws Exception {
        String nonExistingUser = "{\n" +
                "  \"login\": \"doloreUpdate\",\n" +
                "  \"name\": \"est adipisicing\",\n" +
                "  \"id\": 9999,\n" +
                "  \"email\": \"mail@yandex.ru\",\n" +
                "  \"birthday\": \"1976-09-20\"\n" +
                "}";

        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(nonExistingUser)))
                .andExpect(status().is(415))
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(Exception.class));
    }

    @Test
    public void givenEmptyWhenGetFilmsThenStatus200() throws Exception {

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk());
    }
}