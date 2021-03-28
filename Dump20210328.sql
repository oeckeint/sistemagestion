-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: sge
-- ------------------------------------------------------
-- Server version	8.0.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `contenido_xml_otras_facturas`
--

DROP TABLE IF EXISTS `contenido_xml_otras_facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contenido_xml_otras_facturas` (
  `id_cont` int(11) NOT NULL AUTO_INCREMENT,
  `is_deleted` int(11) NOT NULL DEFAULT '0',
  `cod_emp_emi` varchar(4) NOT NULL COMMENT 'codigo_empresa_emisora',
  `cod_emp_des` varchar(4) NOT NULL,
  `cod_pro` varchar(2) NOT NULL,
  `cod_pas` varchar(2) NOT NULL,
  `cod_sol` varchar(20) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `cups` varchar(22) NOT NULL,
  `cod_fis_fac` varchar(45) NOT NULL,
  `tip_fac` varchar(1) NOT NULL,
  `mot_fac` varchar(2) NOT NULL,
  `fec_fac` varchar(10) NOT NULL,
  `com` tinytext NOT NULL,
  `imp_tot_fac` decimal(10,5) NOT NULL,
  `con_rep` varchar(2) NOT NULL,
  `imp_tot_con_rep` decimal(10,5) NOT NULL,
  `id_rem` varchar(30) NOT NULL,
  `com_dev` tinytext,
  `id_err` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_cont`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contenido_xml_otras_facturas`
--

LOCK TABLES `contenido_xml_otras_facturas` WRITE;
/*!40000 ALTER TABLE `contenido_xml_otras_facturas` DISABLE KEYS */;
INSERT INTO `contenido_xml_otras_facturas` VALUES (1,0,'0024','0894','F1','01','3997309',307,'ES0031406170771001FQ0F','F6124N00321827','N','08','2021-03-11','3997309-00',-6.93000,'18',-5.73000,'3997309','','');
/*!40000 ALTER TABLE `contenido_xml_otras_facturas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-28  9:59:11
