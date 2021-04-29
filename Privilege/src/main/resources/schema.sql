-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: express
-- ------------------------------------------------------
-- Server version	8.0.20

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) DEFAULT NULL,
  `password` varchar(128) NOT NULL,
  `real_name` varchar(32) DEFAULT NULL,
  `gender` tinyint DEFAULT NULL,
  `role` tinyint DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `email_verify` tinyint(3) unsigned zerofill NOT NULL,
  `mobile` varchar(128) DEFAULT NULL,
  `mobile_verify` tinyint(3) unsigned zerofill NOT NULL,
  `signature` varchar(500) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;