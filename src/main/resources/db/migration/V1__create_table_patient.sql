create TABLE patient(
 id serial PRIMARY KEY,
 name varchar(50),
 lastname varchar(100),
 cpf varchar(15),
 email varchar(100),
 created_date timestamp
);