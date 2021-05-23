--
-- Dumping data for table `demand`
--

LOCK TABLES `demand` WRITE;
/*!40000 ALTER TABLE `demand` DISABLE KEYS */;
INSERT INTO `demand` VALUES (1,0,1,'10-1234','顺丰','13800001111','菜鸟驿站','王二狗','海韵2-123','今天21:00-23:00',0,2,2,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (2,0,1,'10-1235','顺丰','13800001111','菜鸟驿站','王二狗','海韵2-123','今天21:00-23:00',0,2,5,NULL,1,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (3,0,1,'12-1236','中通','13800002222','菜鸟驿站','李狗蛋','海韵8-412','明天09:00-12:00',1,1,5,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (4,0,1,'12-1237','中通','13800002222','菜鸟驿站','李狗蛋','海韵8-412','明天09:00-12:00',1,1,2,NULL,1,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (5,0,1,'14-1238','申通','13800003333','菜鸟驿站','张大牛','海韵6-312','今天11:00-12:00',2,1,2,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (6,0,1,'15-1238','圆通','13800003333','菜鸟驿站','张大牛','海韵6-312','今天11:00-12:00',2,1,2,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (7,0,1,'16-1238','顺丰','13800003333','菜鸟驿站','张大牛','海韵6-312','今天11:00-12:00',2,1,2,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (8,0,1,'14-1239','申通','13800003333','菜鸟驿站','张大牛','海韵6-312','今天11:00-12:00',2,1,5,NULL,1,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (9,0,2,'31-1240','韵达','13800004444','南光鸟箱','赵静安','芙蓉4-108','明天21:00-23:00',3,1,2,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (10,0,2,'3-1241','韵达','13800004444','南光鸟箱','赵静安','芙蓉4-108','明天21:00-23:00',3,1,8,NULL,1,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (11,0,2,'2-1243','百世','13800005555','南光鸟箱','蒋大力','凌云5-501','今天17:00-23:00',4,2,2,NULL,0,'2021-05-06 00:00:00',NULL);
INSERT INTO `demand` VALUES (12,0,2,'3-1247','百世','13800005555','南光鸟箱','蒋大力','凌云5-501','今天17:00-23:00',5,2,9,NULL,1,'2021-05-06 00:00:00',NULL);
/*!40000 ALTER TABLE `demand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'202105060000001111',5,2,'13800001111',0,NULL,NULL,0,'2021-05-06 00:00:00',NULL,NULL,NULL,NULL,'2021-05-06 00:00:00',NULL);
INSERT INTO `orders` VALUES (2,'202105060000001112',3,2,'13800001111',3,NULL,NULL,0,'2021-05-06 00:00:00',NULL,NULL,NULL,'2021-05-06 00:01:00','2021-05-06 00:00:00','2021-05-06 00:01:00');
INSERT INTO `orders` VALUES (3,'202105060000001113',6,2,'13800001111',1,'123.jpg',NULL,0,'2021-05-06 00:00:00','2021-05-06 00:08:00',NULL,NULL,NULL,'2021-05-06 00:00:00','2021-05-06 00:08:00');
INSERT INTO `orders` VALUES (4,'202105060000001114',7,2,'13800001111',2,'124.jpg','125.jpg',0,'2021-05-06 00:00:00','2021-05-06 00:08:00','2021-05-06 00:11:30',NULL,NULL,'2021-05-06 00:00:00','2021-05-06 00:11:30');
INSERT INTO `orders` VALUES (5,'202105060000001115',9,2,'13800001111',9,'126.jpg','127.jpg',0,'2021-05-06 00:00:00','2021-05-06 00:08:00','2021-05-07 00:21:30','2021-05-06 00:22:00',NULL,'2021-05-06 00:00:00','2021-05-06 00:22:00');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'snow', NULL, NULL, 0, 100, 0, NULL, NULL, '3e887ee8e2d56f3e79188a1b22232fe16f3c2fb24927f2ddafc223000e9b6362','2021-04-29 15:00:00',NULL);
INSERT INTO `user` VALUES (2,'ldr', '李东儒', '24320182203222', 1, 100, 0, NULL, NULL, '1caddc0e274cf67b2de259413290dba390e3e642a35277ee6c4aa2cdefff91f6','2021-05-03 00:00:00',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (1,'申通');
INSERT INTO `company` VALUES (2,'中通');
INSERT INTO `company` VALUES (3,'圆通');
INSERT INTO `company` VALUES (4,'韵达');
INSERT INTO `company` VALUES (5,'顺丰');
INSERT INTO `company` VALUES (6,'京东');
INSERT INTO `company` VALUES (7,'邮政');
INSERT INTO `company` VALUES (8,'EMS');
INSERT INTO `company` VALUES (9,'百世');
INSERT INTO `company` VALUES (10,'丹鸟');
INSERT INTO `company` VALUES (11,'极兔');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,1,'{\"快递\":[1,3,9,11]}','菜鸟驿站海韵学生公寓店');
INSERT INTO `address` VALUES (2,1,'{\"快递\":[2]}','菜鸟驿站曾厝垵北二路后厝店');
INSERT INTO `address` VALUES (3,1,'{\"快递\":[6]}','公寓门口手抓饼对面京东快递车');
INSERT INTO `address` VALUES (4,1,'{\"快递\":[4]}','公寓下坡菜鸟快递旁韵达快递点');
INSERT INTO `address` VALUES (5,1,'{\"快递\":[7,8]}','海韵8中国邮政');
INSERT INTO `address` VALUES (6,1,'{\"快递\":[5]}','公寓门口水果街顺丰快递');
INSERT INTO `address` VALUES (7,2,'{\"快递\":[7,8]}','西村门口邮政快递点');
INSERT INTO `address` VALUES (8,2,'{\"快递\":[1,2,3,4,5,6,9,10,11]}','南光鸟箱');
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `campus`
--
LOCK TABLES `campus` WRITE;
/*!40000 ALTER TABLE `campus` DISABLE KEYS */;
INSERT INTO `campus` VALUES (1,'海韵学生公寓','{\"海韵\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18]}');
INSERT INTO `campus` VALUES (2,'思明校区本部','{\"芙蓉\":[1,2,3,4,5,6,7,8,9,10,11,12,13],\"石井\":[1,2,3,4,5,6,7],\"南光\":[4,5,7,\"综合楼\"],\"凌云\":[1,2,3,4,5,6,7,8,9,10,\"东村\"],\"勤业\":[4,6,7],\"丰庭\":[1,2,3,4,5,\"笃行3\"]}');
INSERT INTO `campus` VALUES (3,'思明校区海滨','{\"海滨新区\":[1,2,3]}');
INSERT INTO `campus` VALUES (4,'翔安校区',NULL);
/*!40000 ALTER TABLE `campus` ENABLE KEYS */;
UNLOCK TABLES;