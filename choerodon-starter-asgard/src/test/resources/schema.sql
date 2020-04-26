DROP TABLE IF EXISTS `saga_task_instance_record`;

CREATE TABLE `saga_task_instance_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `create_time` BIGINT(20) NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;