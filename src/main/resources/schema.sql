CREATE TABLE IF NOT EXISTS directors
(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);
CREATE TABLE IF NOT EXISTS userFilmorate
(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email     VARCHAR(50),
    login     VARCHAR(50),
    name VARCHAR(50),
    birthday  DATE
);
CREATE TABLE IF NOT EXISTS  friendship(
                  friend_id INTEGER REFERENCES userFilmorate (id),
                   user_id INTEGER REFERENCES userFilmorate (id),
            PRIMARY KEY (friend_id, user_id),
            status boolean
);
CREATE TABLE IF NOT EXISTS genre
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(50)
);
MERGE INTO genre
    KEY (id)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

CREATE TABLE IF NOT EXISTS mpa(
                                         id INT PRIMARY KEY,
                                         name VARCHAR(6)
);

MERGE INTO mpa
    KEY (id)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

CREATE TABLE IF NOT EXISTS film
    (
        id          INT AUTO_INCREMENT PRIMARY KEY,
        film_name   VARCHAR(50),
        description VARCHAR(50),
        releaseDate DATE,
        duration    INTEGER,
        rate      INT,
        mpa INT,
            FOREIGN KEY (mpa) REFERENCES mpa(ID)
    );


CREATE TABLE IF NOT EXISTS filmLikes
(
    film_id INTEGER REFERENCES film (id),
    user_id INTEGER REFERENCES userFilmorate (id),
    PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS FilmDirectors
(
    film_id  INTEGER REFERENCES film (id),
    directors_id INTEGER REFERENCES directors (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, directors_id)
);
CREATE TABLE IF NOT EXISTS FilmGenre
(
    film_id  INTEGER REFERENCES film (id),
    genre_id INTEGER REFERENCES genre (id),
    PRIMARY KEY (film_id, genre_id)
);
CREATE TABLE IF NOT EXISTS Friendship (
                            user_id INTEGER REFERENCES userFilmorate(id),
                            friend_id INTEGER REFERENCES userFilmorate(id),
                            status boolean,
                            PRIMARY KEY (user_id, friend_id)
);

-- REVIEWS definition
CREATE TABLE IF NOT EXISTS REVIEWS (
	REVIEW_ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	CONTENT CHARACTER VARYING NOT NULL,
	IS_POSITIVE BOOLEAN NOT NULL,
	FILM_ID INTEGER NOT NULL REFERENCES film (id) ON DELETE CASCADE,
	USER_ID INTEGER NOT NULL REFERENCES userFilmorate (id) ON DELETE CASCADE
);
COMMENT ON COLUMN REVIEWS.REVIEW_ID IS 'Идентификатор отзыва';
COMMENT ON COLUMN REVIEWS.CONTENT IS 'Текст отзыва';
COMMENT ON COLUMN REVIEWS.IS_POSITIVE IS 'Тип отзыва (Негативный\\Положительный)';
COMMENT ON COLUMN REVIEWS.FILM_ID IS 'Уникальный идентификатор фильма';
COMMENT ON COLUMN REVIEWS.USER_ID IS 'Уникальный идентификатор пользователя';

CREATE TABLE IF NOT EXISTS LIKEREVIEWS (
	REVIEW_ID BIGINT NOT NULL REFERENCES REVIEWS (REVIEW_ID) ON DELETE CASCADE,
	USER_ID INTEGER NOT NULL REFERENCES userFilmorate (id) ON DELETE CASCADE,
	IS_POSITIVE BOOLEAN NOT NULL,
	PRIMARY KEY ("REVIEW_ID", "USER_ID", "IS_POSITIVE")
);
COMMENT ON COLUMN LIKEREVIEWS.REVIEW_ID IS 'Идентификатор отзыва';
COMMENT ON COLUMN LIKEREVIEWS.USER_ID IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN LIKEREVIEWS.IS_POSITIVE IS 'Тип лайка (Лайк\\Дизлайк)';
-- END REVIEWS definition