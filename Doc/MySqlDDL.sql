/**001 PLAT_USER（用户）*/
DROP TABLE IF EXISTS plat_user;
CREATE TABLE plat_user (
  id                varchar(36)      NOT NULL                COMMENT 'uuid（用户id）',
  loginName         varchar(15)      NOT NULL                COMMENT '登录账号',
  userName          varchar(100)               DEFAULT NULL  COMMENT '登录账号',
  password          varchar(30)                DEFAULT NULL,
  mailAdress        varchar(100)     NOT NULL                COMMENT '邮箱(非空为一索引)',
  nickName          varchar(100)               DEFAULT NULL  COMMENT '昵称：可空',
  userType          int(1) unsigned  NOT NULL                COMMENT '用户分类：1自然人用户，2机构用户',
  descn             varchar(2000)              DEFAULT NULL  COMMENT '备注',
  userState         int(1)           NOT NULL  DEFAULT '0'   COMMENT '用户状态，0-2,0代表未激活的用户，1代表已激活邮箱的活跃用户，2代表已激活的非活跃用户',
  validataSequence  varchar(36)                DEFAULT NULL  COMMENT '验证信息，用于存储验证邮箱时的验证码，uuid',
  cTime             timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间:创建时的系统时间',
  lmTime            timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改：每次更新的时间',
  PRIMARY KEY (id),
  UNIQUE KEY loginName (loginName) USING BTREE,
  UNIQUE KEY mailAdress (mailAdress) USING BTREE
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

/**002 字典组[PLAT_DICTM]*/
DROP TABLE IF EXISTS plat_dictm;
CREATE TABLE plat_dictm (
  id         varchar(36)      NOT NULL             COMMENT '字典组表ID(UUID)',
  ownerId    varchar(36)      NOT NULL             COMMENT '所有者Id',
  ownerType  int(1) unsigned  NOT NULL  DEFAULT 1  COMMENT '所有者类型(1-用户,2-session)',
  dmName     varchar(200)     NOT NULL             COMMENT '字典组名称',
  nPy        varchar(800)                          COMMENT '名称拼音',
  sort       int(5) unsigned  NOT NULL  DEFAULT 0  COMMENT '字典组排序',
  isValidate int(1) unsigned  NOT NULL  DEFAULT 1  COMMENT '是否生效(1-生效,2-无效)',
  mType      int(1) unsigned  NOT NULL  DEFAULT 3  COMMENT '字典类型(1-系统保留,2-系统,3-自定义)',
  mRef       varchar(4000)                         COMMENT '创建时间',
  descn      varchar(500)                          COMMENT '说明',
  cTime      timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime     timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典组';

/**003 字典项[PLAT_DICTD]*/
DROP TABLE IF EXISTS plat_dictd;
CREATE TABLE plat_dictd (
  id         varchar(36)      NOT NULL             COMMENT '字典项表ID(UUID)',
  mId        varchar(36)      NOT NULL             COMMENT '字典组外键(UUID)',
  pId        varchar(36)      NOT NULL             COMMENT '父结点ID(UUID)',
  sort       int(5) unsigned  NOT NULL  DEFAULT 0  COMMENT '字典项排序,只在本级排序有意义',
  isValidate int(1) unsigned  NOT NULL  DEFAULT 1  COMMENT '是否生效(1-生效,2-无效)',
  ddName     varchar(200)     NOT NULL             COMMENT '字典项名称',
  nPy        varchar(800)                          COMMENT '名称拼音',
  aliasName  varchar(200)                          COMMENT '字典项别名',
  anPy       varchar(800)                          COMMENT '别名拼音',
  bCode      varchar(50)      NOT NULL             COMMENT '业务编码',
  dType      int(1) unsigned  NOT NULL  DEFAULT 3  COMMENT '字典类型(1-系统保留,2-系统,3-自定义,4引用-其他字典项ID；)',
  dRef       varchar(4000)                         COMMENT '创建时间',
  descn      varchar(500)                          COMMENT '说明',
  cTime      timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime     timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='字典项';

/**004 元数据模式[SA_MD_TABMODEL]*/
DROP TABLE IF EXISTS sa_md_tabmodel;
CREATE TABLE sa_md_tabmodel (
  id        varchar(36)      NOT NULL                COMMENT 'uuid（模式ID）',
  ownerId   varchar(36)      NOT NULL                COMMENT '指向用户表(用户id或sessionId)',
  ownerType int(1) unsigned  NOT NULL                COMMENT '用户类型(1-用户，2-session)',
  tableName varchar(40)      NOT NULL                COMMENT '业务 表中的积累表的名称',
  descn     varchar(400)               DEFAULT NULL  COMMENT '备注',
  cTime     timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据模式表';

/**005 元数据列描述[SA_MD_COLUMN]*/
DROP TABLE IF EXISTS sa_md_column;
CREATE TABLE sa_md_column (
  id          varchar(36)      NOT NULL                    COMMENT 'ID主键,可支持UUID',
  tmId        varchar(36)      NOT NULL                    COMMENT '表模式id(外键)',
  columnName  varchar(10)      NOT NULL                    COMMENT '此名称作为表中列名',
  titleName   varchar(100)     NOT NULL                    COMMENT '列意义名称(中文名称，若为excel，则为表头名称，若为DB会比较复杂)',
  columnType  varchar(10)      NOT NULL  DEFAULT 'String'  COMMENT '列数据类型',
  columnIndex int(3) unsigned  NOT NULL  DEFAULT '1'       COMMENT '列排序',
  pkSign      int(1) unsigned  NOT NULL  DEFAULT '0'       COMMENT '是否是主键(0-不是主键,1-确定主键,2-不确定主键)',
  cTime       timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据列描述';

/**006元数据列语义[SA_MD_COLSEMANTEME]*/
DROP TABLE IF EXISTS sa_md_colsemanteme;
CREATE TABLE sa_md_colsemanteme (
  id            varchar(36)      NOT NULL  COMMENT 'uuid',
  cId           varchar(36)      NOT NULL  COMMENT '列描述Id(外键)',
  tmId          varchar(36)      NOT NULL  COMMENT '元数据模式Id(模式id，冗余外键)',
  semantemeCode varchar(36)      NOT NULL  COMMENT '语义代码(某列在确定语义类型后，对该类型的数据的具体描述，目前只对字典项有意义)',
  semantemeType int(1) unsigned  NOT NULL  COMMENT '语义类型(1-字典项，2-身份证，3-姓名等等。。。。)',
  cTime         timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime        timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '每次更新时修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据语义表';

/**007 元数据表对照[SA_MD_TABMAP_ORG]*/
DROP TABLE IF EXISTS sa_md_tabmap_org;
CREATE TABLE sa_md_tabmap_org (
  id         varchar(36)      NOT NULL                COMMENT 'uuid(对照表id)',
  ownerId    varchar(36)      NOT NULL                COMMENT '用户Id或者sessionId',
  tmId       varchar(36)      NOT NULL                COMMENT '元数据模式Id(表模式ID)',
  tableName  varchar(40)      NOT NULL                COMMENT '表名称（此名称与业务数据表名称对应）',
  tableType  int(1) unsigned  NOT NULL  DEFAULT '1'   COMMENT '表类型(1-积累表。2-临时表)',
  tableDescn varchar(400)               DEFAULT NULL  COMMENT '备注',
  cTime      timestamp  NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '创建时的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据对照关系表';

/**008 元数据表指标[SA_MD_TABLEQUOTA]*/
DROP TABLE IF EXISTS sa_md_tabquota;
CREATE TABLE sa_md_tabquota (
  id        varchar(36)       NOT NULL               COMMENT '标指标Id(UUID)',
  tmoId     varchar(36)       NOT NULL               COMMENT '对照表Id(元数据对照表Id，外键)',
  tmId      varchar(36)       NOT NULL               COMMENT '元数据模式Id(模式Id，冗余外键)',
  tableName varchar(40)       NOT NULL               COMMENT '表名称(表名称，冗余外键)',
  allCount  int(10) unsigned  NOT NULL  DEFAULT '0'  COMMENT '表行数(表中记录行数)',
  cTime     timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime    timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改时间',
  laTime    timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '最后访问修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据表指标';

/**009 元数据列指标[SA_MD_COLUMNQUOTA]*/
DROP TABLE IF EXISTS sa_md_colquota;
CREATE TABLE sa_md_colquota (
  id            varchar(36)       NOT NULL               COMMENT '列指标Id(UUID)',
  cId           varchar(36)       NOT NULL               COMMENT '列描述Id(列描述Id外键）',
  tqId          varchar(36)       NOT NULL               COMMENT '表指标Id(实体表指标Id外键)',
  max           varchar(4000)     NOT NULL               COMMENT '无论是什么类型，都要转成字符串',
  min           varchar(4000)     NOT NULL               COMMENT '无论是什么类型，都要转成字符串',
  nullCount     int(10) unsigned  NOT NULL  DEFAULT '0'  COMMENT '空值数(表中本列的空值数)',
  distinctCount int(10) unsigned  NOT NULL  DEFAULT '0'  COMMENT '单值数(表中本列消重后的个数)',
  cTime         timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime        timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据列指标表';

/**010 数据导入日志[SA_IMP_LOG],这个废弃掉*/
DROP TABLE IF EXISTS sa_imp_log;
/**
CREATE TABLE sa_imp_log (
  id        varchar(36)       NOT NULL  COMMENT '日志id(UUID)',
  ownerId   varchar(36)       NOT NULL  COMMENT '用户Id或SessionID(或指向用户表)',
  ownerType int(1) unsigned   NOT NULL  COMMENT '用户类型(1-用户，2-session)',
  sFileName varchar(500)      NOT NULL  COMMENT '服务端文件名(包含文件路径',
  fileSize  int(10) unsigned  NOT NULL  COMMENT '文件大小',
  cFileName varchar(500)      NOT NULL  COMMENT '客户端文件名(包含文件路径)',
  cTime     timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '创建时间(也可以作为上传时间)',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据导入日志';
*//**废弃掉*/

/**011 数据文件/实体表对应[SA_IMP_TABLOG_ORG]*/
DROP TABLE IF EXISTS sa_imp_tablog_org;
CREATE TABLE sa_imp_tablog_org (
  id         varchar(36)      NOT NULL  COMMENT '文件/实体对应关系ID(UUID)',
  fId        varchar(36)      NOT NULL  COMMENT '文件日志ID(文件表外键)',
  tmoId      varchar(36)      NOT NULL  COMMENT '对照表Id(元数据实体表对照Id，外键)',
  tmId       varchar(36)      NOT NULL  COMMENT '元数据模式Id(表模式Id外键)',
  sheetName  varchar(100)     NOT NULL  COMMENT '页签名称',
  sheetIndex int(3) unsigned  NOT NULL  COMMENT '页签排序',
  cTime      timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据文件/实体表对应关系表';

/**012 文件记录索引[SA_FILE_INDEX]*/
DROP TABLE IF EXISTS sa_file_index;
CREATE TABLE sa_file_index (
  id          varchar(36)       NOT NULL  COMMENT '文件记录索引表ID(UUID)',
  ownerId     varchar(36)       NOT NULL  COMMENT '用户Id或SessionID(或指向用户表)，引起文件生成的用户，可以是系统sys',
  ownerType   int(1) unsigned   NOT NULL  COMMENT '用户类型(1-用户，2-session，3-系统)',
  accessType  int(1) unsigned   NOT NULL  COMMENT '文件访问类型，如ftp,操作系统文件等，目前只支持1=操作系统文件',
  filePath    varchar(500)      NOT NULL  COMMENT '文件地址，实际就是文件存储的路径，不包括文件名称',
  fileName    varchar(100)      NOT NULL  COMMENT '文件名称，包括扩展名，注意只有文件名称，没有路径',
  fileExtName varchar(30)       NOT NULL  COMMENT '文件扩展名称',
  fileSize    int(10) unsigned  NOT NULL  COMMENT '文件大小，字节数',
  descn       varchar(500)                COMMENT '说明',
  cTime       timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '文件创建时间',
  lmTime      timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '文件最后修改时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件记录索引';

/**013 文件分类[SA_FILE_CLASS]*/
DROP TABLE IF EXISTS sa_file_class;
CREATE TABLE sa_file_class (
  id      varchar(36)  NOT NULL  COMMENT '文件分类表ID(UUID)',
  fId     varchar(36)  NOT NULL  COMMENT '主文件id，指向文件表',
  type1   varchar(10)  NOT NULL  COMMENT '文件大类型：目前支持持三种,IMP、LOG和ANAL，即导入文件、日志和分析，日志是文本文件，分析是jsonD格式',
  type2   varchar(20)  NOT NULL  COMMENT '文件中类型：文件的二级类型,当时IMP时，可以是rdata（关系型数据）',
  type3   varchar(30)            COMMENT '文件子类型：文件的三级类型',
  extInfo varchar(200)           COMMENT '扩展信息，比如jsonD的一些说明',
  cTime   timestamp    NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '文件创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件分类(文件语义)';


/**014 文件关系[SA_FILE_ORG]*/
/**目前先不实现*/
DROP TABLE IF EXISTS sa_file_org;
CREATE TABLE sa_file_org (
  id     varchar(36)      NOT NULL  COMMENT '文件关系表ID(UUID)',
  aType  int(1) unsigned  NOT NULL  COMMENT '第一文件类型：=1是对原生态表的关联关系；=2是文件关联表',
  aId    varchar(36)      NOT NULL  COMMENT '第一文件类Id',
  bType  int(1) unsigned  NOT NULL  COMMENT '第二文件类型：=1是对原生态表的关联关系；=2是文件关联表',
  bId    varchar(36)      NOT NULL  COMMENT '第二文件类Id',
  rType1 int(1)           NOT NULL  COMMENT '关联类型1:=1单向-说明rfid是fid的子；=0平等；=-1反向-说明fid是rfid的子(这个通过视图实现)',
  rType2 varchar(200)               COMMENT '关联类型2',
  descn  varchar(500)               COMMENT '说明',
  cTime  timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件之间的关系';

/**015 视图[vSA_FILE_ANTIORG]*/
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_file_antiorg AS
  select id, bId AS aId, bType AS aType, aId AS bId, aType AS bType, 
  (0-rType1) AS rType1, rType2, descn, cTime
  from sa_file_org;

/**016 视图*/
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_imp_log AS
  select a.id, ownerId, ownerType, accessType, filePath, fileName, fileExtName, fileSize, b.extInfo AS cFileName, descn, a.cTime
  from sa_file_index a, sa_file_class b
  where a.id=b.fid and b.type1='IMP';
