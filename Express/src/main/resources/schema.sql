-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: express
-- ------------------------------------------------------
-- Server version	8.0.20

--
-- Table structure for table `demand`
--

DROP TABLE IF EXISTS `demand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `demand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` tinyint DEFAULT NULL,
  `code` varchar(64) NOT NULL,
  `mobile` varchar(128) DEFAULT NULL,
  `address` varchar(256) DEFAULT NULL,
  `destination` varchar(256) DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `sponsor_id` bigint DEFAULT NULL,
  `price` int DEFAULT NULL,
  `comment` varchar(128) DEFAULT NULL,
  `deleted` tinyint(1) unsigned zerofill NOT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `demand_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `status` tinyint DEFAULT NULL,
  `url_check` varchar(256) DEFAULT NULL,
  `url_sent` varchar(256) DEFAULT NULL,
  `deleted` tinyint(1) unsigned zerofill NOT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `status` tinyint DEFAULT NULL,
  `content` varchar(512) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `md5` varchar(128) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;