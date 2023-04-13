package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;


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
