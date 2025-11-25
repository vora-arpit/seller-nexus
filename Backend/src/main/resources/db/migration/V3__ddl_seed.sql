
alter table users alter column email_verified set default false;

INSERT INTO organizations (id, name, description, currency)
VALUES (1, 'test-organization-1', 'org.test.description-1', 'USD'),
       (2, 'test-organization-2', 'org.test.description-2', 'EUR');

INSERT INTO positions (id, name, description, organization_id)
VALUES (3, 'accountant', 'simple-accountant', 1),
       (4, 'administrator', 'simple-administrator', 1),
       (5, 'root', 'simple-root:org1', 1),
       (6, 'root', 'simple-root:org2', 2);


INSERT INTO users (id,email, name, password, provider, provider_id,organization_id,position_id)
VALUES(1,'avora@cpp.edu', 'Admin', '$2a$10$4ACr4T33F2KMQRPGbyI1ze1BvMZZR/eSzCUPQKxvqKmxrzqae6CZu', 'local', 'local',1,4);