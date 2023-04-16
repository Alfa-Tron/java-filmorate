
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



    CREATE TABLE IF NOT EXISTS film
    (
        id          INT AUTO_INCREMENT PRIMARY KEY,
        film_name   VARCHAR(50),
        description VARCHAR(50),
        releaseDate DATE,
        duration    INTEGER,
        rate      INT,
        mpa INT

    );


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
