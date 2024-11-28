CREATE DATABASE  IF NOT EXISTS `hrms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hrms`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: hrms
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
-- Table structure for table `aptitude_test`
--

DROP TABLE IF EXISTS `aptitude_test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aptitude_test` (
  `aptitude_test_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `test_mark` int NOT NULL,
  `test_date` timestamp NOT NULL,
  PRIMARY KEY (`aptitude_test_id`),
  UNIQUE KEY `userid_UNIQUE` (`user_id`),
  UNIQUE KEY `aptitude_test_id_UNIQUE` (`aptitude_test_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aptitude_test`
--

LOCK TABLES `aptitude_test` WRITE;
/*!40000 ALTER TABLE `aptitude_test` DISABLE KEYS */;
INSERT INTO `aptitude_test` VALUES (3,1,0,'2024-12-12 10:00:00');
/*!40000 ALTER TABLE `aptitude_test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `attendance_id` bigint NOT NULL AUTO_INCREMENT,
  `contractor_id` bigint NOT NULL,
  `time_in` timestamp NOT NULL,
  `time_out` timestamp NULL DEFAULT NULL,
  `register` enum('PRESENT','ABSENT','LATE') NOT NULL,
  PRIMARY KEY (`attendance_id`),
  KEY `contract_id_idx` (`contractor_id`),
  CONSTRAINT `contract_id` FOREIGN KEY (`contractor_id`) REFERENCES `contractor` (`contractor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (74,5,'2024-11-26 14:47:24','2024-11-26 19:45:05','LATE'),(75,6,'2024-11-26 14:47:24',NULL,'ABSENT'),(76,5,'2024-11-25 14:47:24',NULL,'ABSENT'),(77,6,'2024-11-25 14:47:24',NULL,'PRESENT'),(78,5,'2024-11-27 06:36:56','2024-11-27 06:55:32','LATE'),(79,6,'2024-11-27 06:53:43','2024-11-27 11:52:50','LATE'),(80,7,'2024-11-27 12:28:26','2024-11-27 12:28:34','LATE'),(83,5,'2024-11-28 07:50:32',NULL,'LATE');
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract` (
  `contract_id` bigint NOT NULL AUTO_INCREMENT,
  `contract_period_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `offer_date` date NOT NULL,
  `decision_date` date DEFAULT NULL,
  `expiration_date` date NOT NULL,
  `decision` enum('PENDING','REJECTED','ACCEPTED') NOT NULL DEFAULT 'PENDING',
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`contract_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  KEY `idcontractperiod_idx` (`contract_period_id`),
  CONSTRAINT `id_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `idcontractperiod` FOREIGN KEY (`contract_period_id`) REFERENCES `contractor_period` (`contractor_period_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract`
--

LOCK TABLES `contract` WRITE;
/*!40000 ALTER TABLE `contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contractor`
--

DROP TABLE IF EXISTS `contractor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contractor` (
  `contractor_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `status` enum('EXTERNAL','SUSPENDED','ACTIVE','ON_LEAVE') DEFAULT NULL,
  `contractor_period_id` bigint NOT NULL,
  PRIMARY KEY (`contractor_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  KEY `contractor_period_id_idx` (`contractor_period_id`),
  CONSTRAINT `contractor_period_id` FOREIGN KEY (`contractor_period_id`) REFERENCES `contractor_period` (`contractor_period_id`),
  CONSTRAINT `iduser` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contractor`
--

LOCK TABLES `contractor` WRITE;
/*!40000 ALTER TABLE `contractor` DISABLE KEYS */;
INSERT INTO `contractor` VALUES (5,3,'ACTIVE',1),(6,2,'ACTIVE',1),(7,7,'ACTIVE',1);
/*!40000 ALTER TABLE `contractor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contractor_period`
--

DROP TABLE IF EXISTS `contractor_period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contractor_period` (
  `contractor_period_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  PRIMARY KEY (`contractor_period_id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contractor_period`
--

LOCK TABLES `contractor_period` WRITE;
/*!40000 ALTER TABLE `contractor_period` DISABLE KEYS */;
INSERT INTO `contractor_period` VALUES (1,'j148','2024-05-05','2024-12-12'),(2,'j149','2025-01-01','2025-12-31');
/*!40000 ALTER TABLE `contractor_period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `files`
--

DROP TABLE IF EXISTS `files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `files` (
  `file_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `file_type` varchar(45) NOT NULL,
  `category` enum('TIMESHEET','MATRIC_CERTIFICATE','ID','CONTRACT','LEAVE_FORM','PROJECT','OTHER') NOT NULL,
  `date_added` timestamp NOT NULL,
  `path` varchar(255) NOT NULL,
  `verified` enum('NOT_APPLICABLE','WAITING','REJECTED','APPROVED') NOT NULL DEFAULT 'NOT_APPLICABLE',
  PRIMARY KEY (`file_id`),
  UNIQUE KEY `path_UNIQUE` (`path`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `userid` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `files`
--

LOCK TABLES `files` WRITE;
/*!40000 ALTER TABLE `files` DISABLE KEYS */;
/*!40000 ALTER TABLE `files` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hearings`
--

DROP TABLE IF EXISTS `hearings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hearings` (
  `hearings_id` bigint NOT NULL AUTO_INCREMENT,
  `contractor_id` bigint NOT NULL,
  `schedule_date` timestamp NOT NULL,
  `outcome` enum('NULL','SUSPENDED','CLEARED') NOT NULL,
  `reason` text NOT NULL,
  PRIMARY KEY (`hearings_id`),
  KEY `conid_idx` (`contractor_id`),
  CONSTRAINT `conid` FOREIGN KEY (`contractor_id`) REFERENCES `contractor` (`contractor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hearings`
--

LOCK TABLES `hearings` WRITE;
/*!40000 ALTER TABLE `hearings` DISABLE KEYS */;
INSERT INTO `hearings` VALUES (2,5,'2024-12-03 14:47:31','NULL','Contractor has received three or more warnings for being late or absent'),(3,6,'2024-12-05 07:46:27','NULL','Contractor has received three or more warnings for being late or absent'),(4,5,'2024-12-05 07:50:32','NULL','Contractor has received three or more warnings for being late or absent');
/*!40000 ALTER TABLE `hearings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leave_request`
--

DROP TABLE IF EXISTS `leave_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_request` (
  `leave_request_id` bigint NOT NULL AUTO_INCREMENT,
  `contractor_id` bigint NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `decision` enum('APPROVED','DENIED','PENDING') NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`leave_request_id`),
  KEY `contid_idx` (`contractor_id`),
  CONSTRAINT `contid` FOREIGN KEY (`contractor_id`) REFERENCES `contractor` (`contractor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_request`
--

LOCK TABLES `leave_request` WRITE;
/*!40000 ALTER TABLE `leave_request` DISABLE KEYS */;
INSERT INTO `leave_request` VALUES (1,5,'2024-12-10','2025-01-01','PENDING'),(2,5,'2024-12-02','2024-12-06','PENDING');
/*!40000 ALTER TABLE `leave_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `id_number` varchar(13) NOT NULL,
  `role` enum('APPLICANT','CONTRACTOR','INSTRUCTOR','ADMIN') NOT NULL DEFAULT 'APPLICANT',
  `race` varchar(45) NOT NULL,
  `location` varchar(255) NOT NULL,
  `age` int NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `id_number_UNIQUE` (`id_number`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Glen','La Grange','glenlagrange22@gmail.com','male','980506','ADMIN','white','Johannesburg South',26,'1'),(2,'Mulalo','Molea','m@gmail.com','male','0101010','CONTRACTOR','black','JHB',24,'q'),(3,'Glen','La','g@g.com','male','0111151010100','CONTRACTOR','white','durban',23,'p'),(6,'glen','la grange','glenlagrange22@yahoo.com','male','9811155050000','ADMIN','white','mondeor',26,'wow'),(7,'Hangwe','Dep','HangweDep@gmail.com','Male','9805051112131','CONTRACTOR','Black','Limpopo',26,'qazwsxedc');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warning`
--

DROP TABLE IF EXISTS `warning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warning` (
  `warning_id` bigint NOT NULL AUTO_INCREMENT,
  `contractor_id` bigint NOT NULL,
  `date_issue` timestamp NOT NULL,
  `reason` enum('LATE','ABSENT') NOT NULL,
  `state` enum('APPEALED','ACTIVE','REMOVED','FINAL') NOT NULL,
  PRIMARY KEY (`warning_id`),
  KEY `con_id_idx` (`contractor_id`),
  CONSTRAINT `con_id` FOREIGN KEY (`contractor_id`) REFERENCES `contractor` (`contractor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warning`
--

LOCK TABLES `warning` WRITE;
/*!40000 ALTER TABLE `warning` DISABLE KEYS */;
INSERT INTO `warning` VALUES (79,5,'2024-10-10 07:00:00','LATE','ACTIVE'),(80,5,'2024-10-11 06:51:00','LATE','ACTIVE'),(86,6,'2024-11-26 11:52:27','LATE','ACTIVE'),(87,5,'2024-11-26 14:47:28','ABSENT','ACTIVE'),(88,5,'2024-11-27 06:36:59','LATE','ACTIVE'),(89,6,'2024-11-27 06:53:43','LATE','ACTIVE'),(90,7,'2024-11-27 12:28:27','LATE','ACTIVE'),(91,5,'2024-11-28 06:55:21','LATE','ACTIVE'),(92,6,'2024-11-28 07:46:25','LATE','ACTIVE'),(93,5,'2024-11-28 07:50:32','LATE','ACTIVE');
/*!40000 ALTER TABLE `warning` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-28 15:21:10
