ALTER TABLE estudiante ADD COLUMN correo VARCHAR(254) NOT NULL DEFAULT '';

UPDATE estudiante SET correo = 'ana.perez@example.com' WHERE id = '019a0000-0000-7000-8000-000000000201';
UPDATE estudiante SET correo = 'bruno.diaz@example.com' WHERE id = '019a0000-0000-7000-8000-000000000202';
UPDATE estudiante SET correo = 'camila.torres@example.com' WHERE id = '019a0000-0000-7000-8000-000000000203';
