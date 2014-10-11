DROP TABLE IF EXISTS `platform`.`sa_columninfo`;
CREATE TABLE  `platform`.`sa_columninfo` (
  `id` varchar(36) NOT NULL COMMENT 'ID主键,可支持UUID',
  `tableId` varchar(36) NOT NULL COMMENT '与tableName相关联，用于和数据表关联',
  `columnIndex` int(4) unsigned NOT NULL COMMENT 'title顺序',
  `columnName` varchar(10) NOT NULL COMMENT 'title名',
  `columnType` varchar(10) NOT NULL COMMENT 'title类型',
  `pk` varchar(2) NOT NULL COMMENT 'Y/N(是否主键)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='用于保存列信息';
DROP TABLE IF EXISTS `platform`.`sa_datasignorg`;
CREATE TABLE  `platform`.`sa_datasignorg` (
  `id` varchar(36) NOT NULL,
  `tableId` varchar(36) NOT NULL,
  `dataId` varchar(36) NOT NULL,
  `sign` varchar(40) NOT NULL COMMENT '标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='sign与table，data关系表';
DROP TABLE IF EXISTS `platform`.`sa_datauploadlog`;
CREATE TABLE  `platform`.`sa_datauploadlog` (
  `id` varchar(36) NOT NULL COMMENT 'ID主键,可支持UUID',
  `sourceFileName` varchar(500) NOT NULL COMMENT '文件路径',
  `fileSize` int(10) unsigned NOT NULL COMMENT '文件大小',
  `uploadDate` date NOT NULL COMMENT '上传时间',
  `uploadUser` varchar(36) NOT NULL COMMENT '上传人',
  `descn` varchar(2000) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='用于保存上传文件基本信息';
DROP TABLE IF EXISTS `platform`.`sa_logtableorg`;
CREATE TABLE  `platform`.`sa_logtableorg` (
  `id` varchar(36) NOT NULL COMMENT 'id',
  `tableId` varchar(36) NOT NULL COMMENT '对应表id',
  `logId` varchar(36) NOT NULL COMMENT '对应日志id',
  `sheetIndex` int(10) unsigned NOT NULL COMMENT '对应sheet的顺序',
  `sheetName` varchar(100) NOT NULL COMMENT '对应的sheet名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='datauploadlog与tablInfo中间表';
DROP TABLE IF EXISTS `platform`.`sa_statisticsinfo`;
CREATE TABLE  `platform`.`sa_statisticsinfo` (
  `id` varchar(36) NOT NULL,
  `columnName` varchar(50) NOT NULL COMMENT '列名',
  `max` varchar(45) NOT NULL COMMENT '最大值',
  `min` varchar(45) NOT NULL COMMENT '最小值',
  `columnType` varchar(10) NOT NULL COMMENT '列类型',
  `distinctCount` int(10) unsigned NOT NULL COMMENT 'distinct后个数',
  `nullCount` int(10) unsigned NOT NULL COMMENT 'null个数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='统计信息表';
DROP TABLE IF EXISTS `platform`.`sa_tableinfo`;
CREATE TABLE  `platform`.`sa_tableinfo` (
  `id` varchar(36) NOT NULL COMMENT 'tableid',
  `tableName` varchar(50) NOT NULL COMMENT 'table名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='数据表信息';
DROP TABLE IF EXISTS `platform`.`sa_tablestatisticsinfoorg`;
CREATE TABLE  `platform`.`sa_tablestatisticsinfoorg` (
  `id` varchar(36) NOT NULL,
  `tableId` varchar(36) NOT NULL,
  `rows` int(10) unsigned NOT NULL,
  `statisticsInfoId` varchar(36) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;
