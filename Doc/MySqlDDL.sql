/**001 PLAT_USER（用户）*/
DROP TABLE IF EXISTS plat_user;
CREATE TABLE plat_user (
  id         varchar(36) NOT NULL       COMMENT 'uuid（用户id）',
  loginName  varchar(15) NOT NULL       COMMENT '登录账号',
  userName   varchar(100) NOT NULL      COMMENT '登录账号',
  password   varchar(30) DEFAULT NULL,
  mailAdress varchar(100) NOT NULL      COMMENT '邮箱(非空为一索引)',
  nickName   varchar(100) DEFAULT NULL  COMMENT '昵称：可空',
  userType   int(1) unsigned NOT NULL   COMMENT '用户分类：1自然人用户，2机构用户',
  descn      varchar(2000) DEFAULT NULL COMMENT '备注',
  cTime      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间:创建时的系统时间',
  lmTime     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改：每次更新的时间',
  PRIMARY KEY (id),
  UNIQUE KEY loginName (loginName) USING BTREE,
  UNIQUE KEY mailAdress (mailAdress) USING BTREE
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='用户表';

/**002 元数据模式[SA_MD_TABMODEL]*/
DROP TABLE IF EXISTS sa_md_tabmodel;
CREATE TABLE sa_md_tabmodel (
  id        varchar(36) NOT NULL     COMMENT 'uuid（模式ID）',
  ownerId   varchar(36) NOT NULL     COMMENT '指向用户表(用户id或sessionId)',
  ownerType int(1) unsigned NOT NULL COMMENT '用户类型(1-用户，2-session)',
  tableName varchar(40) NOT NULL     COMMENT '业务 表中的积累表的名称',
  descn     varchar(400) NOT NULL    COMMENT '备注',
  cTime     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='元数据模式表';

/**003 元数据列描述[SA_MD_COLUMN]*/
DROP TABLE IF EXISTS sa_md_column;
CREATE TABLE sa_md_column (
  id          varchar(36) NOT NULL      COMMENT 'ID主键,可支持UUID',
  tmId        varchar(36) NOT NULL      COMMENT '表模式id(外键)',
  columnName  varchar(10) NOT NULL      COMMENT '此名称作为表中列名',
  titleName   varchar(100) DEFAULT NULL COMMENT '列意义名称(中文名称，若为excel，则为表头名称，若为DB会比较复杂)',
  columnType  varchar(10) NOT NULL DEFAULT 'String',
  columnIndex int(3) unsigned NOT NULL DEFAULT '0',
  isPk        int(1) unsigned NOT NULL DEFAULT '2' COMMENT '是否是主键(1-主键，2-不是主键)',
  cTime       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='元数据列描述';

/**004元数据列语义[SA_MD_COLSEMANTEME]*/
DROP TABLE IF EXISTS sa_md_colsemanteme;
CREATE TABLE  sa_md_colsemanteme (
  id                varchar(36) NOT NULL      COMMENT 'uuid',
  cId               varchar(36) NOT NULL      COMMENT '列描述Id(外键)',
  tmId              varchar(36) NOT NULL      COMMENT '元数据模式Id(模式id，冗余外键)',
  semantemeCode     int(1) unsigned NOT NULL  COMMENT '语义代码(某列在确定语义类型后，对该类型的数据的具体描述，目前只对字典项有意义)',
  semantemeType     int(1) unsigned NOT NULL  COMMENT '语义类型(1-身份证，2-字典项。。。。)',
  cTime timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '每次更新时修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='元数据语义表';

/**005 元数据表对照[SA_MD_TABMAP_ORG]*/
DROP TABLE IF EXISTS sa_md_tabmap_org;
CREATE TABLE  sa_md_tabmap_org (
  id                varchar(36) NOT NULL        COMMENT 'uuid(对照表id)',
  ownerId           varchar(36) NOT NULL        COMMENT '用户Id或者sessionId',
  tmId              varchar(36) NOT NULL        COMMENT '元数据模式Id(表模式ID)',
  tableName         varchar(40) NOT NULL        COMMENT '表名称（此名称与业务数据表名称对应）',
  tableType         int(1) unsigned NOT NULL DEFAULT '1' COMMENT '表类型(1-积累表。2-临时表)',
  tableDescn        varchar(400) COMMENT '备注',
  cTime             timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='元数据对照关系表';

/**006 元数据表指标[SA_MD_TABLEQUOTA]*/
DROP TABLE IF EXISTS sa_md_tablequota;
CREATE TABLE  sa_md_tablequota (
  id            varchar(36) NOT NULL         COMMENT '标指标Id(UUID)',
  tmoId         varchar(36) NOT NULL         COMMENT '对照表Id(元数据对照表Id，外键)',
  tmId          varchar(36) NOT NULL         COMMENT '元数据模式Id(模式Id，冗余外键)',
  tableName     varchar(40) NOT NULL         COMMENT '表名称(表名称，冗余外键)',
  allCount      int(10) unsigned NOT NULL DEFAULT '0' COMMENT '表行数(表中记录行数)',
  cTime         timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP COMMENT '创建时间',
  lmTime        timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  laTime        timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP COMMENT '最后访问修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='元数据表指标';

/**007 元数据列指标[SA_MD_COLUMNQUOTA]*/
DROP TABLE IF EXISTS sa_md_columnquota;
CREATE TABLE  sa_md_columnquota (
  id            varchar(36) NOT NULL          COMMENT '列指标Id(UUID)',
  cId           varchar(36) NOT NULL          COMMENT '列描述Id(列描述Id外键）',
  tqId          varchar(36) NOT NULL          COMMENT '表指标Id(实体表指标Id外键)',
  max           varchar(4000) NOT NULL        COMMENT '无论是什么类型，都要转成字符串',
  min           varchar(4000) NOT NULL        COMMENT '无论是什么类型，都要转成字符串',
  nullCount     int(10) unsigned NOT NULL DEFAULT '0' COMMENT '空值数(表中本列的空值数)',
  distinctCount int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单值数(表中本列消重后的个数)',
  cTime         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  lmTime        timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='元数据列指标表';

/**008 数据导入日志[SA_IMP_LOG]*/
DROP TABLE IF EXISTS sa_imp_log;
CREATE TABLE  sa_imp_log (
  id            varchar(36) NOT NULL          COMMENT '日志id(UUID)',
  ownerId       varchar(36) NOT NULL          COMMENT '用户Id或SessionID(或指向用户表)',
  sFileName     varchar(500) NOT NULL         COMMENT '服务端文件名(包含文件路径',
  fileSize      int(10) unsigned NOT NULL     COMMENT '文件大小',
  cFileName     varchar(500) DEFAULT NULL     COMMENT '客户端文件名(包含文件路径)',
  cTime         timestamp NOT NULL DEFAULT    CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间(也可以作为上传时间)',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='数据导入日志';

/**009 数据文件/实体表对应[SA_IMP_TABLOG_ORG]*/
DROP TABLE IF EXISTS sa_imp_tablog_org;
CREATE TABLE  sa_imp_tablog_org (
  id            varchar(36) NOT NULL         COMMENT '文件/实体对应关系ID(UUID)',
  ufId          varchar(36) NOT NULL         COMMENT '文件日志ID(文件表外键)',
  tmoId         varchar(36) NOT NULL         COMMENT '对照表Id(元数据实体表对照Id，外键)',
  tmId          varchar(36) NOT NULL         COMMENT '元数据模式Id(表模式Id外键)',
  sheetName     varchar(100) NOT NULL        COMMENT '页签名称',
  sheetIndex    int(3) unsigned NOT NULL     COMMENT '页签排序',
  cTime         timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='数据文件/实体表对应关系表';