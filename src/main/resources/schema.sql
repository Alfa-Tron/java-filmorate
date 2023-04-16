
CREATE TABLE IF NOT EXISTS userFilmorate
(
    id INT AUTO_INCREMENT PRIMARY KEY,
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
--INSERT INTO  genre (id, name) VALUES
           --     (1, 'Комедия'),
            --    (2, 'Драма'),
             --   (3, 'Мультфильм'),
            --  (4, 'Триллер'),
             --   (5, 'Документальный'),
            --    (6, 'Боевик');
    CREATE TABLE IF NOT EXISTS film
    (
        id          INTEGER PRIMARY KEY,
        film_name   VARCHAR(50),
        description VARCHAR(50),
        releaseDate DATE,
        duration    INTEGER,
        rate      INT,
        mpa INT,
       FOREIGN KEY (mpa) REFERENCES mpa_rating(id)

    );
CREATE TABLE  IF NOT EXISTS mpa_rating
(
    id INT,
    PRIMARY KEY (id),
    mpa VARCHAR(6)

);
--INSERT INTO  mpa_rating (id, mpa) VALUES
      --       (1, 'G'),
      --       (2, 'PG'),
      --   (3, 'PG-13'),
     --     (4, 'R'),
    --      (5, 'NC-17');

CREATE TABLE IF NOT EXISTS filmLikes
(
    film_id INTEGER REFERENCES film (id),
    user_id INTEGER REFERENCES userFilmorate (id),
    PRIMARY KEY (film_id, user_id)
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
