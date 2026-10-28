CREATE TABLE docente (
    id              UUID         NOT NULL,
    identificacion  VARCHAR(20)  NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    estado          VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_docente                PRIMARY KEY (id),
    CONSTRAINT uq_docente_identificacion UNIQUE (identificacion),
    CONSTRAINT ck_docente_estado         CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE estudiante (
    id              UUID         NOT NULL,
    identificacion  VARCHAR(20)  NOT NULL,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    estado          VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_estudiante                PRIMARY KEY (id),
    CONSTRAINT uq_estudiante_identificacion UNIQUE (identificacion),
    CONSTRAINT ck_estudiante_estado         CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE TABLE grupo (
    id          UUID         NOT NULL,
    codigo      VARCHAR(20)  NOT NULL,
    nombre      VARCHAR(150) NOT NULL,
    id_docente  UUID         NOT NULL,
    estado      VARCHAR(10)  NOT NULL,
    CONSTRAINT pk_grupo         PRIMARY KEY (id),
    CONSTRAINT uq_grupo_codigo  UNIQUE (codigo),
    CONSTRAINT fk_grupo_docente FOREIGN KEY (id_docente) REFERENCES docente (id),
    CONSTRAINT ck_grupo_estado  CHECK (estado IN ('VIGENTE', 'CERRADO'))
);

CREATE TABLE estudiante_grupo (
    id             UUID NOT NULL,
    id_estudiante  UUID NOT NULL,
    id_grupo       UUID NOT NULL,
    CONSTRAINT pk_estudiante_grupo            PRIMARY KEY (id),
    CONSTRAINT uq_estudiante_grupo            UNIQUE (id_estudiante, id_grupo),
    CONSTRAINT fk_estudiante_grupo_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id),
    CONSTRAINT fk_estudiante_grupo_grupo      FOREIGN KEY (id_grupo) REFERENCES grupo (id)
);

CREATE TABLE nota (
    id                     UUID         NOT NULL,
    id_grupo               UUID         NOT NULL,
    nombre                 VARCHAR(100) NOT NULL,
    fecha_inicio_registro  TIMESTAMPTZ  NOT NULL,
    fecha_fin_registro     TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_nota          PRIMARY KEY (id),
    CONSTRAINT fk_nota_grupo    FOREIGN KEY (id_grupo) REFERENCES grupo (id),
    CONSTRAINT ck_nota_fechas   CHECK (fecha_fin_registro > fecha_inicio_registro)
);

CREATE TABLE nota_estudiante (
    id                   UUID         NOT NULL,
    id_estudiante_grupo  UUID         NOT NULL,
    id_nota              UUID         NOT NULL,
    valor                INTEGER      NOT NULL,
    fecha_registro       TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_nota_estudiante                  PRIMARY KEY (id),
    CONSTRAINT uq_nota_estudiante                  UNIQUE (id_estudiante_grupo, id_nota),
    CONSTRAINT fk_nota_estudiante_estudiante_grupo FOREIGN KEY (id_estudiante_grupo) REFERENCES estudiante_grupo (id),
    CONSTRAINT fk_nota_estudiante_nota             FOREIGN KEY (id_nota) REFERENCES nota (id),
    CONSTRAINT ck_nota_estudiante_valor            CHECK (valor BETWEEN 0 AND 500)
);

CREATE INDEX ix_grupo_docente ON grupo (id_docente);
CREATE INDEX ix_estudiante_grupo_grupo ON estudiante_grupo (id_grupo);
CREATE INDEX ix_nota_grupo ON nota (id_grupo);
CREATE INDEX ix_nota_estudiante_nota ON nota_estudiante (id_nota);
