#SDGC-91

#Nueva tabla medida_cch

-- sge.medida_cch definition

CREATE TABLE `medida_cch` (
  `id_medida_cch` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `fecha` datetime NOT NULL,
  `bandera_inv_ver` int NOT NULL,
  `actent` int NOT NULL,
  `metod` int NOT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_on` datetime DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_medida_cch`),
  KEY `medida_cch_FK` (`id_cliente`),
  CONSTRAINT `medida_cch_FK` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

COMMIT;