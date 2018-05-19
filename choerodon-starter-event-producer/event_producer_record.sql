DROP TABLE IF EXISTS `event_producer_record`;

CREATE TABLE `event_producer_record` (
  `uuid` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;