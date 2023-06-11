CREATE SCHEMA people;

CREATE TABLE IF NOT EXISTS people.person (
    id BIGINT PRIMARY KEY NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    birthdate DATE
);

CREATE TABLE IF NOT EXISTS people.address (
    id BIGINT PRIMARY KEY NOT NULL,
    first_line VARCHAR(50) NOT NULL,
    second_line VARCHAR(50),
    zip VARCHAR(10) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(20),
    country VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS people.person_address (
    person_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    type VARCHAR(15) NOT NULL,
    FOREIGN KEY (person_id) REFERENCES people.person(id),
    FOREIGN KEY (address_id) REFERENCES people.address(id)
);

INSERT INTO people.person(id, first_name, last_name, birthdate)
VALUES
    (1, 'John', 'Doe', '1970-01-01'),
    (2, 'Jane', 'Doe', '1970-01-01'),
    (3, 'James', 'Bond', NULL);

INSERT INTO people.address(id, first_line, second_line, zip, city, state, country)
VALUES
    (1, '400 Crotch Crescent', 'Marston', 'OX3 0JJ', 'Oxford', NULL, 'United Kingdom'),
    (2, '395 Fabulous Texan Way', NULL, '86336', 'Sedona', 'Arizona', 'United States of America'),
    (3, '85 Albert Embankment', 'Vauxhall', 'SE1 7TP', 'Lambeth', NULL, 'United Kingdom');

INSERT INTO people.person_address(person_id, address_id, type)
VALUES
    (1, 1, 'Private'),
    (2, 2, 'Private'),
    (3, 3, 'Business'),
    (3, 1, 'Private');
