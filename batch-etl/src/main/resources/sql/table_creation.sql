CREATE DATABASE IF NOT EXISTS movie;
USE movie;

DROP TABLE IF EXISTS occupation;
CREATE TABLE occupation (
    id INT,
    occupation VARCHAR(64)
);

DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id INT,
    gender CHAR(1),
    zip_code VARCHAR(10),
    age INT,
    occupation_id INT,
    last_modified DATETIME
);

DROP TABLE IF EXISTS movie;
CREATE TABLE movie (
    id INT,
    title VARCHAR(128),
    release_date DATE,
    video_release_date DATE,
    imdb_url VARCHAR(1024)
);

DROP TABLE IF EXISTS movie_genre;
CREATE TABLE movie_genre (
    movie_id INT,
    genre_id INT
);

DROP TABLE IF EXISTS genre;
CREATE TABLE genre (
    id INT,
    name VARCHAR(15)
);

DROP TABLE IF EXISTS user_rating;
CREATE TABLE user_rating (
    id INT,
    user_id INT,
    movie_id INT,
    rating INT,
    dt_time DATETIME
);

