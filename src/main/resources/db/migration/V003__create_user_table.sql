CREATE TABLE IF NOT EXISTS agenda_schema.user_client
(
    id       serial PRIMARY KEY,
    name     varchar(255),
    username varchar(255),
    password varchar(255)
);
