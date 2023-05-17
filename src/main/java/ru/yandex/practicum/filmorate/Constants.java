package ru.yandex.practicum.filmorate;

import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Constants {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String DESCENDING_ORDER = "desc";
    public static final String ASCENDING_ORDER = "asc";
    public static final Set<String> SORTS = Set.of(ASCENDING_ORDER, DESCENDING_ORDER);
}
