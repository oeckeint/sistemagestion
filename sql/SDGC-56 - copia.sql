--SDGC-56
--Nueva tabla de cliente_tickets

--tickets_tipo_incidencia
CREATE TABLE sge.tickets_tipo_incidencia (
	id_incidencia INT auto_increment NOT NULL,
	detalles_incidencia TEXT NULL,
	is_deleted INT DEFAULT 0 NOT NULL,
	CONSTRAINT tickets_tipo_incidencia_pk PRIMARY KEY (id_incidencia)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci
COMMENT='Describe el tipo de incidencia que existe para los clientes.';


--tickets_estado_incidencia
CREATE TABLE sge.tickets_estado_incidencia (
	id_estado_ticket INT auto_increment NOT NULL,
	detalles_estado_incidencia TEXT NOT NULL,
	is_deleted INT DEFAULT 0 NOT NULL,
	CONSTRAINT tickets_estado_incidencia_pk PRIMARY KEY (id_estado_ticket)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci
COMMENT='El estado refleja en que momento del proceso se encuentra en ticket';


--cliente_tickets
-- sge.cliente_tickets definition

CREATE TABLE `cliente_tickets` (
  `id_cliente_ticket` int NOT NULL AUTO_INCREMENT,
  `detalles` text NOT NULL,
  `comentarios` text,
  `id_incidencia` int NOT NULL,
  `id_estado_ticket` int NOT NULL,
  `id_cliente` int NOT NULL,
  PRIMARY KEY (`id_cliente_ticket`),
  KEY `cliente_tickets_FK` (`id_incidencia`),
  KEY `cliente_tickets_FK_1` (`id_estado_ticket`),
  KEY `cliente_tickets_FK_2` (`id_cliente`),
  CONSTRAINT `cliente_tickets_FK` FOREIGN KEY (`id_incidencia`) REFERENCES `tickets_tipo_incidencia` (`id_incidencia`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cliente_tickets_FK_1` FOREIGN KEY (`id_estado_ticket`) REFERENCES `tickets_estado_incidencia` (`id_estado_ticket`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cliente_tickets_FK_2` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COMMENT='Creacion de tickets que llegan a existir ya sean de quejas o situaciones que pueden presentar los clientes.';


commit;
