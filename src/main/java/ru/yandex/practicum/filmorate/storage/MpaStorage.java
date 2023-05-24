package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    public Mpa findMpaById(int mpaId);
    public List<Mpa> findAllMpa();
}
