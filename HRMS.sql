CREATE DATABASE  IF NOT EXISTS `hrms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hrms`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: hrms
-- ------------------------------------------------------
-- Server version	8.0.35

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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aptitude_test`
--

LOCK TABLES `aptitude_test` WRITE;
/*!40000 ALTER TABLE `aptitude_test` DISABLE KEYS */;
INSERT INTO `aptitude_test` VALUES (1,1,85,'2023-10-19 22:00:00'),(2,2,90,'2023-10-20 22:00:00'),(3,3,78,'2023-10-21 22:00:00'),(4,4,82,'2023-10-22 22:00:00'),(5,5,95,'2023-10-23 22:00:00'),(6,6,88,'2023-10-24 22:00:00'),(7,7,75,'2023-10-25 22:00:00'),(8,8,80,'2023-10-26 22:00:00'),(9,9,67,'2023-10-27 22:00:00'),(10,10,92,'2023-10-31 22:00:00'),(11,11,77,'2023-10-29 22:00:00'),(12,12,85,'2023-10-30 22:00:00'),(13,13,93,'2023-10-31 22:00:00'),(14,14,89,'2023-11-01 22:00:00'),(15,15,76,'2023-11-02 22:00:00'),(16,16,88,'2023-11-03 22:00:00'),(17,17,91,'2023-11-04 22:00:00'),(18,18,83,'2023-11-05 22:00:00'),(19,19,94,'2023-11-06 22:00:00'),(20,20,72,'2023-11-30 22:00:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,1,'2023-11-01 06:00:00','2023-11-01 15:00:00','ABSENT'),(2,1,'2023-11-02 06:15:00','2023-11-02 15:00:00','PRESENT'),(3,2,'2023-11-03 07:00:00','2023-11-03 15:00:00','LATE'),(4,2,'2023-11-04 06:00:00','2023-11-04 15:00:00','PRESENT'),(5,3,'2023-11-05 06:30:00','2023-11-05 15:00:00','LATE'),(6,3,'2023-11-06 06:30:00','2023-11-06 15:00:00','PRESENT'),(7,4,'2023-11-07 06:15:00','2023-11-07 15:00:00','PRESENT'),(8,4,'2023-11-08 07:00:00','2023-11-08 15:00:00','LATE'),(9,5,'2023-11-09 06:00:00','2023-11-09 15:00:00','ABSENT'),(10,5,'2023-11-10 06:30:00','2023-11-10 15:00:00','PRESENT'),(11,6,'2023-11-11 06:00:00','2023-11-11 15:00:00','PRESENT'),(12,6,'2023-11-12 07:15:00','2023-11-12 15:00:00','LATE'),(13,7,'2023-11-13 06:00:00','2023-11-13 15:00:00','PRESENT'),(14,7,'2023-11-14 06:45:00','2023-11-14 15:00:00','ABSENT'),(15,8,'2023-11-15 06:00:00','2023-11-15 15:00:00','PRESENT'),(16,8,'2023-11-16 06:30:00','2023-11-16 15:00:00','ABSENT'),(17,9,'2023-11-17 06:00:00','2023-11-17 15:00:00','PRESENT'),(18,9,'2023-11-18 07:00:00','2023-11-18 15:00:00','LATE'),(19,10,'2023-11-19 06:30:00','2023-11-19 15:00:00','PRESENT'),(20,10,'2023-11-20 06:15:00','2023-11-20 15:00:00','PRESENT'),(21,1,'2023-11-02 06:15:00','2023-11-02 15:00:00','PRESENT');
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contractor`
--

LOCK TABLES `contractor` WRITE;
/*!40000 ALTER TABLE `contractor` DISABLE KEYS */;
INSERT INTO `contractor` VALUES (1,1,'EXTERNAL',1),(2,2,'ACTIVE',1),(3,5,'ACTIVE',2),(4,7,'ACTIVE',2),(5,10,'EXTERNAL',1),(6,11,'ON_LEAVE',1),(7,14,'ON_LEAVE',2),(8,16,'SUSPENDED',1),(9,18,'ACTIVE',2),(10,20,'SUSPENDED',2);
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
INSERT INTO `contractor_period` VALUES (1,'Contractor Period A','2023-01-01','2023-12-31'),(2,'Contractor Period B','2022-01-01','2022-12-31');
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hearings`
--

LOCK TABLES `hearings` WRITE;
/*!40000 ALTER TABLE `hearings` DISABLE KEYS */;
INSERT INTO `hearings` VALUES (1,1,'2023-11-01 08:00:00','SUSPENDED','Policy Violation'),(2,2,'2023-11-02 09:00:00','CLEARED','Attendance Issue'),(3,3,'2023-11-03 10:00:00','NULL','Pending Review'),(4,4,'2023-11-04 11:00:00','SUSPENDED','Misconduct'),(5,5,'2023-11-05 12:00:00','CLEARED','Appealed Successfully'),(6,6,'2023-11-06 08:30:00','NULL','Incomplete Documentation'),(7,7,'2023-11-07 09:30:00','SUSPENDED','Code of Conduct Violation'),(8,8,'2023-11-08 10:30:00','CLEARED','Technicality'),(9,9,'2023-11-09 11:30:00','NULL','Awaiting Decision'),(10,10,'2023-11-10 12:30:00','SUSPENDED','Breach of Contract'),(11,1,'2023-11-11 07:30:00','CLEARED','Resolved in Mediation'),(12,2,'2023-11-12 08:30:00','NULL','Documentation Issue'),(13,3,'2023-11-13 09:30:00','SUSPENDED','Repeated Lateness'),(14,4,'2023-11-14 10:30:00','CLEARED','Error on File'),(15,5,'2023-11-15 11:30:00','NULL','Pending Investigation'),(16,6,'2023-11-16 12:30:00','SUSPENDED','Policy Violation'),(17,7,'2023-11-17 07:00:00','CLEARED','Formal Warning'),(18,8,'2023-11-18 08:00:00','NULL','Review Needed'),(19,9,'2023-11-19 09:00:00','SUSPENDED','Attendance Violation'),(20,10,'2023-11-20 10:00:00','CLEARED','Accepted Appeal'),(21,1,'2023-11-21 11:00:00','NULL','Pending Review'),(22,2,'2023-11-22 12:00:00','SUSPENDED','Insubordination'),(23,3,'2023-11-23 13:00:00','CLEARED','Document Error'),(24,4,'2023-11-24 07:00:00','NULL','Awaiting Decision'),(25,5,'2023-11-25 08:00:00','SUSPENDED','Contract Breach'),(26,6,'2023-11-26 09:00:00','CLEARED','Cleared of Charges'),(27,7,'2023-11-27 10:00:00','NULL','Processing Appeal'),(28,8,'2023-11-28 11:00:00','SUSPENDED','Unauthorized Absence'),(29,9,'2023-11-29 12:00:00','CLEARED','Reinstated After Review'),(30,10,'2023-11-30 13:00:00','NULL','Under Investigation');
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
  `file_id` bigint NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `decision` enum('APPROVED','DENIED','PENDING') NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`leave_request_id`),
  KEY `contid_idx` (`contractor_id`),
  KEY `file_id_idx` (`file_id`),
  CONSTRAINT `contid` FOREIGN KEY (`contractor_id`) REFERENCES `contractor` (`contractor_id`),
  CONSTRAINT `file_id` FOREIGN KEY (`file_id`) REFERENCES `files` (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_request`
--

LOCK TABLES `leave_request` WRITE;
/*!40000 ALTER TABLE `leave_request` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'John','Doe','john.doe@example.com','Male','1234567890123','CONTRACTOR','Caucasian','New York',30,'password1'),(2,'Jane','Smith','jane.smith@example.com','Female','2345678901234','CONTRACTOR','Asian','Los Angeles',25,'password2'),(3,'Michael','Brown','michael.brown@example.com','Male','3456789012345','INSTRUCTOR','African American','Chicago',45,'password3'),(4,'Linda','Taylor','linda.taylor@example.com','Female','4567890123456','APPLICANT','Hispanic','Miami',28,'password4'),(5,'David','Wilson','david.wilson@example.com','Male','5678901234567','CONTRACTOR','Caucasian','Seattle',35,'password5'),(6,'Susan','Martinez','susan.martinez@example.com','Female','6789012345678','ADMIN','Asian','San Francisco',42,'password6'),(7,'Robert','Clark','robert.clark@example.com','Male','7890123456789','CONTRACTOR','African American','Houston',37,'password7'),(8,'Mary','Rodriguez','mary.rodriguez@example.com','Female','8901234567890','INSTRUCTOR','Hispanic','Phoenix',31,'password8'),(9,'James','Lewis','james.lewis@example.com','Male','9012345678901','APPLICANT','Caucasian','Denver',29,'password9'),(10,'Patricia','Walker','patricia.walker@example.com','Female','0123456789012','CONTRACTOR','Asian','Dallas',36,'password10'),(11,'Mark','Hall','mark.hall@example.com','Male','1123456789012','CONTRACTOR','Caucasian','Austin',34,'password11'),(12,'Barbara','Allen','barbara.allen@example.com','Female','2123456789012','ADMIN','Hispanic','Boston',41,'password12'),(13,'Thomas','Young','thomas.young@example.com','Male','3123456789012','INSTRUCTOR','African American','San Diego',46,'password13'),(14,'Nancy','Hernandez','nancy.hernandez@example.com','Female','4123456789012','CONTRACTOR','Caucasian','Las Vegas',33,'password14'),(15,'Steven','King','steven.king@example.com','Male','5123456789012','APPLICANT','Asian','Orlando',27,'password15'),(16,'Sarah','Scott','sarah.scott@example.com','Female','6123456789012','CONTRACTOR','African American','Atlanta',38,'password16'),(17,'Charles','Green','charles.green@example.com','Male','7123456789012','CONTRACTOR','Hispanic','Philadelphia',40,'password17'),(18,'Karen','Adams','karen.adams@example.com','Female','8123456789012','ADMIN','Caucasian','San Antonio',39,'password18'),(19,'Matthew','Baker','matthew.baker@example.com','Male','9123456789012','CONTRACTOR','Asian','Nashville',32,'password19'),(20,'Jessica','Carter','jessica.carter@example.com','Female','0123456789013','APPLICANT','Hispanic','Portland',26,'password20');
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
  `reason` enum('LATE','ABSENT','MISCONDUCT') NOT NULL,
  `state` enum('APPEALED','ACTIVE','REMOVED','FINAL') NOT NULL,
  PRIMARY KEY (`warning_id`),
  KEY `con_id_idx` (`contractor_id`),
  CONSTRAINT `con_id` FOREIGN KEY (`contractor_id`) REFERENCES `contractor` (`contractor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warning`
--

LOCK TABLES `warning` WRITE;
/*!40000 ALTER TABLE `warning` DISABLE KEYS */;
INSERT INTO `warning` VALUES (1,1,'2023-10-31 22:00:00','LATE','ACTIVE'),(2,1,'2023-11-01 22:00:00','LATE','APPEALED'),(3,2,'2023-11-02 22:00:00','LATE','FINAL'),(4,2,'2023-11-03 22:00:00','ABSENT','ACTIVE'),(5,3,'2023-11-04 22:00:00','LATE','REMOVED'),(6,3,'2023-11-05 22:00:00','LATE','FINAL'),(7,4,'2023-11-06 22:00:00','LATE','ACTIVE'),(8,4,'2023-11-07 22:00:00','LATE','APPEALED'),(9,5,'2023-11-08 22:00:00','ABSENT','FINAL'),(10,5,'2023-11-09 22:00:00','LATE','REMOVED'),(11,6,'2023-11-10 22:00:00','LATE','FINAL'),(12,6,'2023-11-11 22:00:00','ABSENT','ACTIVE'),(13,7,'2023-11-12 22:00:00','LATE','REMOVED'),(14,7,'2023-11-13 22:00:00','LATE','APPEALED'),(15,8,'2023-11-14 22:00:00','LATE','ACTIVE'),(16,8,'2023-11-15 22:00:00','LATE','FINAL'),(17,9,'2023-11-16 22:00:00','LATE','ACTIVE'),(18,9,'2023-11-17 22:00:00','ABSENT','APPEALED'),(19,10,'2023-11-18 22:00:00','LATE','FINAL'),(20,10,'2023-11-19 22:00:00','LATE','REMOVED');
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

-- Dump completed on 2024-11-28 15:00:10
