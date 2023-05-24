package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa findMpaById(int mpaId) {
        return mpaStorage.findMpaById(mpaId);
    }

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }
}
