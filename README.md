#龙王统计工具

目前是初始版本，仅支持自定义的一个群

需要 MySQL / MariaDB 数据库支持

需要的数据库名为：speakstats，以及两张表

````
CREATE TABLE `historydragon` (
	`date` TEXT NULL DEFAULT NULL,
	`dragon` BIGINT(20) UNSIGNED NULL DEFAULT NULL
)
COMMENT='show history dragons'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
````

````
CREATE TABLE `todaystats` (
	`QQ` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
	`Bubbles` INT(10) UNSIGNED NOT NULL
)
COMMENT='Today\'s Dragon Statistics.'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
````

更多描述待补充