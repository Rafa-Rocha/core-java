CREATE DATABASE  IF NOT EXISTS `arrowhead`;
USE `arrowhead`;

CREATE TABLE IF NOT EXISTS `event_filter` (
  `id` bigint(20) NOT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `event_type` varchar(255) NOT NULL,
  `match_metadata` char(1) DEFAULT NULL,
  `notify_uri` varchar(255) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `consumer_system_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbkos27fkducgbn6rxqty2k6n1` (`event_type`,`consumer_system_id`),
  KEY `FK8k1vieqrr0cxw4x0ubocsrrpo` (`consumer_system_id`),
  CONSTRAINT `FK8k1vieqrr0cxw4x0ubocsrrpo` FOREIGN KEY (`consumer_system_id`) REFERENCES `arrowhead_system` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `event_filter_metadata` (
  `filter_id` bigint(20) NOT NULL,
  `metadata_value` varchar(2047) DEFAULT NULL,
  `metadata_key` varchar(255) NOT NULL,
  PRIMARY KEY (`filter_id`,`metadata_key`),
  CONSTRAINT `FK1iu2vhxo8211io6weiwryguib` FOREIGN KEY (`filter_id`) REFERENCES `event_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `event_filter_sources_list` (
  `filter_id` bigint(20) NOT NULL,
  `sources_id` bigint(20) NOT NULL,
  PRIMARY KEY (`filter_id`,`sources_id`),
  UNIQUE KEY `UK_nbe4wrcv5w6rga8uc6t0cb0ck` (`sources_id`),
  CONSTRAINT `FK7gulo44n997tr1146xxi2xhfe` FOREIGN KEY (`sources_id`) REFERENCES `arrowhead_system` (`id`),
  CONSTRAINT `FKqihrii4ab12xo3oxp5d5pb77j` FOREIGN KEY (`filter_id`) REFERENCES `event_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
