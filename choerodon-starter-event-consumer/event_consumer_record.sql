DROP TABLE IF EXISTS `event_consumer_record`;

CREATE TABLE `event_consumer_record` (
  `uuid` varchar(50) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;