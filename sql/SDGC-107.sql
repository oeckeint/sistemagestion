--SDGC-107
--Agregar nuevo bloque de nodo AE 91,92,93

CREATE TABLE `medidaqh` (
  `id_medidaQH` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `tipomed` int DEFAULT NULL,
  `fecha` datetime DEFAULT NULL,
  `bandera_inv_ver` int DEFAULT NULL,
  `actent` int DEFAULT NULL,
  `qactent` int DEFAULT NULL,
  `actsal` int DEFAULT NULL,
  `qactsal` int DEFAULT NULL,
  `r_q1` int DEFAULT NULL,
  `qr_q1` int DEFAULT NULL,
  `r_q2` int DEFAULT NULL,
  `qr_q2` int DEFAULT NULL,
  `r_q3` int DEFAULT NULL,
  `qr_q3` int DEFAULT NULL,
  `r_q4` int DEFAULT NULL,
  `qr_q4` int DEFAULT NULL,
  `medres1` int DEFAULT NULL,
  `qmedres1` int DEFAULT NULL,
  `medres2` int DEFAULT NULL,
  `qmedres2` int DEFAULT NULL,
  `metod_obt` int DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `updated_on` datetime DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `temporal` int DEFAULT NULL,
  KEY `medidaqh_FK` (`id_medidaQH`),
  KEY `id_cliente_FK` (`id_cliente`),
  CONSTRAINT `id_cliente_FK` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

commit;
