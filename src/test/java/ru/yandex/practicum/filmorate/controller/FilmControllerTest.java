package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Film createTestFilm() {
        Film film = new Film("nisi eiusmod", "adipisicing", LocalDate.of(1967, 3, 25), 100);
        return film;
    }

    @Test
    public void givenFilm_whenAdd_thenStatus200andFilmReturned() throws Exception {
        Film film = createTestFilm();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("nisi eiusmod"))
                .andExpect(jsonPath("$.releaseDate").value("1967-03-25"))
                .andExpect(jsonPath("$.duration").value("100"));
    }

    @Test
    public void updateFilmWhenGetNotExistingFilm_thenStatus404anExceptionThrown() throws Exception {
        String nonExistingFilm = "{\n" +
                "  \"id\": 9999,\n" +
                "  \"name\": \"Film Updated\",\n" +
                "  \"releaseDate\": \"1989-04-17\",\n" +
                "  \"description\": \"New film update decription\",\n" +
                "  \"duration\": 190,\n" +
                "  \"rate\": 4\n" +
                "}";

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(nonExistingFilm)))
                .andExpect(status().is(415))
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(Exception.class));
    }

    @Test
    public void givenEmptyWhenGetFilmsThenStatus200() throws Exception {

        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk());
    }
}
