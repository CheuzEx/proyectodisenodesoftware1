/* 
   BASE DE DATOS: clinica
   DESCRIPCIÓN:   Sistema de gestión de citas, pacientes, doctores,
                  historiales médicos y recetas.
   MOTOR:         PostgreSQL (>= 12)
   AUTOR:         (Generado a partir de script de migración)
   NOTA:          Este script está documentado para facilitar el
                  mantenimiento y la comprensión del modelo de datos.
  */

-- 1. CONFIGURACIÓN INICIAL DE LA SESIÓN
--    Se deshabilitan timeouts para evitar interrupciones durante la
--    migración y se fija la codificación a UTF-8.
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);  -- Asegura que no se usen schemas por defecto
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- 2. ESQUEMA DE LA APLICACIÓN
--    Contiene todas las tablas, funciones y procedimientos del negocio.
CREATE SCHEMA clinica;
ALTER SCHEMA clinica OWNER TO postgres;


-- 3. PROCEDIMIENTOS ALMACENADOS Y FUNCIONES

-- 3.1 PROCEDIMIENTO: agendar_cita
--    Propósito: Crear una nueva cita médica tras validar:
--      - Existencia del paciente y del doctor.
--      - Fecha futura (mayor a la actual).
--      - Disponibilidad del doctor en un margen de ±30 minutos.
--    Parámetros:
--      p_id_paciente  : ID del paciente.
--      p_id_doctor    : ID del doctor.
--      p_fecha_hora   : Fecha y hora de la cita (sin zona horaria).
--      p_motivo       : Motivo de la consulta.
--    ADVERTENCIA: No valida solapamiento del paciente con otra cita
--                 en el mismo horario (mejora pendiente).

CREATE PROCEDURE clinica.agendar_cita(
    IN p_id_paciente INTEGER,
    IN p_id_doctor INTEGER,
    IN p_fecha_hora TIMESTAMP WITHOUT TIME ZONE,
    IN p_motivo CHARACTER VARYING
)
LANGUAGE plpgsql
AS $$
DECLARE
  v_count INT;
BEGIN
  -- 1. Validar que el paciente exista en la BD
  SELECT COUNT(*) INTO v_count FROM clinica.paciente WHERE id_paciente = p_id_paciente;
  IF v_count = 0 THEN
    RAISE EXCEPTION 'Paciente no existe';
  END IF;

  -- 2. Validar que el doctor exista en la BD
  SELECT COUNT(*) INTO v_count FROM clinica.doctor WHERE id_doctor = p_id_doctor;
  IF v_count = 0 THEN
    RAISE EXCEPTION 'Doctor no existe';
  END IF;

  -- 3. Validar que la cita sea en fecha futura (no se permite agendar en pasado)
  IF p_fecha_hora <= now() THEN
    RAISE EXCEPTION 'La cita debe ser en fecha futura';
  END IF;

  -- 4. Validar disponibilidad del doctor en el bloque de ±30 minutos
  SELECT COUNT(*) INTO v_count
  FROM clinica.cita
  WHERE id_doctor = p_id_doctor
    AND fecha_hora BETWEEN p_fecha_hora - INTERVAL '30 minutes' AND p_fecha_hora + INTERVAL '30 minutes';

  IF v_count > 0 THEN
    RAISE EXCEPTION 'Doctor no disponible en ese horario';
  END IF;

  -- 5. Insertar la nueva cita con estado 'PROGRAMADA' (valor por defecto)
  INSERT INTO clinica.cita (fecha_hora, id_paciente, id_doctor, motivo)
  VALUES (p_fecha_hora, p_id_paciente, p_id_doctor, p_motivo);
END;
$$;

ALTER PROCEDURE clinica.agendar_cita(IN INTEGER, IN INTEGER, IN TIMESTAMP, IN VARCHAR) OWNER TO postgres;

-- 3.2 FUNCIÓN: calcular_edad
--    Propósito: Devuelve la edad en años a partir de la fecha de
--               nacimiento, usando la fecha actual del sistema.
--    Uso típico: SELECT clinica.calcular_edad(fecha_nac) FROM paciente;
CREATE FUNCTION clinica.calcular_edad(fecha_nac DATE) RETURNS INTEGER
LANGUAGE sql
AS $$
  SELECT EXTRACT(YEAR FROM AGE(CURRENT_DATE, fecha_nac))::INT;
$$;

ALTER FUNCTION clinica.calcular_edad(DATE) OWNER TO postgres;


-- 3.3 PROCEDIMIENTO: importar_no_normalizados
--    Propósito: Migrar datos desde una tabla externa (parametrizada)
--               hacia la tabla PACIENTE.
CREATE PROCEDURE clinica.importar_no_normalizados(IN p_src_table TEXT)
LANGUAGE plpgsql
AS $$
DECLARE
  rec RECORD;
BEGIN
  -- Bucle por cada fila de la tabla origen
  FOR rec IN EXECUTE format('SELECT * FROM %I', p_src_table)
  LOOP
    -- Inserta directamente, asumiendo que las columnas existen con esos nombres exactos.
    -- COALESCE asigna 1900-01-01 si el campo viene NULL.
    INSERT INTO clinica.paciente (nombre, apellido, fecha_nacimiento, telefono)
    VALUES (rec.nombre, rec.apellido, COALESCE(rec.fecha_nacimiento, '1900-01-01'), rec.telefono);
  END LOOP;
END;
$$;

ALTER PROCEDURE clinica.importar_no_normalizados(IN TEXT) OWNER TO postgres;
-- 4. CONFIGURACIÓN DE TABLAS Y SECUENCIAS
--    Se definen las tablas y sus respectivas secuencias para el
--    autoincremento de las claves primarias.
-- 4.1 TABLA: cita
--    Almacena las citas médicas programadas.
--    Estado por defecto: 'PROGRAMADA'.

SET default_tablespace = '';
SET default_table_access_method = heap;

CREATE TABLE clinica.cita (
    id_cita BIGINT NOT NULL,                           -- PK autoincrementable
    fecha_hora TIMESTAMP WITHOUT TIME ZONE NOT NULL,   -- Fecha/hora de la cita
    id_paciente BIGINT NOT NULL,                       -- FK a paciente
    id_doctor BIGINT NOT NULL,                         -- FK a doctor
    id_especialidad BIGINT,                            -- FK a especialidad (opcional, se puede derivar del doctor)
    motivo CHARACTER VARYING(255),                     -- Descripción del motivo
    estado CHARACTER VARYING(20) NOT NULL DEFAULT 'PROGRAMADA'  -- Estado actual de la cita
);

ALTER TABLE clinica.cita OWNER TO postgres;

CREATE SEQUENCE clinica.cita_id_cita_seq AS INTEGER START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE clinica.cita_id_cita_seq OWNER TO postgres;
ALTER SEQUENCE clinica.cita_id_cita_seq OWNED BY clinica.cita.id_cita;


-- 4.2 TABLA: doc_especialidad
--    Tabla puente para la relación muchos a muchos entre DOCTOR y ESPECIALIDAD.

CREATE TABLE clinica.doc_especialidad (
    id_doctor BIGINT NOT NULL,
    id_especialidad BIGINT NOT NULL
);
ALTER TABLE clinica.doc_especialidad OWNER TO postgres;


-- 4.3 TABLA: doctor
--    Datos personales y de contacto de los médicos.

CREATE TABLE clinica.doctor (
    id_doctor BIGINT NOT NULL,
    nombre CHARACTER VARYING(100) NOT NULL,
    apellido CHARACTER VARYING(100) NOT NULL,
    direccion CHARACTER VARYING(150),
    telefono CHARACTER VARYING(50)
);
ALTER TABLE clinica.doctor OWNER TO postgres;

CREATE SEQUENCE clinica.doctor_id_doctor_seq AS INTEGER START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE clinica.doctor_id_doctor_seq OWNER TO postgres;
ALTER SEQUENCE clinica.doctor_id_doctor_seq OWNED BY clinica.doctor.id_doctor;


-- 4.4 TABLA: especialidad
--    Catálogo de especialidades médicas.

CREATE TABLE clinica.especialidad (
    id_especialidad BIGINT NOT NULL,
    nom_especialidad CHARACTER VARYING(100) NOT NULL
);
ALTER TABLE clinica.especialidad OWNER TO postgres;

CREATE SEQUENCE clinica.especialidad_id_especialidad_seq AS INTEGER START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE clinica.especialidad_id_especialidad_seq OWNER TO postgres;
ALTER SEQUENCE clinica.especialidad_id_especialidad_seq OWNED BY clinica.especialidad.id_especialidad;


-- 4.5 TABLA: historial_medico
--    Registro de las consultas médicas realizadas a un paciente.
--    Puede estar asociada a una cita (id_cita) o ser una consulta externa.

CREATE TABLE clinica.historial_medico (
    id_historial BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    id_cita BIGINT,                                  -- Opcional: si viene de una cita formal
    fecha_consulta DATE,
    diagnostico TEXT,
    tratamiento TEXT
);
ALTER TABLE clinica.historial_medico OWNER TO postgres;

CREATE SEQUENCE clinica.historial_medico_id_historial_seq AS INTEGER START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE clinica.historial_medico_id_historial_seq OWNER TO postgres;
ALTER SEQUENCE clinica.historial_medico_id_historial_seq OWNED BY clinica.historial_medico.id_historial;

-- 4.6 TABLA: usuario
--    Gestión de usuarios del sistema para autenticación.

CREATE TABLE IF NOT EXISTS clinica.usuario (
    id_usuario SERIAL PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,   -- Hash BCrypt
    rol        VARCHAR(30)  NOT NULL
);

-- Usuarios de ejemplo (hashes generados con BCrypt)
INSERT INTO clinica.usuario (username, password, rol) VALUES
  ('admin',   '$2a$10$7EqJtq98hPqEX7fNZaFWoOe3z1OLb4tDrGVjjfYPhlfU7bCnTr6Fy', 'ROLE_ADMIN'),
  ('doctor1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTtoskm9Ci', 'ROLE_DOCTOR');

-- 4.7 TABLA: paciente
--    Datos personales de los pacientes.
--    Incluye restricción CHECK para sexo: M, F, O.

CREATE TABLE clinica.paciente (
    id_paciente BIGINT NOT NULL,
    nombre CHARACTER VARYING(100) NOT NULL,
    apellido CHARACTER VARYING(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    sexo CHARACTER VARYING(1),
    direccion CHARACTER VARYING(255),
    telefono CHARACTER VARYING(255) NOT NULL,
    correo CHARACTER VARYING(255),
    CONSTRAINT paciente_sexo_check CHECK (((sexo)::bpchar = ANY (ARRAY['M'::bpchar, 'F'::bpchar, 'O'::bpchar])))
);
ALTER TABLE clinica.paciente OWNER TO postgres;

CREATE SEQUENCE clinica.paciente_id_paciente_seq AS INTEGER START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE clinica.paciente_id_paciente_seq OWNER TO postgres;
ALTER SEQUENCE clinica.paciente_id_paciente_seq OWNED BY clinica.paciente.id_paciente;

-- 4.8 TABLA: receta
--    Medicamentos recetados, asociados a un historial médico.

CREATE TABLE clinica.receta (
    id_receta BIGINT NOT NULL,
    medicamento CHARACTER VARYING(200) NOT NULL,
    dosis CHARACTER VARYING(100) NOT NULL,
    frecuencia CHARACTER VARYING(100),
    duracion INTEGER,                                 -- Duración en días (ej. 7)
    id_historial BIGINT NOT NULL
);
ALTER TABLE clinica.receta OWNER TO postgres;

CREATE SEQUENCE clinica.receta_id_receta_seq AS INTEGER START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE clinica.receta_id_receta_seq OWNER TO postgres;
ALTER SEQUENCE clinica.receta_id_receta_seq OWNED BY clinica.receta.id_receta;

-- 5. ASIGNACIÓN DE DEFAULTS A LAS SECUENCIAS
--    Conecta cada columna ID con su secuencia correspondiente.

ALTER TABLE ONLY clinica.cita ALTER COLUMN id_cita SET DEFAULT nextval('clinica.cita_id_cita_seq'::regclass);
ALTER TABLE ONLY clinica.doctor ALTER COLUMN id_doctor SET DEFAULT nextval('clinica.doctor_id_doctor_seq'::regclass);
ALTER TABLE ONLY clinica.especialidad ALTER COLUMN id_especialidad SET DEFAULT nextval('clinica.especialidad_id_especialidad_seq'::regclass);
ALTER TABLE ONLY clinica.historial_medico ALTER COLUMN id_historial SET DEFAULT nextval('clinica.historial_medico_id_historial_seq'::regclass);
ALTER TABLE ONLY clinica.paciente ALTER COLUMN id_paciente SET DEFAULT nextval('clinica.paciente_id_paciente_seq'::regclass);
ALTER TABLE ONLY clinica.receta ALTER COLUMN id_receta SET DEFAULT nextval('clinica.receta_id_receta_seq'::regclass);


-- 6. CARGA DE DATOS INICIALES (MAESTROS Y CATÁLOGOS)

-- Relación Doctor-Especialidad (Doctor Daniel Barboza -> Medicina General)
INSERT INTO clinica.doc_especialidad (id_doctor, id_especialidad) VALUES (1, 1);

-- Doctores
INSERT INTO clinica.doctor (id_doctor, nombre, apellido, direccion, telefono) VALUES
(1, 'Daniel', 'Barboza', 'Avenida Central, Cartago', '8888-0000');

-- Especialidades
INSERT INTO clinica.especialidad (id_especialidad, nom_especialidad) VALUES
(1, 'Medicina General'),
(2, 'Cardiología'),
(3, 'Pediatría');

-- Pacientes (ejemplo)
INSERT INTO clinica.paciente (id_paciente, nombre, apellido, fecha_nacimiento, sexo, direccion, telefono, correo) VALUES
(7, 'Juan', 'Pérez', '1990-05-10', 'M', 'Cartago', '8888-8888', 'juanperez@example.com');



-- 7. REINICIO DE SECUENCIAS (setval)
--    Ajusta el valor actual de las secuencias para que coincidan con
--    los IDs insertados manualmente.
--    - 'false' en is_called indica que el próximo nextval devolverá
--      exactamente el valor asignado.
--    - 'true' indica que ya se usó ese valor, el próximo será +1.

SELECT pg_catalog.setval('clinica.cita_id_cita_seq', 1, false);           -- Tabla vacía, empieza en 1
SELECT pg_catalog.setval('clinica.doctor_id_doctor_seq', 1, true);        -- Ya existe ID=1, próximo será 2
SELECT pg_catalog.setval('clinica.especialidad_id_especialidad_seq', 3, true); -- IDs 1,2,3 ocupados, próximo 4
SELECT pg_catalog.setval('clinica.historial_medico_id_historial_seq', 1, false);
SELECT pg_catalog.setval('clinica.paciente_id_paciente_seq', 7, true);    -- ID=7 ocupado, próximo 8
SELECT pg_catalog.setval('clinica.receta_id_receta_seq', 1, false);


-- 8. RESTRICCIONES DE INTEGRIDAD (PK y FK)
--    Se añaden después de los datos para mayor rendimiento durante la
--    importación, aunque aquí se hace al final por claridad.


-- 8.1 Claves Primarias
ALTER TABLE ONLY clinica.cita ADD CONSTRAINT cita_pkey PRIMARY KEY (id_cita);
ALTER TABLE ONLY clinica.doc_especialidad ADD CONSTRAINT doc_especialidad_pkey PRIMARY KEY (id_doctor, id_especialidad);
ALTER TABLE ONLY clinica.doctor ADD CONSTRAINT doctor_pkey PRIMARY KEY (id_doctor);
ALTER TABLE ONLY clinica.especialidad ADD CONSTRAINT especialidad_pkey PRIMARY KEY (id_especialidad);
ALTER TABLE ONLY clinica.historial_medico ADD CONSTRAINT historial_medico_pkey PRIMARY KEY (id_historial);
ALTER TABLE ONLY clinica.paciente ADD CONSTRAINT paciente_pkey PRIMARY KEY (id_paciente);
ALTER TABLE ONLY clinica.receta ADD CONSTRAINT receta_pkey PRIMARY KEY (id_receta);

-- 8.2 Claves Foráneas (Relaciones)
ALTER TABLE ONLY clinica.cita
    ADD CONSTRAINT cita_id_doctor_fkey FOREIGN KEY (id_doctor) REFERENCES clinica.doctor(id_doctor);
ALTER TABLE ONLY clinica.cita
    ADD CONSTRAINT cita_id_paciente_fkey FOREIGN KEY (id_paciente) REFERENCES clinica.paciente(id_paciente);
ALTER TABLE ONLY clinica.cita
    ADD CONSTRAINT cita_id_especialidad_fkey FOREIGN KEY (id_especialidad) REFERENCES clinica.especialidad(id_especialidad);

ALTER TABLE ONLY clinica.doc_especialidad
    ADD CONSTRAINT doc_especialidad_id_doctor_fkey FOREIGN KEY (id_doctor) REFERENCES clinica.doctor(id_doctor);
ALTER TABLE ONLY clinica.doc_especialidad
    ADD CONSTRAINT doc_especialidad_id_especialidad_fkey FOREIGN KEY (id_especialidad) REFERENCES clinica.especialidad(id_especialidad);

ALTER TABLE ONLY clinica.historial_medico
    ADD CONSTRAINT historial_medico_id_cita_fkey FOREIGN KEY (id_cita) REFERENCES clinica.cita(id_cita);
ALTER TABLE ONLY clinica.historial_medico
    ADD CONSTRAINT historial_medico_id_paciente_fkey FOREIGN KEY (id_paciente) REFERENCES clinica.paciente(id_paciente);

ALTER TABLE ONLY clinica.receta
    ADD CONSTRAINT receta_id_historial_fkey FOREIGN KEY (id_historial) REFERENCES clinica.historial_medico(id_historial);
