#SDGC-85

#Nueva tabla tipo_reclamacion

-- sge.tipo_reclamacion definition

CREATE TABLE `tipo_reclamacion` (
  `id_tipo_reclamacion` int NOT NULL AUTO_INCREMENT,
  `codigo_reclamacion` int NOT NULL,
  `descripcion` varchar(50) NOT NULL,
  `created_on` date DEFAULT NULL,
  `created_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `updated_on` date DEFAULT NULL,
  `updated_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_tipo_reclamacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

#Nueva tabla de subtipo_reclamacion

-- sge.subtipo_reclamacion definition

CREATE TABLE `subtipo_reclamacion` (
  `id_subtipo_reclamacion` int NOT NULL AUTO_INCREMENT,
  `codigo_reclamacion` int NOT NULL,
  `descripcion` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `created_on` date DEFAULT NULL,
  `created_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `updated_on` date DEFAULT NULL,
  `updated_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_subtipo_reclamacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

#Nueva tabla de Reclamaciones

-- sge.reclamaciones definition

CREATE TABLE `reclamaciones` (
  `id_reclamacion` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `codigo_empresa_emisora` int NOT NULL,
  `codigo_empresa_destino` int NOT NULL,
  `codigo_de_paso` int NOT NULL,
  `codigo_de_solicitud` int NOT NULL,
  `fecha_solicitud` date NOT NULL,
  `fecha_incidente` date NOT NULL,
  `numero_factura_atr` varchar(45) NOT NULL,
  `comentarios` text NOT NULL,
  `id_tipo_reclamacion` int NOT NULL,
  `id_subtipo_reclamacion` int NOT NULL,
  `is_deleted` int DEFAULT NULL,
  `created_on` date NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `updated_on` date NOT NULL,
  `updated_by` varchar(100) NOT NULL,
  PRIMARY KEY (`id_reclamacion`),
  KEY `reclamaciones_FK` (`id_cliente`),
  KEY `reclamaciones_FK_2` (`id_subtipo_reclamacion`),
  KEY `reclamaciones_FK_1` (`id_tipo_reclamacion`),
  CONSTRAINT `reclamaciones_FK` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `reclamaciones_FK_1` FOREIGN KEY (`id_tipo_reclamacion`) REFERENCES `tipo_reclamacion` (`id_tipo_reclamacion`),
  CONSTRAINT `reclamaciones_FK_2` FOREIGN KEY (`id_subtipo_reclamacion`) REFERENCES `subtipo_reclamacion` (`id_subtipo_reclamacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

#Datos de la tabla tipo_reclamacion
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 1, 'Atención Personal', NULL, NULL, NULL, NULL);
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 2, 'Facturación y Medida', NULL, NULL, NULL, NULL);
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 3, 'Contratación', NULL, NULL, NULL, NULL);
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 4, 'Gestión de Acometidas', NULL, NULL, NULL, NULL);
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 5, 'Calidad de Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 6, 'Situación de Instalaciones', NULL, NULL, NULL, NULL);
INSERT INTO sge.tipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 7, 'Atención Reglamentaria', NULL, NULL, NULL, NULL);

#Datos de la tabla de subtipo_reclamacion

INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 1, 'Atencion Incorrecta', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 2, 'Privacidad de los Datos', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 3, 'Incidencia en Equipos de Medida', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 4, 'Daños Originados por Equipo de Medida', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 5, 'Contador en Factura no Corresponde Con Instalado', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 6, 'Contratos ATR que no se Facturan', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 7, 'CUPS no Pertenece a Comercializadora o No Vigente en Periodo de Factura', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 8, 'Disconformidad Con Conceptos Facturados', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 9, 'Disconformidad Con Lectura Facturada', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 10, 'Disconformidad en Factura Anomalía/Fraude', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 11, 'Reclamacion Factura Pago Duplicado', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 12, 'Refacturacion no Recibida', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 13, 'Disconformidad con Cambio de Suministrador', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 14, 'Requerimiento de Fianza/Depósito de Garantía', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 15, 'Retraso Corte de Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 16, 'Retraso en Plazo Aceptación', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 17, 'Retraso en Plazo Activación', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 18, 'Disconformidad con Criterios Económicos/Cobros', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 19, 'Disconformidad con Criterios Técnicos/Obra Ejecutada', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 20, 'Calidad de Onda', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 21, 'Con Petición de Indemnización', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 22, 'Sin Petición de Indemnización', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 23, 'Retraso en Pago Indemnización', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 24, 'Daños a Terceros por Instalaciones', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 25, 'Impacto Ambiental Instalaciones', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 26, 'Reclamaciones Sobre Instalaciones', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 27, 'Disconformidad Descuento Servicio Individual', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 28, 'Ejecución Indebida de Corte', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 29, 'Retraso en la Atención a Reclamaciones', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 30, 'Retraso Plazo de Contestación Nuevos Suministros', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 31, 'Retraso Plazo de Ejecución Nuevo Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 32, 'Retraso Reenganche Tras Corte', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 34, 'Disconformidad con Conceptos de Contratación ATR-Peaje', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 35, 'Disconformidad Rechazo Solicitud ATR-Peaje', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 36, 'Petición de Refacturación Aportando Lectura', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 37, 'Fichero XML incorrecto', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 38, 'Privacidad de los Datos', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 39, 'Solicitud de Certificado/Informe de Calidad', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 40, 'Solicitud de Duplicado de Factura', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 41, 'Solicitud de Actuación sobre Instalaciones', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 42, 'Solicitud de Descargo', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 43, 'Petición de Precintado/Desprecintado de Equipos', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 44, 'Peticiones con Origen en Campañas de Telegestión', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 45, 'Actualización Dirección Punto de Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 46, 'Certificado de Lectura', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 47, 'Solicitud Recalculo CCH sin Modificación cierre ATR', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 48, 'Petición Información Adicional Rechazo', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 49, 'Falta Fichero Medida', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 55, 'Disconformidad Sobre Importe Facturado Autoconsumo', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 56, 'Petición Desglose Importe a Facturar Autoconsumo', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 57, 'Disconformidad con Expediente de Anomalía y Fraude', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 58, 'Retraso en Plazo Aceptación Cambio de Comercializador', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 59, 'Retraso en Plazo Activación Cambio de Comercializador', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 60, 'Retraso en Plazo Aceptación Modificación Contractual', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 61, 'Retraso en Plazo Activacion Modificacion Contractual', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 62, 'Retraso en Plazo Aceptación Alta de un Nuevo Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 63, 'Retraso en Plazo Activación Alta de un Nuevo Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 64, 'Retraso en Plazo Aceptación de una Baja de un Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 65, 'Retraso en Plazo Activación Baja de una Suministro', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 66, 'Información/Validación Sobre Datos del Contratao ATR/PEAJE', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 67, 'Verificacion del Contador', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 69, 'Copia F1 en PDF', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 70, 'Retraso en la Atención a Reclamaciones no Sujetas a Atención Reglamentaria', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 71, 'Retraso en Plazo Aceptación Desistimiento', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 72, 'Retraso en Plazo Activación Desistimiento', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 73, 'Parámetros de Comunicación', NULL, NULL, NULL, NULL);
INSERT INTO sge.subtipo_reclamacion (codigo_reclamacion, descripcion, created_on, created_by, updated_on, updated_by) VALUES( 74, 'Retraso Plazo Aceptación Anulación', NULL, NULL, NULL, NULL);

COMMIT;