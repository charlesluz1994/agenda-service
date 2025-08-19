CREATE TABLE agenda
(
    id               serial PRIMARY KEY,
    description      varchar(255) NOT NULL,
    appointment_time timestamp,
    created_date     timestamp,
    patient_id       integer      NOT NULL,
    CONSTRAINT fk_agenda_patient FOREIGN KEY (patient_id) REFERENCES patient (id)
);
