-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for mydb
DROP DATABASE IF EXISTS `mydb`;
CREATE DATABASE IF NOT EXISTS `mydb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `mydb`;

-- Dumping structure for table mydb.table1
DROP TABLE IF EXISTS `table1`;
CREATE TABLE IF NOT EXISTS `table1` (
    `mo_name` varchar(15) NOT NULL,
    `mo_content` text,
    `mo_tags` text,
    PRIMARY KEY (`mo_name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table mydb.table1: ~7 rows (approximately)
DELETE FROM `table1`;
INSERT INTO `table1` (`mo_name`, `mo_content`, `mo_tags`) VALUES
                                                              ('mo_bstc', 'A registration was started online to set up an APE account.The account was activated. The seller listed {#tag_prodcat} priced {#tag_abatbe}  their current market value A review of the account showed that the seller also controlled {#tag_singmul},  . {#tag_singmultt}  used to place orders with the seller account.{#tag_ishnr}  {#tag_isactiveall} {#tag_isactivesm} {#tag_isblocked}  ', 'tag_prodcat,tag_abatbe,tag_singmul,tag_singmultt,tag_ishnr,tag_isactiveall,tag_isactivesm,tag_isblocked'),
                                                              ('mo1', '{#tag1 this a tag} original mo content  {#tag2 this is another tag} Some more content', 'tag1,tag2'),
                                                              ('mo2', 'original mo content {#tag3 this a tag}  Some more content', 'tag3'),
                                                              ('mo2ss', 'original mo content {#tag3 this a tag}  Some more content', 'tag3'),
                                                              ('mo3', 'hi there {#tag_prodcat originalcontentss}', 'tag_prodcat'),
                                                              ('mo4', 'A registration was started online to set up an APE account.\r\n\r\nThe account was activated. The seller listed {#tag_prodcat ss} priced {#tag_abatbe ss}  their current market value.\r\n\r\nA review of the account showed that the seller also controlled {#tag_singmul ss},  {#tag_singmultt ss}  used to place orders with the seller account.\r\n\r\n{#tag_ishnr ss}  .\r\n\r\n{#tag_isactiveall ss} {#tag_isactivesm ss} {#tag_isblocked ss}  .', 'tag_prodcat,tag_abatbe,tag_singmul,tag_singmultt,tag_ishnr,tag_isactiveall,tag_isactivesm,tag_isblocked'),
                                                              ('mo5', 'original mo content {#tag_mytag this a tag /this,that/ notthis}  Some more content', 'tag_mytag');

-- Dumping structure for table mydb.table2
DROP TABLE IF EXISTS `table2`;
CREATE TABLE IF NOT EXISTS `table2` (
    `mo_tags` varchar(15) NOT NULL,
    `tag_comment` text,
    PRIMARY KEY (`mo_tags`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table mydb.table2: ~12 rows (approximately)
DELETE FROM `table2`;
INSERT INTO `table2` (`mo_tags`, `tag_comment`) VALUES
                                                    ('tag_abatbe', 'Pricing Above / at / below'),
                                                    ('tag_isactiveall', 'The APE account is active on the Amazon EU online stores'),
                                                    ('tag_isactivesm', 'The APE account is active on the Amazon <marketplace names> online stores'),
                                                    ('tag_isblocked', 'The APE account is currently blocked on the <Marketplace name> ONLINE STORES ONLY online store(s). '),
                                                    ('tag_ishnr', 'Additionally, these items did not appear to have been sent to the related buyer [#account./accounts.#] '),
                                                    ('tag_mytag', 'i am tag_mytag'),
                                                    ('tag_prodcat', 'Type the Product Categries'),
                                                    ('tag_singmul', '[#multiple buyer accounts/a buyer account#]'),
                                                    ('tag_singmultt', '[#These buyer accounts were/This buyer account was#] '),
                                                    ('tag1', 'I am tag 1'),
                                                    ('tag2', 'I am tag 2'),
                                                    ('tag3', 'I am tag3 ');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
