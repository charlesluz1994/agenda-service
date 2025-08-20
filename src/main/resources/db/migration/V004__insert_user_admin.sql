SET search_path TO agenda_schema, public;

INSERT INTO agenda_schema.user_client(id, name, username, password)
VALUES (nextval('agenda_schema.user_client_id_seq'), 'admin', 'admin',
        '$2a$12$VP2USlG.A6GDxESFy5Jqh.6dC4AcRZ6Qgbk7f1nQvX7wwBxKs0p4a');