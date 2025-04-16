-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: lms_husc
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
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
  `id` char(36) NOT NULL,
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
INSERT INTO `lms_account` VALUES ('07b0010e-52ad-4fc9-8a63-ba54774842fb','admin','$2a$10$EF/bMsOID40ZrXUhP.OOxejGsT/G84bcvL7IYBUv4uEMTNakdlnL.',NULL,_binary '','admin','2025-04-04 07:15:51',NULL,NULL,NULL,NULL),('3b868566-675f-400f-a92b-4282c01cb132','letien@example.com','$2a$10$.VdbG3bRVvg77ZVc5Qgn9.JoUKe1/3KvFgpP0g0CE6WU9so27p2i.','letien@example.com',_binary '','admin','2025-04-04 07:47:30',NULL,NULL,NULL,NULL),('4a807615-aa8c-47c5-a5f5-7e196ab3dbef','letien@teacher.com','$2a$10$o/pV16M59IJs2KPc5LpUXO4PHZCdWZQLrOtFC0z1b5Hpt4T0PDfAu','letien@teacher.com',_binary '','admin','2025-04-04 08:05:36',NULL,NULL,NULL,NULL),('8d467125-1403-4b52-afad-000c3b8b4122','letienvan@example.com','$2a$10$Ko.Kc4PueEqkGHPgO2wGIuLlgTMpUXD/Q/9J0vpSRmKlOewlr01lm','letienvan@example.com',_binary '','admin','2025-04-04 07:48:51',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_account_roles`
--

DROP TABLE IF EXISTS `lms_account_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_account_roles` (
  `Account_id` char(36) DEFAULT NULL,
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
  `accountId` char(36) DEFAULT NULL,
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
  `id` char(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order` int DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `lessonId` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
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
/*!40000 ALTER TABLE `lms_chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_comment`
--

DROP TABLE IF EXISTS `lms_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_comment` (
  `id` char(36) NOT NULL,
  `accountId` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `chapterId` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `courseId` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `lessonMaterialId` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `detail` text,
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
/*!40000 ALTER TABLE `lms_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_course`
--

DROP TABLE IF EXISTS `lms_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_course` (
  `id` char(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `startDate` datetime(6) DEFAULT NULL,
  `endDate` datetime(6) DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `learningDurationType` varchar(255) DEFAULT NULL,
  `teacherId` char(36) DEFAULT NULL,
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
INSERT INTO `lms_course` VALUES ('76cf929a-ae12-42c6-b964-7358f3e21ed4','Lập trình Java cơ bản','Khóa học nhập môn lập trình Java cho người mới bắt đầu.','2025-04-10 00:00:00.000000','2025-07-10 00:00:00.000000','Công nghệ thông tin','PUBLIC','Có thời hạn','f16bd037-35aa-4fba-bcd7-e0538bf43c83',NULL,'letien@teacher.com','2025-04-04 09:14:22',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_course` ENABLE KEYS */;
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
/*!40000 ALTER TABLE `lms_invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_lesson`
--

DROP TABLE IF EXISTS `lms_lesson`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_lesson` (
  `id` char(36) NOT NULL,
  `courseId` char(36) DEFAULT NULL,
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
  `lessonId` char(36) DEFAULT NULL,
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
  `lessonId` char(36) DEFAULT NULL,
  `question` varchar(255) DEFAULT NULL,
  `option` longtext,
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
-- Table structure for table `lms_notification`
--

DROP TABLE IF EXISTS `lms_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_notification` (
  `id` char(36) NOT NULL,
  `senderAccountId` char(36) NOT NULL,
  `receiverAccountId` char(36) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `isRead` tinyint(1) DEFAULT '0',
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_sender_account` (`senderAccountId`),
  KEY `fk_receiver_account` (`receiverAccountId`),
  CONSTRAINT `fk_receiver_account` FOREIGN KEY (`receiverAccountId`) REFERENCES `lms_account` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sender_account` FOREIGN KEY (`senderAccountId`) REFERENCES `lms_account` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_notification`
--

LOCK TABLES `lms_notification` WRITE;
/*!40000 ALTER TABLE `lms_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `lms_notification` ENABLE KEYS */;
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
  `id` char(36) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `major` varchar(255) DEFAULT NULL,
  `accountId` char(36) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_LMS_STUDENT_ON_ACCOUNTID` (`accountId`),
  CONSTRAINT `FK_LMS_STUDENT_ON_ACCOUNTID` FOREIGN KEY (`accountId`) REFERENCES `lms_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lms_student`
--

LOCK TABLES `lms_student` WRITE;
/*!40000 ALTER TABLE `lms_student` DISABLE KEYS */;
INSERT INTO `lms_student` VALUES ('36a0d397-b9b7-4fbd-b162-a8fd7b58910f','Lê Văn Tiến','letienvan@example.com',NULL,'CNTT','8d467125-1403-4b52-afad-000c3b8b4122',NULL),('3d161011-dba5-4fa3-bc17-cd12a1662d97','Lê Văn Tiến','letien@example.com',NULL,'CNTT','3b868566-675f-400f-a92b-4282c01cb132',NULL);
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
  `studentId` char(36) DEFAULT NULL,
  `courseId` char(36) DEFAULT NULL,
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
INSERT INTO `lms_student_course` VALUES ('c52f35dd-f47e-4c06-b7c2-d5339a467f18','3d161011-dba5-4fa3-bc17-cd12a1662d97','76cf929a-ae12-42c6-b964-7358f3e21ed4','2025-04-06 12:12:54',NULL,NULL,'2025-04-06 12:12:54',NULL,NULL,NULL,NULL),('ef6c76ce-4c31-4ab0-8a1f-c8ad68899fa9','36a0d397-b9b7-4fbd-b162-a8fd7b58910f','76cf929a-ae12-42c6-b964-7358f3e21ed4','2025-04-06 12:12:54',NULL,NULL,'2025-04-06 12:12:54',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `lms_student_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_student_lesson_chapter_progress`
--

DROP TABLE IF EXISTS `lms_student_lesson_chapter_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_student_lesson_chapter_progress` (
  `id` char(36) NOT NULL,
  `studentId` char(36) DEFAULT NULL,
  `chapterId` char(36) DEFAULT NULL,
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
  `studentId` char(36) DEFAULT NULL,
  `lessonId` char(36) DEFAULT NULL,
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
/*!40000 ALTER TABLE `lms_student_lesson_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lms_teacher`
--

DROP TABLE IF EXISTS `lms_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lms_teacher` (
  `id` char(36) NOT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `userId` char(36) DEFAULT NULL,
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
-- Dumping events for database 'lms_husc'
--

--
-- Dumping routines for database 'lms_husc'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-12 13:33:59
