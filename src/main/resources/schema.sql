CREATE TABLE IF NOT EXISTS `PUBLIC."user"`(
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            email VARCHAR(30) NOT NULL,
                                            login VARCHAR(30) NOT NULL,
                                            name VARCHAR(30),
                                            birthday DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS `genre` (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(50) NOT NULL
);
CREATE TABLE IF NOT EXISTS `friendship` (
                                          one_friend_id BIGINT NOT NULL,
                                          other_friend_id BIGINT NOT NULL,
                                          CONSTRAINT U_each_other UNIQUE (one_friend_id, other_friend_id),
                                          CONSTRAINT FK_one_friend_id FOREIGN KEY(one_friend_id) REFERENCES `PUBLIC."user"`(id),
                                          CONSTRAINT FK_other_friend_id FOREIGN KEY(other_friend_id) REFERENCES `PUBLIC."user"`(id));
--------------------------------------------------
CREATE TABLE IF NOT EXISTS `film`
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(200) NOT NULL,
    login       VARCHAR(30)  NOT NULL,
    release     DATE         NOT NULL CHECK (release > '1895-12-28'),
    duration    INTEGER CHECK (duration > 0),
    mpa_id      INTEGER      NOT NULL,
    CONSTRAINT FK_mpa_id FOREIGN KEY (mpa_id) REFERENCES `mpa` (id)
);
-------------------------------------------------
CREATE TABLE IF NOT EXISTS `mpa` (
                                   id INTEGER AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(50) NOT NULL
);
--------------------------------------------------
CREATE TABLE IF NOT EXISTS `film_genre` (
                                          film_id BIGINT NOT NULL,
                                          genre_id INTEGER,
                                          CONSTRAINT U_film_genre UNIQUE (film_id, genre_id),
                                          CONSTRAINT FK_film_id FOREIGN KEY(film_id) REFERENCES `film`(id),
                                          CONSTRAINT FK_genre_id FOREIGN KEY(genre_id) REFERENCES `genre`(id)
);
--------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_likefilm` (
                                             user_id BIGINT NOT NULL,
                                             film_id BIGINT NOT NULL,
                                             CONSTRAINT U_user_id_film_id UNIQUE (user_id, film_id),
                                             CONSTRAINT FK_like_film_id FOREIGN KEY(film_id) REFERENCES `film`(id),
                                             CONSTRAINT FK_like_user_id FOREIGN KEY(user_id) REFERENCES `PUBLIC."user"`(id)
);