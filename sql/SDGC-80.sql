#SDGC-80
#Nueva tabla de Medidas

-- sge.medida definition

CREATE TABLE `medida` (
  `id_medida` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int NOT NULL,
  `fecha` datetime DEFAULT NULL,
  `bandera_inv_ver` int DEFAULT NULL,
  `ae1` int DEFAULT NULL,
  `as1` int DEFAULT NULL,
  `r_q1` int DEFAULT NULL,
  `r_q2` int DEFAULT NULL,
  `r_q3` int DEFAULT NULL,
  `r_q4` int DEFAULT NULL,
  `metod_obt` int DEFAULT NULL,
  `indic_firmez` int DEFAULT NULL,
  `codigo_factura` varchar(27) DEFAULT NULL,
  `medida_col` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_medida`),
  KEY `medida_FK` (`id_cliente`),
  CONSTRAINT `medida_FK` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


COMMIT;