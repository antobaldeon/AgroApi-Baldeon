CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(60) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_codigo UNIQUE (codigo)
) ENGINE=InnoDB;

CREATE TABLE privilegios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(80) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_privilegios_codigo UNIQUE (codigo)
) ENGINE=InnoDB;

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (rol_id)
        REFERENCES roles (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE rol_privilegio (
    rol_id BIGINT NOT NULL,
    privilegio_id BIGINT NOT NULL,
    PRIMARY KEY (rol_id, privilegio_id),
    CONSTRAINT fk_rol_privilegio_rol FOREIGN KEY (rol_id)
        REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_rol_privilegio_privilegio FOREIGN KEY (privilegio_id)
        REFERENCES privilegios (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE lotes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(40) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    hectareas DECIMAL(10, 2) NOT NULL,
    cultivo VARCHAR(100) NOT NULL,
    valle VARCHAR(100) NOT NULL,
    campania_cerrada BOOLEAN NOT NULL DEFAULT FALSE,
    dosis_maxima_por_hectarea DECIMAL(12, 3) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_lotes_codigo UNIQUE (codigo),
    CONSTRAINT chk_lotes_hectareas CHECK (hectareas > 0),
    CONSTRAINT chk_lotes_dosis CHECK (dosis_maxima_por_hectarea >= 0),
    INDEX idx_lotes_valle_estado (valle, campania_cerrada)
) ENGINE=InnoDB;

CREATE TABLE labores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lote_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    responsable_id BIGINT NOT NULL,
    cantidad DECIMAL(12, 3) NOT NULL,
    observacion TEXT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_labores_lote FOREIGN KEY (lote_id)
        REFERENCES lotes (id) ON DELETE RESTRICT,
    CONSTRAINT fk_labores_responsable FOREIGN KEY (responsable_id)
        REFERENCES usuarios (id) ON DELETE RESTRICT,
    CONSTRAINT chk_labores_tipo CHECK (tipo IN ('RIEGO', 'FUMIGACION', 'COSECHA')),
    CONSTRAINT chk_labores_cantidad CHECK (cantidad >= 0),
    INDEX idx_labores_lote_fecha (lote_id, fecha DESC),
    INDEX idx_labores_responsable_fecha (responsable_id, fecha)
) ENGINE=InnoDB;
