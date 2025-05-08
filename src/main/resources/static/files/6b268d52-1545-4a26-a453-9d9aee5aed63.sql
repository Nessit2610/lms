-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lms_husc
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `lms_account`
--

DROP TABLE IF EXISTS `lms_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_account` (
  `id` varchar(36) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_USER__ON_USERNAME` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_account`
--

LOCK TABLES `lms_account` WRITE;
/*!40000 ALTER TABLE `lms_account` DISABLE KEYS */;
INSERT INTO `lms_account` VALUES ('07b0010e-52ad-4fc9-8a63-ba54774842fb','admin','$2a$10$EF/bMsOID40ZrXUhP.OOxejGsT/G84bcvL7IYBUv4uEMTNakdlnL.',NULL,_binary '','admin','2025-04-04 07:15:51',NULL,NULL,NULL,NULL),('3b868566-675f-400f-a92b-4282c01cb132','letien@example.com','$2a$10$.VdbG3bRVvg77ZVc5Qgn9.JoUKe1/3KvFgpP0g0CE6WU9so27p2i.','letien@example.com',_binary '','admin','2025-04-04 07:47:30',NULL,NULL,NULL,NULL),('4a807615-aa8c-47c5-a5f5-7e196ab3dbef','letien@teacher.com','$2a$10$o/pV16M59IJs2KPc5LpUXO4PHZCdWZQLrOtFC0z1b5Hpt4T0PDfAu','letien@teacher.com',_binary '','admin','2025-04-04 08:05:36',NULL,NULL,NULL,NULL),('8d467125-1403-4b52-afad-000c3b8b4122','letienvan@example.com','$2a$10$Ko.Kc4PueEqkGHPgO2wGIuLlgTMpUXD/Q/9J0vpSRmKlOewlr01lm','letienvan@example.com',_binary '','admin','2025-04-04 07:48:51',NULL,NULL,NULL,NULL),('f8bbd04f-1e67-11f0-8d49-089798bd050c','21t1080037@husc.edu.vn','$2a$12$aprBSM3t2sswgIouAkImjuwvRvImfGwnubAs.9/kvfxomqvxnLrWe','21t1080037@husc.edu.vn',_binary '','system','2025-04-21 11:20:35',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_account_roles`
--

DROP TABLE IF EXISTS `lms_account_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_account_roles` (
  `Account_id` varchar(36) DEFAULT NULL,
  `roles_name` varchar(255) DEFAULT NULL,
  KEY `FK_LMS_ACCOUNT_ROLES_ON_ACCOUNT` (`Account_id`),
  KEY `FK_LMS_ACCOUNT_ROLES_ON_ROLES_NAME` (`roles_name`),
  CONSTRAINT `FK_LMS_ACCOUNT_ROLES_ON_ACCOUNT` FOREIGN KEY (`Account_id`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `FK_LMS_ACCOUNT_ROLES_ON_ROLES_NAME` FOREIGN KEY (`roles_name`) REFERENCES `lms_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_account_roles`
--

LOCK TABLES `lms_account_roles` WRITE;
/*!40000 ALTER TABLE `lms_account_roles` DISABLE KEYS */;
INSERT INTO `lms_account_roles` VALUES ('07b0010e-52ad-4fc9-8a63-ba54774842fb','ADMIN'),('3b868566-675f-400f-a92b-4282c01cb132','STUDENT'),('8d467125-1403-4b52-afad-000c3b8b4122','STUDENT'),('4a807615-aa8c-47c5-a5f5-7e196ab3dbef','TEACHER');
/*!40000 ALTER TABLE `lms_account_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_acount_device`
--

DROP TABLE IF EXISTS `lms_acount_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_acount_device` (
  `id` varchar(36) NOT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `deviceId` varchar(255) DEFAULT NULL,
  `fcmDeviceToken` varchar(255) DEFAULT NULL,
  `deviceType` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_ACOUNT_DEVICE_ON_ACCOUNTID` (`accountId`),
  CONSTRAINT `FK_LMS_ACOUNT_DEVICE_ON_ACCOUNTID` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_acount_device`
--

LOCK TABLES `lms_acount_device` WRITE;
/*!40000 ALTER TABLE `lms_acount_device` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_acount_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_chapter`
--

DROP TABLE IF EXISTS `lms_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_chapter` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order` int DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lessonId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `createdBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_lesson_id` (`lessonId`),
  CONSTRAINT `fk_lesson_id` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_chapter`
--

LOCK TABLES `lms_chapter` WRITE;
/*!40000 ALTER TABLE `lms_chapter` DISABLE KEYS */;
INSERT INTO `lms_chapter` VALUES ('02c8d310-1bd2-4e69-8693-21b95aacdd31','Bài học số 3',3,NULL,NULL,'a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL),('1372ec35-d18d-4dab-af40-83daaecbc0d2','Bài số học 1',1,'/lms/chapter/files/1372ec35-d18d-4dab-af40-83daaecbc0d2.sql',NULL,'a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL),('19792a73-90c4-4f45-888e-63c05b87b9f4','Bài học số 3',3,NULL,NULL,'a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL),('1bf29d2c-c273-403d-bea0-853bf7f002be','Bài học số 3',3,'/lms/chapter/images/1bf29d2c-c273-403d-bea0-853bf7f002be.jpg','image','a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL),('25d44280-1e39-4e73-8875-fe3efe915e9a','Bài học số 3',3,NULL,NULL,'a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL),('40bed487-012d-480e-a40e-2ce37dce16bf','Bài số học 2',2,'/lms/chapter/files/40bed487-012d-480e-a40e-2ce37dce16bf.sql','file','a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL),('770b5361-93d4-4de4-b26a-3e3f1ed1b6aa','Bài học số 3',3,NULL,NULL,'a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_comment`
--

DROP TABLE IF EXISTS `lms_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_comment` (
  `id` varchar(36) NOT NULL,
  `accountId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `chapterId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `courseId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `lessonMaterialId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comment_account` (`accountId`),
  KEY `fk_comment_chapter` (`chapterId`),
  KEY `fk_comment_course` (`courseId`),
  KEY `fk_comment_lesson_material` (`lessonMaterialId`),
  CONSTRAINT `fk_comment_account` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_comment_chapter` FOREIGN KEY (`chapterId`) REFERENCES `lms_chapter` (`id`),
  CONSTRAINT `fk_comment_course` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`),
  CONSTRAINT `fk_comment_lesson_material` FOREIGN KEY (`lessonMaterialId`) REFERENCES `lms_lesson_material` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_comment`
--

LOCK TABLES `lms_comment` WRITE;
/*!40000 ALTER TABLE `lms_comment` DISABLE KEYS */;
INSERT INTO `lms_comment` VALUES ('00d818ee-6c6e-4b96-b475-d4eb08c3092f','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'tanngo','2025-04-22 02:13:49',NULL,NULL),('03ce1abd-e45c-4ad6-89c6-c89565d989bb','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'ABC XYZ','2025-04-22 02:17:41',NULL,NULL),('03e555f5-26ef-4e5a-a844-7e82c2fdcec1','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'alo xin chào 123','2025-04-22 06:46:41',NULL,NULL),('086bf7d9-ea4f-41ac-953b-12aaf30af281','8d467125-1403-4b52-afad-000c3b8b4122','40bed487-012d-480e-a40e-2ce37dce16bf','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Chuẩn đéo cần chỉnh Tín siêu ngu','2025-04-15 15:18:30',NULL,NULL),('0aaa1f75-59f2-461c-ab61-07a100f4ca29','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'abc','2025-04-22 10:55:33',NULL,NULL),('1a176510-cd76-4f8e-b245-2f11ed8d6a0a','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'djtmemayyyy','2025-04-22 07:20:49',NULL,NULL),('1bcb5053-e29f-4357-abfd-d32c3fa300b5','8d467125-1403-4b52-afad-000c3b8b4122','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Djtme Tín Nguyễn','2025-04-16 05:50:25',NULL,NULL),('1ec41426-1849-43f2-830c-3146af958162','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'alo','2025-04-15 15:13:50',NULL,NULL),('22f5d21d-6146-4277-abe4-821ee156c614','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Djtme Tín Nguyễn','2025-04-22 06:26:44',NULL,NULL),('32362adb-1c7d-48c7-b893-805273c9e4e9','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'abc','2025-04-22 16:01:17',NULL,NULL),('351933df-460e-4b69-8aa2-34b9abfc7481','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'alo 123','2025-04-22 02:43:17',NULL,NULL),('372d14d3-02d3-4732-b84c-22ebc9a8ac92','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'dumami','2025-04-22 06:28:26',NULL,NULL),('3b313445-2858-48b8-8915-60e386c84000','3b868566-675f-400f-a92b-4282c01cb132','40bed487-012d-480e-a40e-2ce37dce16bf','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Tín ngu l','2025-04-16 01:44:12',NULL,NULL),('3f9e70ac-eeb0-476e-b7a2-b224859eb557','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'123','2025-04-22 10:51:33',NULL,NULL),('4737e96b-52e1-482b-b9c9-2fcf90156965','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'websocket cái con cacccccccccc','2025-04-22 06:48:14',NULL,NULL),('4b88039f-d8e1-4a16-bc38-f4470f98d640','8d467125-1403-4b52-afad-000c3b8b4122','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Djtme Tiến Đình','2025-04-16 09:16:12',NULL,NULL),('4f26b9e5-de78-45d8-b3a0-b33042c60aeb','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Moẹ màyyyyyy','2025-04-22 06:52:49',NULL,NULL),('502c6f35-756a-4a85-9547-db03aae2d438','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'abc','2025-04-22 06:29:38',NULL,NULL),('50396122-fdcd-425d-92ef-b886ed08df24','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'? Tín có ngu không','2025-04-22 07:21:17',NULL,NULL),('540c1de6-51b9-43da-8733-ca42e276c0f1','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'aaa','2025-04-22 10:55:42',NULL,NULL),('5ae84c79-0aea-4ebc-858c-946169391080','8d467125-1403-4b52-afad-000c3b8b4122','40bed487-012d-480e-a40e-2ce37dce16bf','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Hế lô','2025-04-15 15:20:33',NULL,NULL),('6c28259a-7132-4e5a-8fd7-dad1dc38f020','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'123','2025-04-23 09:23:43',NULL,NULL),('6d4aec71-c7b8-41e7-a786-b936d54de550','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'abcaa','2025-04-22 06:25:59',NULL,NULL),('7431c759-ae7e-4f84-8efd-dece37f1ff90','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Nam mô a di phò phò','2025-04-22 06:57:41',NULL,NULL),('75351747-efde-43ea-acce-86ba7da782d2','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'abc','2025-04-22 02:13:35',NULL,NULL),('75461711-79b2-4c39-a108-04172e831743','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'djtmemay','2025-04-15 15:15:53',NULL,NULL),('75d9c851-69da-4dec-a0c4-8cf64f5c2bb7','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Chạy giùm tấn với @@','2025-04-22 06:50:03',NULL,NULL),('783ce0b4-0f46-41ee-8db6-77be41635de1','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'dumami chạy giùm taooooo','2025-04-22 06:40:48',NULL,NULL),('80646e98-afe8-491b-81eb-b60e8c2fb33c','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Djtme Tiến Đình','2025-04-23 04:02:41',NULL,NULL),('86046696-8bf1-4d20-a3b2-e4b94b05358c','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'nam mô ai di phò phò','2025-04-22 06:49:26',NULL,NULL),('8799d0b0-8fba-41c6-966b-7d1a297e7f67','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'aaa','2025-04-22 02:27:34',NULL,NULL),('8816dd0e-b190-41bb-8c3a-51407c57132d','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'tanngo','2025-04-15 15:08:41',NULL,NULL),('931a3df4-5f05-47a4-9c20-da88c183a86d','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'av','2025-04-22 10:45:51',NULL,NULL),('949cf470-46a5-405b-8725-647537b9a24f','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Tiến có ngu không','2025-04-23 01:37:01',NULL,NULL),('a81e983d-1f46-4c9a-8566-89cd34ed7f02','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'dumami TÍn nGUỸên','2025-04-16 02:15:15',NULL,NULL),('a9c7120b-d75a-46f6-8d86-0f32b5c6464a','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'aaa','2025-04-22 02:29:02',NULL,NULL),('b9fa9018-1fa4-46c0-9077-9b7b78b658de','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'123123312','2025-04-22 10:51:39',NULL,NULL),('be542e3c-4b5f-4369-aa64-981a5b7e756e','8d467125-1403-4b52-afad-000c3b8b4122','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'duma levantien','2025-04-15 15:14:32',NULL,NULL),('c18d6a1a-f531-43ee-a59f-09ff2a076f9a','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Djtme Tín Nguyễn','2025-04-22 06:27:20',NULL,NULL),('c589cc32-118e-402d-9bc1-a439264ed155','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'WTF 1970','2025-04-16 05:50:46',NULL,NULL),('ce2d6f99-d2b2-4226-b0d1-4e455fcb86eb','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Djtme Tín nguyễn','2025-04-16 09:15:35',NULL,NULL),('cf2bda70-e1d0-4721-a391-94e2d23d0e9b','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'a di phò phò','2025-04-22 16:14:28',NULL,NULL),('da1c218f-5d53-4b34-87a9-c4e02bcb46de','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Tín ngu vl','2025-04-15 15:11:14',NULL,NULL),('e9e91efc-cc3f-4fab-9571-87ad0f9ec428','3b868566-675f-400f-a92b-4282c01cb132','40bed487-012d-480e-a40e-2ce37dce16bf','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'Tín thì ngu thôi rồi','2025-04-15 15:18:17',NULL,NULL),('ea7e974d-6240-41d8-b63e-4a0fb9ee62f6','3b868566-675f-400f-a92b-4282c01cb132','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'tan dep trai vl','2025-04-15 15:08:55',NULL,NULL),('efeee6b8-9a31-4246-9027-0b6439b4a0d8','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'abc','2025-04-22 02:29:22',NULL,NULL),('f27e925c-0456-4197-b272-ee0a79678db3','8d467125-1403-4b52-afad-000c3b8b4122','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'mamaytien','2025-04-15 15:16:36',NULL,NULL),('f4ab9986-528a-4eaf-bec9-cbdeefc8ad3e','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'dumami Tiến Đình','2025-04-23 04:01:26',NULL,NULL),('fbab5f08-8b1f-40b0-afea-db6f53d72e72','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,'dumaaaa','2025-04-22 06:58:00',NULL,NULL);
/*!40000 ALTER TABLE `lms_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_comment_read_status`
--

DROP TABLE IF EXISTS `lms_comment_read_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_comment_read_status` (
  `id` varchar(36) NOT NULL,
  `commentId` varchar(36) DEFAULT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `isRead` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_account` (`accountId`),
  KEY `fk_comment` (`commentId`),
  CONSTRAINT `fk_account` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_comment` FOREIGN KEY (`commentId`) REFERENCES `lms_comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_comment_read_status`
--

LOCK TABLES `lms_comment_read_status` WRITE;
/*!40000 ALTER TABLE `lms_comment_read_status` DISABLE KEYS */;
INSERT INTO `lms_comment_read_status` VALUES ('213ec189-2a22-4d60-bce5-73dcd2dd1949','949cf470-46a5-405b-8725-647537b9a24f','f8bbd04f-1e67-11f0-8d49-089798bd050c',0),('54d16547-1165-4e2c-aa7d-9d798a7e391d','6c28259a-7132-4e5a-8fd7-dad1dc38f020','3b868566-675f-400f-a92b-4282c01cb132',0),('5fc7c3cf-c091-440a-b09f-c234bea663df','949cf470-46a5-405b-8725-647537b9a24f','3b868566-675f-400f-a92b-4282c01cb132',1),('676fc613-1fe3-479d-9936-a8c15d9be017','cf2bda70-e1d0-4721-a391-94e2d23d0e9b','f8bbd04f-1e67-11f0-8d49-089798bd050c',1),('84e31b98-cae5-4142-aaa2-efb243cf7db4','32362adb-1c7d-48c7-b893-805273c9e4e9','3b868566-675f-400f-a92b-4282c01cb132',0),('8a1548da-145c-4a92-b8a5-4d9e5760c9b0','f4ab9986-528a-4eaf-bec9-cbdeefc8ad3e','f8bbd04f-1e67-11f0-8d49-089798bd050c',1),('a425feb4-6468-4cf0-a7d0-c86d2d51d606','32362adb-1c7d-48c7-b893-805273c9e4e9','f8bbd04f-1e67-11f0-8d49-089798bd050c',1),('b2d93230-4b2b-45a9-92c8-71325a50512c','6c28259a-7132-4e5a-8fd7-dad1dc38f020','f8bbd04f-1e67-11f0-8d49-089798bd050c',1),('c744f1f8-6941-4ea0-bafa-bf43285a27cd','f4ab9986-528a-4eaf-bec9-cbdeefc8ad3e','3b868566-675f-400f-a92b-4282c01cb132',0),('d87ffda0-ca5c-4f69-b9c2-0ac7ce92ef7a','80646e98-afe8-491b-81eb-b60e8c2fb33c','3b868566-675f-400f-a92b-4282c01cb132',1),('f20bba0a-78ad-410e-a3fe-2dfda5e35b31','cf2bda70-e1d0-4721-a391-94e2d23d0e9b','3b868566-675f-400f-a92b-4282c01cb132',0),('f3e18e4e-488e-45bf-8015-9ffecc78e336','80646e98-afe8-491b-81eb-b60e8c2fb33c','f8bbd04f-1e67-11f0-8d49-089798bd050c',0);
/*!40000 ALTER TABLE `lms_comment_read_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_comment_reply`
--

DROP TABLE IF EXISTS `lms_comment_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_comment_reply` (
  `id` varchar(36) NOT NULL,
  `ownerAccountId` varchar(36) DEFAULT NULL,
  `replyAccountId` varchar(36) DEFAULT NULL,
  `chapterId` varchar(36) DEFAULT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `commentId` varchar(36) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comment_reply_owner_account` (`ownerAccountId`),
  KEY `fk_comment_reply_reply_account` (`replyAccountId`),
  KEY `fk_comment_reply_chapter` (`chapterId`),
  KEY `fk_comment_reply_course` (`courseId`),
  KEY `fk_comment_reply_comment` (`commentId`),
  CONSTRAINT `fk_comment_reply_chapter` FOREIGN KEY (`chapterId`) REFERENCES `lms_chapter` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_reply_comment` FOREIGN KEY (`commentId`) REFERENCES `lms_comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_reply_course` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_reply_owner_account` FOREIGN KEY (`ownerAccountId`) REFERENCES `lms_account` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_reply_reply_account` FOREIGN KEY (`replyAccountId`) REFERENCES `lms_account` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_comment_reply`
--

LOCK TABLES `lms_comment_reply` WRITE;
/*!40000 ALTER TABLE `lms_comment_reply` DISABLE KEYS */;
INSERT INTO `lms_comment_reply` VALUES ('4630a456-1226-4d95-b29b-085279fb02df','f8bbd04f-1e67-11f0-8d49-089798bd050c','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4','6c28259a-7132-4e5a-8fd7-dad1dc38f020','123','2025-04-23 09:33:49',NULL,NULL),('4dfc4fac-0200-4346-a69e-a8d3254bf320','f8bbd04f-1e67-11f0-8d49-089798bd050c','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4','6c28259a-7132-4e5a-8fd7-dad1dc38f020','Cái con mẹ màyyy','2025-04-23 09:37:24',NULL,NULL),('a837f53a-3c49-441f-8d95-cd63f15cb8f8','f8bbd04f-1e67-11f0-8d49-089798bd050c','f8bbd04f-1e67-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2','76cf929a-ae12-42c6-b964-7358f3e21ed4','6c28259a-7132-4e5a-8fd7-dad1dc38f020','13123123','2025-04-23 10:10:11',NULL,NULL);
/*!40000 ALTER TABLE `lms_comment_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_confirmation_token`
--

DROP TABLE IF EXISTS `lms_confirmation_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_confirmation_token` (
  `id` varchar(36) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `isVerify` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_confirmation_token`
--

LOCK TABLES `lms_confirmation_token` WRITE;
/*!40000 ALTER TABLE `lms_confirmation_token` DISABLE KEYS */;
INSERT INTO `lms_confirmation_token` VALUES ('090b59b6-22c9-4aba-aad2-f95e0c077161','255271','2025-04-18 05:44:00.432316','ngotan2k3@gmail.com',_binary '\0'),('fa8cffac-080f-4eaa-81a0-35197f745362','260724','2025-04-21 04:09:04.136182','21t1080037@husc.edu.vn',_binary '');
/*!40000 ALTER TABLE `lms_confirmation_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_course`
--

DROP TABLE IF EXISTS `lms_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_course` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `startDate` datetime(6) DEFAULT NULL,
  `endDate` datetime(6) DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `learningDurationType` varchar(255) DEFAULT NULL,
  `teacherId` varchar(36) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_COURSE_MAJOR` (`major`),
  KEY `IDX_LMS_COURSE_TEACHERID` (`teacherId`),
  CONSTRAINT `FK_LMS_COURSE_ON_TEACHERID` FOREIGN KEY (`teacherId`) REFERENCES `lms_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_course`
--

LOCK TABLES `lms_course` WRITE;
/*!40000 ALTER TABLE `lms_course` DISABLE KEYS */;
INSERT INTO `lms_course` VALUES ('76cf929a-ae12-42c6-b964-7358f3e21ed4','Lập trình Java cơ bản','Khóa học nhập môn lập trình Java cho người mới bắt đầu.','2025-04-10 00:00:00.000000','2025-07-10 00:00:00.000000','Công nghệ thông tin','PUBLIC','Có thời hạn','f16bd037-35aa-4fba-bcd7-e0538bf43c83','/lms/course/image/76cf929a-ae12-42c6-b964-7358f3e21ed4.jpg','letien@teacher.com','2025-04-04 09:14:22',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_document`
--

DROP TABLE IF EXISTS `lms_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_document` (
  `id` varchar(36) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` longtext,
  `status` varchar(255) DEFAULT NULL,
  `majorId` varchar(36) DEFAULT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_document_major` (`majorId`),
  KEY `fk_document_account` (`accountId`),
  CONSTRAINT `fk_document_account` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_document_major` FOREIGN KEY (`majorId`) REFERENCES `lms_major` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_document`
--

LOCK TABLES `lms_document` WRITE;
/*!40000 ALTER TABLE `lms_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_group`
--

DROP TABLE IF EXISTS `lms_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_group` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `teacherId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrfnldyynxoi3cneskfn7w6ct9` (`teacherId`),
  CONSTRAINT `FKrfnldyynxoi3cneskfn7w6ct9` FOREIGN KEY (`teacherId`) REFERENCES `lms_teacher` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_group`
--

LOCK TABLES `lms_group` WRITE;
/*!40000 ALTER TABLE `lms_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_group_post`
--

DROP TABLE IF EXISTS `lms_group_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_group_post` (
  `Group_id` varchar(36) NOT NULL,
  `post_id` varchar(36) NOT NULL,
  UNIQUE KEY `UK8bhll0nlomjjcj57e951wvuy` (`post_id`),
  KEY `FKjicnlxha9ulny6tpx49gjvpgk` (`Group_id`),
  CONSTRAINT `FKjicnlxha9ulny6tpx49gjvpgk` FOREIGN KEY (`Group_id`) REFERENCES `lms_group` (`id`),
  CONSTRAINT `FKnf4qw2pbiotoooocrhn1eq90t` FOREIGN KEY (`post_id`) REFERENCES `lms_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_group_post`
--

LOCK TABLES `lms_group_post` WRITE;
/*!40000 ALTER TABLE `lms_group_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_group_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_group_studentgroups`
--

DROP TABLE IF EXISTS `lms_group_studentgroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_group_studentgroups` (
  `Group_id` varchar(36) NOT NULL,
  `studentGroups_id` varchar(36) NOT NULL,
  UNIQUE KEY `UKdnke8fiwpmv2ovm9j4ax74qkj` (`studentGroups_id`),
  KEY `FKtaxnax655fmkgv1d72btdfw4` (`Group_id`),
  CONSTRAINT `FKjpyvq7kqwq080umc04ieka28y` FOREIGN KEY (`studentGroups_id`) REFERENCES `lms_student_group` (`id`),
  CONSTRAINT `FKtaxnax655fmkgv1d72btdfw4` FOREIGN KEY (`Group_id`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_group_studentgroups`
--

LOCK TABLES `lms_group_studentgroups` WRITE;
/*!40000 ALTER TABLE `lms_group_studentgroups` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_group_studentgroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_invalidated_token`
--

DROP TABLE IF EXISTS `lms_invalidated_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiryTime` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_invalidated_token`
--

LOCK TABLES `lms_invalidated_token` WRITE;
/*!40000 ALTER TABLE `lms_invalidated_token` DISABLE KEYS */;
INSERT INTO `lms_invalidated_token` VALUES ('08ab3a9f-9ebc-42ad-b88e-f9cd3fb6f778','2025-04-15 03:15:57.000000');
/*!40000 ALTER TABLE `lms_invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_join_class_requests`
--

DROP TABLE IF EXISTS `lms_join_class_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_join_class_requests` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `courseId` varchar(36) NOT NULL,
  `studentId` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf5q2c371aaki931081xo2dpps` (`courseId`),
  KEY `FKakifs76ktnpq1dviphcw7e47y` (`studentId`),
  CONSTRAINT `FKakifs76ktnpq1dviphcw7e47y` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`),
  CONSTRAINT `FKf5q2c371aaki931081xo2dpps` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_join_class_requests`
--

LOCK TABLES `lms_join_class_requests` WRITE;
/*!40000 ALTER TABLE `lms_join_class_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_join_class_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_lesson`
--

DROP TABLE IF EXISTS `lms_lesson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_lesson` (
  `id` varchar(36) NOT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `order` int DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LESSON_COURSE` (`courseId`),
  CONSTRAINT `FK_LMS_LESSON_ON_COURSEID` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_lesson`
--

LOCK TABLES `lms_lesson` WRITE;
/*!40000 ALTER TABLE `lms_lesson` DISABLE KEYS */;
INSERT INTO `lms_lesson` VALUES ('a67ee588-6f96-4463-aaee-a5f72ed99272','76cf929a-ae12-42c6-b964-7358f3e21ed4','Bài mở đầu java cơ bản',1,'letien@teacher.com',NULL,NULL,NULL,NULL,NULL),('d98cef86-7a02-400b-abca-2f13dbac823d','76cf929a-ae12-42c6-b964-7358f3e21ed4','Bài 2 java cơ bản',2,'letien@teacher.com',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_lesson` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_lesson_material`
--

DROP TABLE IF EXISTS `lms_lesson_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_lesson_material` (
  `id` varchar(36) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lessonId` varchar(36) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LESSON_MATERIAL_LESSONID` (`lessonId`),
  CONSTRAINT `FK_LMS_LESSON_MATERIAL_ON_LESSONID` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_lesson_material`
--

LOCK TABLES `lms_lesson_material` WRITE;
/*!40000 ALTER TABLE `lms_lesson_material` DISABLE KEYS */;
INSERT INTO `lms_lesson_material` VALUES ('4be336fd-6603-447b-9508-dd96ee43f684','/lms/lessonmaterial/files/4be336fd-6603-447b-9508-dd96ee43f684.docx',NULL,'a67ee588-6f96-4463-aaee-a5f72ed99272',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_lesson_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_lesson_quiz`
--

DROP TABLE IF EXISTS `lms_lesson_quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_lesson_quiz` (
  `id` varchar(36) NOT NULL,
  `lessonId` varchar(36) DEFAULT NULL,
  `question` varchar(255) DEFAULT NULL,
  `option` varchar(255) DEFAULT NULL,
  `answer` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LESSON_QUIZ_LESSONID` (`lessonId`),
  CONSTRAINT `FK_LMS_LESSON_QUIZ_ON_LESSONID` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_lesson_quiz`
--

LOCK TABLES `lms_lesson_quiz` WRITE;
/*!40000 ALTER TABLE `lms_lesson_quiz` DISABLE KEYS */;
INSERT INTO `lms_lesson_quiz` VALUES ('20e6cdaa-d7fc-4295-a0fe-69443a4753d1','a67ee588-6f96-4463-aaee-a5f72ed99272','What is the capital of France?','A. Paris; B. London; C. Berlin; D. Madrid','A','admin','2025-04-05 08:27:59',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_lesson_quiz` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_major`
--

DROP TABLE IF EXISTS `lms_major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_major` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `deleted_by` varchar(255) DEFAULT NULL,
  `deleted_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_major`
--

LOCK TABLES `lms_major` WRITE;
/*!40000 ALTER TABLE `lms_major` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_messages`
--

DROP TABLE IF EXISTS `lms_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_messages` (
  `id` varchar(36) NOT NULL,
  `sendAccountId` varchar(36) DEFAULT NULL,
  `receiveAccountId` varchar(36) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` text,
  `createAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_send_account` (`sendAccountId`),
  KEY `fk_receive_account` (`receiveAccountId`),
  CONSTRAINT `fk_receive_account` FOREIGN KEY (`receiveAccountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_send_account` FOREIGN KEY (`sendAccountId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_messages`
--

LOCK TABLES `lms_messages` WRITE;
/*!40000 ALTER TABLE `lms_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_notifications`
--

DROP TABLE IF EXISTS `lms_notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_notifications` (
  `id` varchar(36) NOT NULL,
  `receiveAccountId` varchar(36) NOT NULL,
  `commentId` varchar(36) DEFAULT NULL,
  `messageId` varchar(36) DEFAULT NULL,
  `type` varchar(50) NOT NULL,
  `isRead` tinyint(1) DEFAULT '0',
  `description` text,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `commentReplyId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_notification_receive_account` (`receiveAccountId`),
  KEY `fk_notification_message` (`messageId`),
  KEY `fk_notification_comment` (`commentId`),
  KEY `fk_notification_comment_reply` (`commentReplyId`),
  CONSTRAINT `fk_notification_comment` FOREIGN KEY (`commentId`) REFERENCES `lms_comment` (`id`),
  CONSTRAINT `fk_notification_comment_reply` FOREIGN KEY (`commentReplyId`) REFERENCES `lms_comment_reply` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_notification_message` FOREIGN KEY (`messageId`) REFERENCES `lms_messages` (`id`),
  CONSTRAINT `fk_notification_receive_account` FOREIGN KEY (`receiveAccountId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_notifications`
--

LOCK TABLES `lms_notifications` WRITE;
/*!40000 ALTER TABLE `lms_notifications` DISABLE KEYS */;
INSERT INTO `lms_notifications` VALUES ('108dbca6-0fa0-499f-945c-99d73f15374a','3b868566-675f-400f-a92b-4282c01cb132','cf2bda70-e1d0-4721-a391-94e2d23d0e9b',NULL,'COMMENT',1,'a di phò phò','2025-04-22 16:14:28',NULL),('2539aa2f-ea30-4336-834f-d6170879d54e','3b868566-675f-400f-a92b-4282c01cb132','6c28259a-7132-4e5a-8fd7-dad1dc38f020',NULL,'COMMENT',0,'123','2025-04-23 09:23:43',NULL),('33176128-051e-48a9-bc77-bda525cbfee2','f8bbd04f-1e67-11f0-8d49-089798bd050c','949cf470-46a5-405b-8725-647537b9a24f',NULL,'COMMENT',0,'Tiến có ngu không','2025-04-23 01:37:02',NULL),('7899751f-3f5f-45e7-8343-7d1c6df94665','f8bbd04f-1e67-11f0-8d49-089798bd050c','80646e98-afe8-491b-81eb-b60e8c2fb33c',NULL,'COMMENT',0,'Djtme Tiến Đình','2025-04-23 04:02:41',NULL),('833e32dd-9e8b-45fa-961f-1df0d5092ad6','3b868566-675f-400f-a92b-4282c01cb132','32362adb-1c7d-48c7-b893-805273c9e4e9',NULL,'COMMENT',1,'abc','2025-04-22 16:01:17',NULL),('ac09bb57-e997-4f23-9816-75cd57da2814','3b868566-675f-400f-a92b-4282c01cb132','f4ab9986-528a-4eaf-bec9-cbdeefc8ad3e',NULL,'COMMENT',1,'dumami Tiến Đình','2025-04-23 04:01:26',NULL);
/*!40000 ALTER TABLE `lms_notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_permission`
--

DROP TABLE IF EXISTS `lms_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_permission` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_permission`
--

LOCK TABLES `lms_permission` WRITE;
/*!40000 ALTER TABLE `lms_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_post`
--

DROP TABLE IF EXISTS `lms_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_post` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `text` text,
  `title` varchar(255) DEFAULT NULL,
  `groupId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtfklmh7u6wocpsgrp34xsx2q8` (`groupId`),
  CONSTRAINT `FKtfklmh7u6wocpsgrp34xsx2q8` FOREIGN KEY (`groupId`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_post`
--

LOCK TABLES `lms_post` WRITE;
/*!40000 ALTER TABLE `lms_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_role`
--

DROP TABLE IF EXISTS `lms_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_role`
--

LOCK TABLES `lms_role` WRITE;
/*!40000 ALTER TABLE `lms_role` DISABLE KEYS */;
INSERT INTO `lms_role` VALUES ('ADMIN','Admin role'),('STUDENT','Student role'),('TEACHER','Teacher role');
/*!40000 ALTER TABLE `lms_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_role_permission`
--

DROP TABLE IF EXISTS `lms_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_role_permission` (
  `Role_name` varchar(255) DEFAULT NULL,
  `permission_name` varchar(255) DEFAULT NULL,
  KEY `FK_LMS_ROLE_PERMISSION_ON_PERMISSION_NAME` (`permission_name`),
  KEY `FK_LMS_ROLE_PERMISSION_ON_ROLE_NAME` (`Role_name`),
  CONSTRAINT `FK_LMS_ROLE_PERMISSION_ON_PERMISSION_NAME` FOREIGN KEY (`permission_name`) REFERENCES `lms_permission` (`name`),
  CONSTRAINT `FK_LMS_ROLE_PERMISSION_ON_ROLE_NAME` FOREIGN KEY (`Role_name`) REFERENCES `lms_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_role_permission`
--

LOCK TABLES `lms_role_permission` WRITE;
/*!40000 ALTER TABLE `lms_role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student`
--

DROP TABLE IF EXISTS `lms_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student` (
  `id` varchar(36) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `majorId` varchar(36) DEFAULT NULL,
  `accountId` varchar(36) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_STUDENT_ON_ACCOUNTID` (`accountId`),
  KEY `fk_student_major` (`majorId`),
  CONSTRAINT `FK_LMS_STUDENT_ON_ACCOUNTID` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`),
  CONSTRAINT `fk_student_major` FOREIGN KEY (`majorId`) REFERENCES `lms_major` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student`
--

LOCK TABLES `lms_student` WRITE;
/*!40000 ALTER TABLE `lms_student` DISABLE KEYS */;
INSERT INTO `lms_student` VALUES ('d5c10af7-1e68-11f0-8d49-089798bd0502','Djtme Lê Tiến','letien@example.com',NULL,NULL,'3b868566-675f-400f-a92b-4282c01cb132',NULL),('d5c10af7-1e68-11f0-8d49-089798bd050c','Ngô Tấn','21t1080037@husc.edu.vn',NULL,NULL,'f8bbd04f-1e67-11f0-8d49-089798bd050c',NULL);
/*!40000 ALTER TABLE `lms_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student_course`
--

DROP TABLE IF EXISTS `lms_student_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student_course` (
  `id` varchar(36) NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `courseId` varchar(36) DEFAULT NULL,
  `registrationDate` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_STUDENT_COURSE_COURSEID` (`courseId`),
  KEY `IDX_STUDENT_COURSE_STUDENTID` (`studentId`),
  CONSTRAINT `FK_LMS_STUDENT_COURSE_ON_COURSEID` FOREIGN KEY (`courseId`) REFERENCES `lms_course` (`id`),
  CONSTRAINT `FK_LMS_STUDENT_COURSE_ON_STUDENTID` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student_course`
--

LOCK TABLES `lms_student_course` WRITE;
/*!40000 ALTER TABLE `lms_student_course` DISABLE KEYS */;
INSERT INTO `lms_student_course` VALUES ('2e642e91-5ace-4f0c-93f6-b41121f46e11','d5c10af7-1e68-11f0-8d49-089798bd050c','76cf929a-ae12-42c6-b964-7358f3e21ed4','2025-04-21 04:29:38',NULL,NULL,'2025-04-21 04:29:38',NULL,NULL,NULL,NULL),('2e642e91-5ace-4f0c-93f6-b41121f46e14','d5c10af7-1e68-11f0-8d49-089798bd0502','76cf929a-ae12-42c6-b964-7358f3e21ed4',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_student_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student_document`
--

DROP TABLE IF EXISTS `lms_student_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student_document` (
  `id` varchar(36) NOT NULL,
  `documentId` varchar(36) DEFAULT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `createAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_student_document_document` (`documentId`),
  KEY `fk_student_document_student` (`studentId`),
  CONSTRAINT `fk_student_document_document` FOREIGN KEY (`documentId`) REFERENCES `lms_document` (`id`),
  CONSTRAINT `fk_student_document_student` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student_document`
--

LOCK TABLES `lms_student_document` WRITE;
/*!40000 ALTER TABLE `lms_student_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_student_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student_group`
--

DROP TABLE IF EXISTS `lms_student_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student_group` (
  `id` varchar(36) NOT NULL,
  `joinAt` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `groupId` varchar(36) DEFAULT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK416tvrdnpqofmyg40cvgki40a` (`groupId`),
  KEY `FK38wcyflqxe0woknfla369wi3j` (`studentId`),
  CONSTRAINT `FK38wcyflqxe0woknfla369wi3j` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`),
  CONSTRAINT `FK416tvrdnpqofmyg40cvgki40a` FOREIGN KEY (`groupId`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student_group`
--

LOCK TABLES `lms_student_group` WRITE;
/*!40000 ALTER TABLE `lms_student_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_student_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student_lesson_chapter_progress`
--

DROP TABLE IF EXISTS `lms_student_lesson_chapter_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student_lesson_chapter_progress` (
  `id` varchar(36) NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `chapterId` varchar(36) DEFAULT NULL,
  `isCompleted` tinyint(1) DEFAULT NULL,
  `completeAt` datetime DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_progress_student` (`studentId`),
  KEY `fk_progress_chapter` (`chapterId`),
  CONSTRAINT `fk_progress_chapter` FOREIGN KEY (`chapterId`) REFERENCES `lms_chapter` (`id`),
  CONSTRAINT `fk_progress_student` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student_lesson_chapter_progress`
--

LOCK TABLES `lms_student_lesson_chapter_progress` WRITE;
/*!40000 ALTER TABLE `lms_student_lesson_chapter_progress` DISABLE KEYS */;
INSERT INTO `lms_student_lesson_chapter_progress` VALUES ('ac1d05d0-2ce7-4058-a65e-205346b071d4','d5c10af7-1e68-11f0-8d49-089798bd050c','1372ec35-d18d-4dab-af40-83daaecbc0d2',1,'2025-04-21 04:52:37','21t1080037@husc.edu.vn','2025-04-21 04:50:10','21t1080037@husc.edu.vn','2025-04-21 04:52:37',NULL,NULL),('ac1d05d0-2ce7-4058-a65e-205346b071d5','d5c10af7-1e68-11f0-8d49-089798bd0502','1372ec35-d18d-4dab-af40-83daaecbc0d2',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_student_lesson_chapter_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student_lesson_progress`
--

DROP TABLE IF EXISTS `lms_student_lesson_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student_lesson_progress` (
  `id` varchar(36) NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `lessonId` varchar(36) DEFAULT NULL,
  `isCompleted` bit(1) DEFAULT NULL,
  `completeAt` datetime DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_STUDENT_LESSON_PROGRESS_LESSONID` (`lessonId`),
  KEY `IDX_STUDENT_LESSON_PROGRESS_STUDENTID` (`studentId`),
  CONSTRAINT `FK_LMS_STUDENT_LESSON_PROGRESS_ON_LESSONID` FOREIGN KEY (`lessonId`) REFERENCES `lms_lesson` (`id`),
  CONSTRAINT `FK_LMS_STUDENT_LESSON_PROGRESS_ON_STUDENTID` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student_lesson_progress`
--

LOCK TABLES `lms_student_lesson_progress` WRITE;
/*!40000 ALTER TABLE `lms_student_lesson_progress` DISABLE KEYS */;
INSERT INTO `lms_student_lesson_progress` VALUES ('15845c9f-9145-4a16-82a1-6ca4e7a63a89','d5c10af7-1e68-11f0-8d49-089798bd050c','a67ee588-6f96-4463-aaee-a5f72ed99272',_binary '','2025-04-21 04:46:38','21t1080037@husc.edu.vn','2025-04-21 04:41:09','21t1080037@husc.edu.vn','2025-04-21 04:46:38',NULL,NULL),('45b3f191-9ff2-47ca-838a-7af4ca2b60ab','d5c10af7-1e68-11f0-8d49-089798bd050c','d98cef86-7a02-400b-abca-2f13dbac823d',_binary '','2025-04-21 04:47:37','21t1080037@husc.edu.vn','2025-04-21 04:47:32','21t1080037@husc.edu.vn','2025-04-21 04:47:37',NULL,NULL),('45b3f191-9ff2-47ca-838a-7af4ca2b60ac','d5c10af7-1e68-11f0-8d49-089798bd0502','a67ee588-6f96-4463-aaee-a5f72ed99272',_binary '\0',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_student_lesson_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_teacher`
--

DROP TABLE IF EXISTS `lms_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_teacher` (
  `id` varchar(36) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `userId` varchar(36) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `lastModifiedBy` varchar(255) DEFAULT NULL,
  `lastModifiedDate` datetime DEFAULT NULL,
  `deletedBy` varchar(255) DEFAULT NULL,
  `deletedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_TEACHER_ON_USERID` (`userId`),
  CONSTRAINT `FK_LMS_TEACHER_ON_USERID` FOREIGN KEY (`userId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_teacher`
--

LOCK TABLES `lms_teacher` WRITE;
/*!40000 ALTER TABLE `lms_teacher` DISABLE KEYS */;
INSERT INTO `lms_teacher` VALUES ('f16bd037-35aa-4fba-bcd7-e0538bf43c83','Lê Văn Tiến','letien@teacher.com','4a807615-aa8c-47c5-a5f5-7e196ab3dbef',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_test_in_group`
--

DROP TABLE IF EXISTS `lms_test_in_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_test_in_group` (
  `id` varchar(36) NOT NULL,
  `createdAt` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `expiredAt` datetime(6) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `groupId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjatheue0p87a0x6ubgi4tru4i` (`groupId`),
  CONSTRAINT `FKjatheue0p87a0x6ubgi4tru4i` FOREIGN KEY (`groupId`) REFERENCES `lms_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_test_in_group`
--

LOCK TABLES `lms_test_in_group` WRITE;
/*!40000 ALTER TABLE `lms_test_in_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_test_in_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_test_question`
--

DROP TABLE IF EXISTS `lms_test_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_test_question` (
  `id` varchar(36) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `correctAnswers` varchar(255) DEFAULT NULL,
  `options` text,
  `type` varchar(255) DEFAULT NULL,
  `testId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqe1bgm8ofxw46i7fmxb394id3` (`testId`),
  CONSTRAINT `FKqe1bgm8ofxw46i7fmxb394id3` FOREIGN KEY (`testId`) REFERENCES `lms_test_in_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_test_question`
--

LOCK TABLES `lms_test_question` WRITE;
/*!40000 ALTER TABLE `lms_test_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_test_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_test_student_answer`
--

DROP TABLE IF EXISTS `lms_test_student_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_test_student_answer` (
  `id` varchar(36) NOT NULL,
  `answer` varchar(255) DEFAULT NULL,
  `isCorrect` bit(1) NOT NULL,
  `questionId` varchar(36) DEFAULT NULL,
  `resultId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcwe30aocpytmtc2iyckgaopuu` (`questionId`),
  KEY `FKdp8nmri49i75g3lmgw1ejvhk6` (`resultId`),
  CONSTRAINT `FKcwe30aocpytmtc2iyckgaopuu` FOREIGN KEY (`questionId`) REFERENCES `lms_test_question` (`id`),
  CONSTRAINT `FKdp8nmri49i75g3lmgw1ejvhk6` FOREIGN KEY (`resultId`) REFERENCES `lms_test_student_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_test_student_answer`
--

LOCK TABLES `lms_test_student_answer` WRITE;
/*!40000 ALTER TABLE `lms_test_student_answer` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_test_student_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_test_student_result`
--

DROP TABLE IF EXISTS `lms_test_student_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_test_student_result` (
  `id` varchar(36) NOT NULL,
  `score` double NOT NULL,
  `submittedAt` datetime(6) DEFAULT NULL,
  `totalCorrect` int NOT NULL,
  `studentId` varchar(36) DEFAULT NULL,
  `testId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3g0x31ypwvsd3vuoln9keoa6v` (`studentId`),
  KEY `FKfnucktohl6xyhmt396xmr7243` (`testId`),
  CONSTRAINT `FK3g0x31ypwvsd3vuoln9keoa6v` FOREIGN KEY (`studentId`) REFERENCES `lms_student` (`id`),
  CONSTRAINT `FKfnucktohl6xyhmt396xmr7243` FOREIGN KEY (`testId`) REFERENCES `lms_test_in_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_test_student_result`
--

LOCK TABLES `lms_test_student_result` WRITE;
/*!40000 ALTER TABLE `lms_test_student_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_test_student_result` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-25 12:35:19
