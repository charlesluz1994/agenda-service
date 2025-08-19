create TABLE patient
(
    id           serial PRIMARY KEY,
    name         varchar(50)  NOT NULL,
    lastname     varchar(100) NOT NULL,
    cpf          varchar(15)  NOT NULL,
    email        varchar(100) NOT NULL,
    created_date timestamp
);