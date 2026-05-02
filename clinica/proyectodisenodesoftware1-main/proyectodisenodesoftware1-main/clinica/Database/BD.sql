SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA clinica;

ALTER SCHEMA clinica OWNER TO postgres;

CREATE PROCEDURE clinica.agendar_cita(IN p_id_paciente integer, IN p_id_doctor integer, IN p_fecha_hora timestamp without time zone, IN p_motivo character varying)
    LANGUAGE plpgsql
    AS $$
DECLARE
  v_count INT;
BEGIN
  -- verificar paciente
  SELECT COUNT(*) INTO v_count FROM clinica.paciente WHERE id_paciente = p_id_paciente;
  IF v_count = 0 THEN
    RAISE EXCEPTION 'Paciente no existe';
  END IF;

  -- verificar doctor
  SELECT COUNT(*) INTO v_count FROM clinica.doctor WHERE id_doctor = p_id_doctor;
  IF v_count = 0 THEN
    RAISE EXCEPTION 'Doctor no existe';
  END IF;

  IF p_fecha_hora <= now() THEN
    RAISE EXCEPTION 'La cita debe ser en fecha futura';
  END IF;

  -- verificar disponibilidad 
  SELECT COUNT(*) INTO v_count
  FROM clinica.cita
  WHERE id_doctor = p_id_doctor
    AND fecha_hora BETWEEN p_fecha_hora - INTERVAL '30 minutes' AND p_fecha_hora + INTERVAL '30 minutes';

  IF v_count > 0 THEN
    RAISE EXCEPTION 'Doctor no disponible en ese horario';
  END IF;

  INSERT INTO clinica.cita (fecha_hora, id_paciente, id_doctor, motivo)
  VALUES (p_fecha_hora, p_id_paciente, p_id_doctor, p_motivo);
END;
$$;

ALTER PROCEDURE clinica.agendar_cita(IN p_id_paciente integer, IN p_id_doctor integer, IN p_fecha_hora timestamp without time zone, IN p_motivo character varying) OWNER TO postgres;

CREATE FUNCTION clinica.calcular_edad(fecha_nac date) RETURNS integer
    LANGUAGE sql
    AS $$
  SELECT EXTRACT(YEAR FROM AGE(CURRENT_DATE, fecha_nac))::INT;
$$;

ALTER FUNCTION clinica.calcular_edad(fecha_nac date) OWNER TO postgres;

CREATE PROCEDURE clinica.importar_no_normalizados(IN p_src_table text)
    LANGUAGE plpgsql
    AS $$
DECLARE
  rec RECORD;
BEGIN
  FOR rec IN EXECUTE format('SELECT * FROM %I', p_src_table)
  LOOP
  
    INSERT INTO clinica.paciente (nombre, apellido, fecha_nacimiento, telefono)
    VALUES (rec.nombre, rec.apellido, COALESCE(rec.fecha_nacimiento, '1900-01-01'), rec.telefono);
  END LOOP;
END;
$$;

ALTER PROCEDURE clinica.importar_no_normalizados(IN p_src_table text) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE TABLE clinica.cita (
    id_cita bigint NOT NULL,
    fecha_hora timestamp without time zone NOT NULL,
    id_paciente bigint NOT NULL,
    id_doctor bigint NOT NULL,
    id_especialidad bigint,
    motivo character varying(255),
    estado character varying(20) NOT NULL DEFAULT 'PROGRAMADA'
);

ALTER TABLE clinica.cita OWNER TO postgres;

CREATE SEQUENCE clinica.cita_id_cita_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE clinica.cita_id_cita_seq OWNER TO postgres;

ALTER SEQUENCE clinica.cita_id_cita_seq OWNED BY clinica.cita.id_cita;

CREATE TABLE clinica.doc_especialidad (
    id_doctor bigint NOT NULL,
    id_especialidad bigint NOT NULL
);

ALTER TABLE clinica.doc_especialidad OWNER TO postgres;

-- doc_especialidad uses composite PK, no sequence needed

CREATE TABLE clinica.doctor (
    id_doctor bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    direccion character varying(150),
    telefono character varying(50)
);

ALTER TABLE clinica.doctor OWNER TO postgres;

CREATE SEQUENCE clinica.doctor_id_doctor_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE clinica.doctor_id_doctor_seq OWNER TO postgres;

ALTER SEQUENCE clinica.doctor_id_doctor_seq OWNED BY clinica.doctor.id_doctor;

CREATE TABLE clinica.especialidad (
    id_especialidad bigint NOT NULL,
    nom_especialidad character varying(100) NOT NULL
);

ALTER TABLE clinica.especialidad OWNER TO postgres;

CREATE SEQUENCE clinica.especialidad_id_especialidad_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE clinica.especialidad_id_especialidad_seq OWNER TO postgres;

ALTER SEQUENCE clinica.especialidad_id_especialidad_seq OWNED BY clinica.especialidad.id_especialidad;

CREATE TABLE clinica.historial_medico (
    id_historial bigint NOT NULL,
    id_paciente bigint NOT NULL,
    id_cita bigint,
    fecha_consulta date,
    diagnostico text,
    tratamiento text
);

ALTER TABLE clinica.historial_medico OWNER TO postgres;

CREATE SEQUENCE clinica.historial_medico_id_historial_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE clinica.historial_medico_id_historial_seq OWNER TO postgres;

ALTER SEQUENCE clinica.historial_medico_id_historial_seq OWNED BY clinica.historial_medico.id_historial;

CREATE TABLE clinica.paciente (
    id_paciente bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    apellido character varying(100) NOT NULL,
    fecha_nacimiento date NOT NULL,
    sexo character varying(1),
    direccion character varying(255),
    telefono character varying(255) NOT NULL,
    correo character varying(255),
    CONSTRAINT paciente_sexo_check CHECK (((sexo)::bpchar = ANY (ARRAY['M'::bpchar, 'F'::bpchar, 'O'::bpchar])))
);

ALTER TABLE clinica.paciente OWNER TO postgres;

CREATE SEQUENCE clinica.paciente_id_paciente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE clinica.paciente_id_paciente_seq OWNER TO postgres;

ALTER SEQUENCE clinica.paciente_id_paciente_seq OWNED BY clinica.paciente.id_paciente;

CREATE TABLE clinica.receta (
    id_receta bigint NOT NULL,
    medicamento character varying(200) NOT NULL,
    dosis character varying(100) NOT NULL,
    frecuencia character varying(100),
    duracion integer,
    id_historial bigint NOT NULL
);

ALTER TABLE clinica.receta OWNER TO postgres;

CREATE SEQUENCE clinica.receta_id_receta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE clinica.receta_id_receta_seq OWNER TO postgres;

ALTER SEQUENCE clinica.receta_id_receta_seq OWNED BY clinica.receta.id_receta;

ALTER TABLE ONLY clinica.cita ALTER COLUMN id_cita SET DEFAULT nextval('clinica.cita_id_cita_seq'::regclass);

ALTER TABLE ONLY clinica.doctor ALTER COLUMN id_doctor SET DEFAULT nextval('clinica.doctor_id_doctor_seq'::regclass);

ALTER TABLE ONLY clinica.especialidad ALTER COLUMN id_especialidad SET DEFAULT nextval('clinica.especialidad_id_especialidad_seq'::regclass);

ALTER TABLE ONLY clinica.historial_medico ALTER COLUMN id_historial SET DEFAULT nextval('clinica.historial_medico_id_historial_seq'::regclass);

ALTER TABLE ONLY clinica.paciente ALTER COLUMN id_paciente SET DEFAULT nextval('clinica.paciente_id_paciente_seq'::regclass);

ALTER TABLE ONLY clinica.receta ALTER COLUMN id_receta SET DEFAULT nextval('clinica.receta_id_receta_seq'::regclass);

COPY clinica.cita (id_cita, fecha_hora, id_paciente, id_doctor, motivo) FROM stdin;
\.

COPY clinica.doc_especialidad (id_doctor, id_especialidad) FROM stdin;
1	1
\.

COPY clinica.doctor (id_doctor, nombre, apellido, direccion, telefono) FROM stdin;
1	Daniel	Barboza	Avenida Central, Cartago	8888-0000
\.

COPY clinica.especialidad (id_especialidad, nom_especialidad) FROM stdin;
1	Medicina General
2	Cardiología
3	Pediatría
\.

COPY clinica.historial_medico (id_historial, id_paciente, id_cita, fecha_consulta, diagnostico, tratamiento) FROM stdin;
\.

COPY clinica.paciente (id_paciente, nombre, apellido, fecha_nacimiento, sexo, direccion, telefono, correo) FROM stdin;
7	Juan	Pérez	1990-05-10	M	Cartago	8888-8888	juanperez@example.com
\.

COPY clinica.receta (id_receta, medicamento, dosis, frecuencia, duracion, id_historial) FROM stdin;
\.

SELECT pg_catalog.setval('clinica.cita_id_cita_seq', 1, false);
SELECT pg_catalog.setval('clinica.doctor_id_doctor_seq', 1, true);
SELECT pg_catalog.setval('clinica.especialidad_id_especialidad_seq', 3, true);
SELECT pg_catalog.setval('clinica.historial_medico_id_historial_seq', 1, false);
SELECT pg_catalog.setval('clinica.paciente_id_paciente_seq', 7, true);
SELECT pg_catalog.setval('clinica.receta_id_receta_seq', 1, false);

ALTER TABLE ONLY clinica.cita ADD CONSTRAINT cita_pkey PRIMARY KEY (id_cita);
ALTER TABLE ONLY clinica.doc_especialidad ADD CONSTRAINT doc_especialidad_pkey PRIMARY KEY (id_doctor, id_especialidad);
ALTER TABLE ONLY clinica.doctor ADD CONSTRAINT doctor_pkey PRIMARY KEY (id_doctor);
ALTER TABLE ONLY clinica.especialidad ADD CONSTRAINT especialidad_pkey PRIMARY KEY (id_especialidad);
ALTER TABLE ONLY clinica.historial_medico ADD CONSTRAINT historial_medico_pkey PRIMARY KEY (id_historial);
ALTER TABLE ONLY clinica.paciente ADD CONSTRAINT paciente_pkey PRIMARY KEY (id_paciente);
ALTER TABLE ONLY clinica.receta ADD CONSTRAINT receta_pkey PRIMARY KEY (id_receta);

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
