-- Base de datos para Laragon - MS Monitoreo
-- Ejecutar en HeidiSQL o phpMyAdmin

CREATE DATABASE IF NOT EXISTS db_monitoreo
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_monitoreo;

CREATE TABLE IF NOT EXISTS alertas (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_alerta   VARCHAR(50)  NOT NULL,
    descripcion   VARCHAR(255) NOT NULL,
    estado        VARCHAR(50)  NOT NULL,
    fecha_alerta  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO alertas (tipo_alerta, descripcion, estado, fecha_alerta) VALUES
('CRITICA',     'CPU al 98% por mas de 10 minutos',      'ACTIVA',    NOW()),
('ADVERTENCIA', 'Memoria RAM al 85%',                    'PENDIENTE', NOW()),
('INFO',        'Servicio reiniciado correctamente',      'RESUELTA',  NOW()),
('CRITICA',     'Disco duro al 95% de capacidad',        'ACTIVA',    NOW()),
('ADVERTENCIA', 'Latencia de red aumento a 200ms',       'ACTIVA',    NOW());

SELECT CONCAT('OK: ', COUNT(*), ' alertas insertadas') AS resultado FROM alertas;
