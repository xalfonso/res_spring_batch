DROP TABLE person IF EXISTS;

CREATE TABLE person
(
    id          BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name  VARCHAR(30),
    second_name VARCHAR(50),
    age         INTEGER
)