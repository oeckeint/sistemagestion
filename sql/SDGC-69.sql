--SDGC-69
--Tablas para los tickets de los clientes

-- sge.cliente_tickets definition

CREATE TABLE `cliente_tickets` (
  `id_cliente_ticket` int NOT NULL AUTO_INCREMENT,
  `detalles` text NOT NULL,
  `comentarios` text,
  `id_incidencia` int NOT NULL,
  `id_estado_ticket` int NOT NULL,
  `id_cliente` int NOT NULL,
  `is_deleted` int NOT NULL DEFAULT '0',
  `created_on` date DEFAULT NULL,
  `created_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `updated_on` date DEFAULT NULL,
  `updated_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_cliente_ticket`),
  KEY `cliente_tickets_FK` (`id_incidencia`),
  KEY `cliente_tickets_FK_1` (`id_estado_ticket`),
  KEY `cliente_tickets_FK_2` (`id_cliente`),
  CONSTRAINT `cliente_tickets_FK` FOREIGN KEY (`id_incidencia`) REFERENCES `tickets_tipo_incidencia` (`id_incidencia`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cliente_tickets_FK_1` FOREIGN KEY (`id_estado_ticket`) REFERENCES `tickets_estado_incidencia` (`id_estado_ticket`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cliente_tickets_FK_2` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb3 COMMENT='Creacion de tickets que llegan a existir ya sean de quejas o situaciones que pueden presentar los clientes.';


-- sge.tickets_tipo_incidencia definition

CREATE TABLE `tickets_tipo_incidencia` (
  `id_incidencia` int NOT NULL AUTO_INCREMENT,
  `detalles_incidencia` text,
  `is_deleted` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_incidencia`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 COMMENT='Describe el tipo de incidencia que existe para los clientes.';

-- sge.tickets_estado_incidencia definition

CREATE TABLE `tickets_estado_incidencia` (
  `id_estado_ticket` int NOT NULL AUTO_INCREMENT,
  `detalles_estado_incidencia` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  `is_deleted` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_estado_ticket`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COMMENT='El estado refleja en que momento del proceso se encuentra en ticket';

commit;
