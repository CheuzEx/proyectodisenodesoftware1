-- ============================================================
-- Script de inserción de usuarios iniciales para pruebas
-- Ejecutar manualmente en la base de datos clinica_db
-- schema: clinica
-- ============================================================

-- Contraseñas encriptadas con BCrypt (factor 10):
--   admin123   -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
--   doctor123  -> $2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIXCyaVFAKsN5.6

-- IMPORTANTE: Asegurarse de que exista al menos un doctor antes de insertar
-- el usuario con rol ROLE_DOCTOR (ajustar el id_doctor según corresponda)

INSERT INTO clinica.usuarios (username, password, rol, id_doctor)
VALUES
    ('admin',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN',  NULL),
    ('dr.perez',   '$2a$10$TKh8H1.PfQx37YgCzwiKb.KjNyWgaHb9cbcoQgdIXCyaVFAKsN5.6', 'ROLE_DOCTOR', 1);

-- Verificar inserciones:
-- SELECT id, username, rol, id_doctor FROM clinica.usuarios;
