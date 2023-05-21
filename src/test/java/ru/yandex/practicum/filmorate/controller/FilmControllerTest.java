package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

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
        Mpa ratingMpa = new Mpa(1, "G");
        Genre genreOne = new Genre(1, "Drama");
        Genre genreTwo = new Genre(2, "Komedy");

        var genres = Arrays.asList(genreOne, genreTwo);
        Film film = new Film("nisi eiusmod", "adipisicing",
                LocalDate.of(1967, 3, 25), 100, ratingMpa, genres);

        return film;
    }

    private Film createTestFilmWithId9999() {
        Mpa ratingMpa = new Mpa(1, "G");
        Genre genreOne = new Genre(1, "Drama");
        Genre genreTwo = new Genre(2, "Komedy");
        var genres = Arrays.asList(genreOne, genreTwo);
        Film film = new Film("nisi eiusmod", "adipisicing",
                LocalDate.of(1967, 3, 25), 100, ratingMpa, genres);
        film.setId(9999);
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
        Film film = createTestFilmWithId9999();

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().is(404))
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(Exception.class));
    }


    @Test
    public void givenEmptyWhenGetFilmsThenStatus200() throws Exception {
        mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk());
    }
}

