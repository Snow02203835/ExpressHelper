--
-- Dumping data for table `demand`
--

LOCK TABLES `demand` WRITE;
/*!40000 ALTER TABLE `demand` DISABLE KEYS */;
INSERT INTO `demand` VALUES (1,0,'string','string','string','string','string','string',0,1,0,NULL,0,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (2,0,'string','string','string','string','string','string',0,1,0,NULL,1,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (3,0,'string','string','string','string','string','string',1,1,0,NULL,0,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (4,0,'string','string','string','string','string','string',1,1,0,NULL,1,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (5,0,'string','string','string','string','string','string',2,1,0,NULL,0,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (6,0,'string','string','string','string','string','string',2,1,0,NULL,1,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (7,0,'string','string','string','string','string','string',3,1,0,NULL,0,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (8,0,'string','string','string','string','string','string',3,1,0,NULL,1,'2021-04-15 17:13:50',NULL);
INSERT INTO `demand` VALUES (9,0,'string','string','string','string','string','string',4,1,0,NULL,0,'2021-04-15 18:00:00',NULL);
INSERT INTO `demand` VALUES (10,0,'string','string','string','string','string','string',4,1,0,NULL,1,'2021-04-15 18:00:00',NULL);
/*!40000 ALTER TABLE `demand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'snow', NULL, NULL, 0, 100, NULL, NULL, '3e887ee8e2d56f3e79188a1b22232fe16f3c2fb24927f2ddafc223000e9b6362','2021-04-29 15:00:00',NULL);
INSERT INTO `user` VALUES (2,'ldr', '李东儒', '24320182203222', 1, 100, NULL, NULL, '1caddc0e274cf67b2de259413290dba390e3e642a35277ee6c4aa2cdefff91f6','2021-05-03 00:00:00',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;