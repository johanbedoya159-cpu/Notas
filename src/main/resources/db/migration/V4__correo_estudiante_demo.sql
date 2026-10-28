-- Vincula a Ana Pérez con una cuenta real de Microsoft Entra ID para probar el rol Estudiante.
-- El correo se toma de la variable de entorno CORREO_ESTUDIANTE_DEMO; si no existe, no cambia nada.
UPDATE estudiante SET correo = '${correo_estudiante_demo}' WHERE id = '019a0000-0000-7000-8000-000000000201';
