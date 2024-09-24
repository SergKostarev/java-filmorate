DROP TABLE IF EXISTS film_genre; -- remove!
DROP TABLE IF EXISTS friendship; -- remove!
DROP TABLE IF EXISTS likes; -- remove!
DROP TABLE IF EXISTS genre; -- remove!
DROP TABLE IF EXISTS films; -- remove!
DROP TABLE IF EXISTS users; -- remove!
DROP TABLE IF EXISTS rating; -- remove!





CREATE TABLE IF NOT EXISTS genre (
    id BIGINT PRIMARY KEY,
    description TEXT NOT NULL CHECK(LENGTH(description) > 0)
);

CREATE TABLE IF NOT EXISTS rating (
    id BIGINT PRIMARY KEY,
    description TEXT NOT NULL CHECK(LENGTH(description) > 0)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email TEXT NOT NULL CHECK(LENGTH(email) > 0),
    login TEXT NOT NULL CHECK(LENGTH(login) > 0),
    name TEXT,
    birthday DATE CHECK(birthday <= CAST(LOCALTIMESTAMP AS DATE))
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL CHECK(LENGTH(name) > 0),
    description VARCHAR(200) CHECK(LENGTH(description) >= 200),
    release_date DATE,
    duration INTEGER CHECK(duration >= 0),
    rating_id BIGINT DEFAULT NULL REFERENCES rating(id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL REFERENCES films(id),
    genre_id BIGINT NOT NULL REFERENCES genre(id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id BIGINT NOT NULL REFERENCES users(id),
    film_id BIGINT NOT NULL REFERENCES films(id),
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id BIGINT NOT NULL REFERENCES users(id),
    friend_id BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (user_id, friend_id)
);