/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.7.21-log : Database - serverc
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`serverc` /*!40100 DEFAULT CHARACTER SET utf8 */;

/*Table structure for table `ciphertext` */

DROP TABLE IF EXISTS `ciphertext`;

CREATE TABLE `ciphertext` (
  `sequence` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` bigint(20) unsigned NOT NULL,
  `ciphersequence` bigint(20) NOT NULL,
  `username` varchar(10) NOT NULL,
  `cipher_a` varbinary(4096) NOT NULL,
  `cipher_b` varbinary(4096) NOT NULL,
  `cipher_a_PK` varbinary(4096) DEFAULT NULL,
  `cipher_b_PK` varbinary(4096) DEFAULT NULL,
  PRIMARY KEY (`sequence`),
  KEY `name` (`username`),
  KEY `num` (`number`),
  CONSTRAINT `name` FOREIGN KEY (`username`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `num` FOREIGN KEY (`number`) REFERENCES `invitation` (`number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `ciphertext` */

/*Table structure for table `client` */

DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `username` varchar(10) NOT NULL,
  `hashed2saltpwd` char(64) NOT NULL,
  `salt` int(11) NOT NULL,
  `N` varbinary(2048) DEFAULT NULL,
  `h` varbinary(4096) DEFAULT NULL,
  `PK` varbinary(4096) DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `PP_N` (`N`),
  CONSTRAINT `PP_N` FOREIGN KEY (`N`) REFERENCES `pp` (`N`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `client` */

/*Table structure for table `invitation` */

DROP TABLE IF EXISTS `invitation`;

CREATE TABLE `invitation` (
  `number` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `inviter` varchar(10) NOT NULL,
  `length` bigint(20) unsigned NOT NULL COMMENT '碱基对长度',
  `invitees` varchar(1200) DEFAULT NULL,
  `submissiontime` varchar(40) NOT NULL COMMENT '客户端请求提交，被服务器接收后的时间',
  `puttime` varchar(40) DEFAULT NULL COMMENT 'put进队列的时间，包括rand的手动put和spec的自动put，put代表已经计算完毕PK，但还没有进行密文转换',
  `taketime` varchar(40) DEFAULT NULL COMMENT '把计算任务从阻塞队列中take出来的时间，即正式开始计算的时间',
  `keyprodtime` varchar(40) DEFAULT NULL COMMENT 'keyprod完成的时间，此时密文由各自公钥加密转换为由公共PK加密',
  `resultonpktime` varchar(40) DEFAULT NULL COMMENT '通过add与mult完成计算的时间，此时结果为PK加密',
  `finishedtime` varchar(40) DEFAULT NULL COMMENT '将用PK加密的结果转换为用各自公钥加密后的时间',
  PRIMARY KEY (`number`),
  KEY `invitername` (`inviter`),
  CONSTRAINT `invitername` FOREIGN KEY (`inviter`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `invitation` */

/*Table structure for table `invitee` */

DROP TABLE IF EXISTS `invitee`;

CREATE TABLE `invitee` (
  `sequence` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` bigint(20) unsigned NOT NULL COMMENT '对应invitation中的number',
  `inviter` varchar(10) NOT NULL,
  `inviterlen` bigint(20) unsigned NOT NULL COMMENT 'inviter的len',
  `invitee` varchar(10) NOT NULL,
  `inviteelen` bigint(20) unsigned DEFAULT NULL COMMENT 'invitee的len',
  PRIMARY KEY (`sequence`),
  KEY `number` (`number`),
  KEY `inviter` (`inviter`),
  KEY `invitee` (`invitee`),
  CONSTRAINT `invitee` FOREIGN KEY (`invitee`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `inviter` FOREIGN KEY (`inviter`) REFERENCES `invitation` (`inviter`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `number` FOREIGN KEY (`number`) REFERENCES `invitation` (`number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `invitee` */

/*Table structure for table `pp` */

DROP TABLE IF EXISTS `pp`;

CREATE TABLE `pp` (
  `N` varbinary(2048) NOT NULL,
  `k` varbinary(2048) NOT NULL,
  `g` varbinary(4096) NOT NULL,
  PRIMARY KEY (`N`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pp` */

/*Table structure for table `resultonh` */

DROP TABLE IF EXISTS `resultonh`;

CREATE TABLE `resultonh` (
  `sequence` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` bigint(20) unsigned NOT NULL,
  `usernamea` varchar(10) NOT NULL,
  `usernameb` varchar(10) NOT NULL,
  `adda` varbinary(4096) NOT NULL,
  `addb` varbinary(4096) NOT NULL,
  `multa` varbinary(4096) NOT NULL,
  `multb` varbinary(4096) NOT NULL,
  PRIMARY KEY (`sequence`),
  KEY `ueara` (`usernamea`),
  KEY `userb` (`usernameb`),
  KEY `numh` (`number`),
  CONSTRAINT `numh` FOREIGN KEY (`number`) REFERENCES `invitation` (`number`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ueara` FOREIGN KEY (`usernamea`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userb` FOREIGN KEY (`usernameb`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `resultonh` */

/*Table structure for table `resultonpk` */

DROP TABLE IF EXISTS `resultonpk`;

CREATE TABLE `resultonpk` (
  `sequence` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` bigint(20) unsigned NOT NULL,
  `usernamea` varchar(10) NOT NULL,
  `usernameb` varchar(10) NOT NULL,
  `adda` varbinary(4096) NOT NULL,
  `addb` varbinary(4096) NOT NULL,
  `multa` varbinary(4096) NOT NULL,
  `multb` varbinary(4096) NOT NULL,
  `PK` varbinary(4096) NOT NULL,
  PRIMARY KEY (`sequence`),
  KEY `usernamea` (`usernamea`),
  KEY `usernameb` (`usernameb`),
  KEY `numpk` (`number`),
  CONSTRAINT `numpk` FOREIGN KEY (`number`) REFERENCES `invitation` (`number`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usernamea` FOREIGN KEY (`usernamea`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usernameb` FOREIGN KEY (`usernameb`) REFERENCES `client` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `resultonpk` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
