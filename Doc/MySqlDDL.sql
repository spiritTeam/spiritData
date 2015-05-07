/**001 PLAT_USER（用户）*/
DROP TABLE IF EXISTS plat_user;
CREATE TABLE plat_user (
  id                varchar(32)      NOT NULL                COMMENT 'uuid（用户id）',
  loginName         varchar(15)      NOT NULL                COMMENT '登录账号',
  userName          varchar(100)               DEFAULT NULL  COMMENT '登录账号',
  password          varchar(30)                DEFAULT NULL,
  mailAdress        varchar(100)     NOT NULL                COMMENT '邮箱(非空为一索引)',
  nickName          varchar(100)               DEFAULT NULL  COMMENT '昵称：可空',
  userType          int(1) unsigned  NOT NULL                COMMENT '用户分类：1自然人用户，2机构用户',
  descn             varchar(2000)              DEFAULT NULL  COMMENT '备注',
  userState         int(1)           NOT NULL  DEFAULT '0'   COMMENT '用户状态，0-2,0代表未激活的用户，1代表已激用户，2代表失效用户,3根据邮箱找密码的用户',
  validataSequence  varchar(32)                DEFAULT NULL  COMMENT '验证信息，用于存储验证邮箱时的验证码，uuid',
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
  id         varchar(32)      NOT NULL             COMMENT '字典组表ID(UUID)',
  ownerId    varchar(32)      NOT NULL             COMMENT '所有者Id',
  ownerType  int(1) unsigned  NOT NULL  DEFAULT 1  COMMENT '所有者类型(1-用户,2-session)',
  dmName     varchar(200)     NOT NULL             COMMENT '字典组名称',
  nPy        varchar(800)                          COMMENT '名称拼音',
  sort       int(5) unsigned  NOT NULL  DEFAULT 0  COMMENT '字典组排序,从大到小排序，越大越靠前',
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
  id         varchar(32)      NOT NULL             COMMENT '字典项表ID(UUID)',
  mId        varchar(32)      NOT NULL             COMMENT '字典组外键(UUID)',
  pId        varchar(32)      NOT NULL             COMMENT '父结点ID(UUID)',
  sort       int(5) unsigned  NOT NULL  DEFAULT 0  COMMENT '字典项排序,只在本级排序有意义,从大到小排序，越大越靠前',
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
  id        varchar(32)      NOT NULL                COMMENT 'uuid（模式ID）',
  ownerId   varchar(32)      NOT NULL                COMMENT '指向用户表(用户id或sessionId)',
  ownerType int(1) unsigned  NOT NULL                COMMENT '用户类型(1-用户，2-session)',
  tableName varchar(40)      NOT NULL                COMMENT '业务 表中的积累表的名称',
  titleName varchar(100)                             COMMENT '表描述名称',
  descn     varchar(400)               DEFAULT NULL  COMMENT '备注',
  cTime     timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据模式表';

/**005 元数据列描述[SA_MD_COLUMN]*/
DROP TABLE IF EXISTS sa_md_column;
CREATE TABLE sa_md_column (
  id          varchar(32)      NOT NULL                    COMMENT 'ID主键,可支持UUID',
  tmId        varchar(32)      NOT NULL                    COMMENT '表模式id(外键)',
  columnName  varchar(10)      NOT NULL                    COMMENT '此名称作为表中列名',
  titleName   varchar(100)     NOT NULL                    COMMENT '列意义名称(中文名称，若为excel，则为表头名称，若为DB会比较复杂)',
  columnType  varchar(10)      NOT NULL  DEFAULT 'String'  COMMENT '列数据类型',
  columnIndex int(3) unsigned  NOT NULL  DEFAULT '0'       COMMENT '列排序',
  pkSign      int(1) unsigned  NOT NULL  DEFAULT '0'       COMMENT '是否是主键(0-不是主键,1-确定主键,2-不确定主键)',
  cTime       timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时的系统时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据列描述';

/**006元数据列语义[SA_MD_COLSEMANTEME]*/
DROP TABLE IF EXISTS sa_md_colsemanteme;
CREATE TABLE sa_md_colsemanteme (
  id            varchar(32)      NOT NULL  COMMENT 'uuid',
  cId           varchar(32)      NOT NULL  COMMENT '列描述Id(外键)',
  tmId          varchar(32)      NOT NULL  COMMENT '元数据模式Id(模式id，冗余外键)',
  semantemeCode varchar(32)      NOT NULL  COMMENT '语义代码(某列在确定语义类型后，对该类型的数据的具体描述，目前只对字典项有意义)',
  semantemeType int(4) unsigned  NOT NULL  COMMENT '语义类型(1-字典项，2-身份证，3-姓名等等。。。。)',
  cTime         timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime        timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '每次更新时修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据语义表';

/**007 元数据表对照[SA_MD_TABMAP_REL]*/
DROP TABLE IF EXISTS sa_md_tabmap_rel;
CREATE TABLE sa_md_tabmap_rel (
  id         varchar(32)      NOT NULL                COMMENT 'uuid(对照表id)',
  tmId       varchar(32)      NOT NULL                COMMENT '元数据模式Id(表模式ID)',
  ownerId    varchar(32)      NOT NULL                COMMENT '用户Id或者sessionId',
  ownerType  int(1) unsigned  NOT NULL                COMMENT '用户类型(1-用户，2-session)',
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
  id        varchar(32)       NOT NULL               COMMENT '标指标Id(UUID)',
  tmoId     varchar(32)       NOT NULL               COMMENT '对照表Id(元数据对照表Id，外键)',
  tmId      varchar(32)       NOT NULL               COMMENT '元数据模式Id(模式Id，冗余外键)',
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
  id            varchar(32)       NOT NULL               COMMENT '列指标Id(UUID)',
  cId           varchar(32)       NOT NULL               COMMENT '列描述Id(列描述Id外键）',
  tqId          varchar(32)       NOT NULL               COMMENT '表指标Id(实体表指标Id外键)',
  max           varchar(4000)     NOT NULL               COMMENT '无论是什么类型，都要转成字符串',
  min           varchar(4000)     NOT NULL               COMMENT '无论是什么类型，都要转成字符串',
  nullCount     int(10) unsigned  NOT NULL  DEFAULT '0'  COMMENT '空值数(表中本列的空值数)',
  distinctCount int(10) unsigned  NOT NULL  DEFAULT '0'  COMMENT '单值数(表中本列消重后的个数)',
  cTime         timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  lmTime        timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '最后修改',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='元数据列指标表';

/**010 数据文件/实体表对应[SA_IMP_TABMAP_REL]*/
DROP TABLE IF EXISTS sa_imp_tabmap_rel;
CREATE TABLE sa_imp_tabmap_rel (
  id              varchar(32)      NOT NULL  COMMENT '文件/实体对应关系ID(UUID)',
  fId             varchar(32)      NOT NULL  COMMENT '文件日志ID(文件表外键)',
  tmoId           varchar(32)      NOT NULL  COMMENT '对照表Id(元数据实体表对照Id，外键)',
  tmId            varchar(32)      NOT NULL  COMMENT '元数据模式Id(表模式Id外键)',
  sheetName       varchar(100)     NOT NULL  COMMENT '页签名称',
  sheetIndex      int(3) unsigned  NOT NULL  COMMENT '页签排序',
  tableTitleName  varchar(100)     NOT NULL  COMMENT '页签名称',
  cTime           timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据文件/实体表对应关系表';

/**011 文件记录索引[SA_FILE_INDEX]*/
DROP TABLE IF EXISTS sa_file_index;
CREATE TABLE sa_file_index (
  id          varchar(32)       NOT NULL  COMMENT '文件记录索引表ID(UUID)',
  ownerType   int(1) unsigned   NOT NULL  COMMENT '用户类型(1-用户，2-session，3-系统)',
  ownerId     varchar(32)       NOT NULL  COMMENT '用户Id或SessionID(或指向用户表)，引起文件生成的用户，可以是系统sys',
  accessType  int(1) unsigned   NOT NULL  COMMENT '文件访问类型，如ftp,操作系统文件等，目前只支持1=操作系统文件',
  filePath    varchar(500)      NOT NULL  COMMENT '文件地址，实际就是文件存储的路径，不包括文件名称',
  fileName    varchar(100)      NOT NULL  COMMENT '文件名称，包括扩展名，注意只有文件名称，没有路径',
  fileExtName varchar(30)       NOT NULL  COMMENT '文件扩展名称',
  fileSize    int(10) unsigned  NOT NULL  COMMENT '文件大小，字节数',
  descn       varchar(500)                COMMENT '说明',
  fcTime      timestamp         NOT NULL  COMMENT '文件创建时间',
  flmTime     timestamp         NOT NULL  COMMENT '文件最后修改时间',
  cTime       timestamp         NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '记录创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件记录索引';

/**012 文件分类[SA_FILE_CATEGORY]*/
DROP TABLE IF EXISTS sa_file_category;
CREATE TABLE sa_file_category (
  id      varchar(32)  NOT NULL  COMMENT '文件分类表ID(UUID)',
  fId     varchar(32)  NOT NULL  COMMENT '主文件id，指向文件表',
  type1   varchar(10)  NOT NULL  COMMENT '文件大类型：目前支持持三种,IMP、LOG和ANAL，即导入文件、日志和分析，日志是文本文件，分析是jsonD格式',
  type2   varchar(60)  NOT NULL  COMMENT '文件中类型：文件的二级类型,当时IMP时，可以是rdata（关系型数据）',
  type3   varchar(60)            COMMENT '文件子类型：文件的三级类型',
  extInfo varchar(2000)          COMMENT '扩展信息，比如jsonD的一些说明',
  cTime   timestamp    NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '文件创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件分类(文件语义)';

/**013 文件关系[SA_FILE_REL]*/
DROP TABLE IF EXISTS sa_file_rel;
CREATE TABLE sa_file_rel (
  id     varchar(32)      NOT NULL  COMMENT '文件关系表ID(UUID)',
  aType  int(1) unsigned  NOT NULL  COMMENT '第一文件类型：=1是对原生态表的关联关系；=2是文件分类',
  aId    varchar(32)      NOT NULL  COMMENT '第一文件类Id',
  bType  int(1) unsigned  NOT NULL  COMMENT '第二文件类型：=1是对原生态表的关联关系；=2是文件分类',
  bId    varchar(32)      NOT NULL  COMMENT '第二文件类Id',
  rType1 int(1)           NOT NULL  COMMENT '关联类型1:=1单向-说明aid是bid的子；=0平等；=-1反向-说明bid是aid的子(这个通过视图实现)',
  rType2 varchar(200)               COMMENT '关联类型2',
  descn  varchar(500)               COMMENT '说明',
  cTime  timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件之间的关系';

/**014 反向关系视图[vSA_FILE_INVERSEREL]*/
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_file_inverserel AS
  select id, bId AS aId, bType AS aType, aId AS bId, aType AS bType, 
  (0-rType1) AS rType1, rType2, descn, cTime
  from sa_file_rel;

/**015 导入文件视图[vSA_IMP_LOG]*/
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_imp_log AS
  select a.id, ownerId, ownerType, accessType, filePath, fileName, fileExtName, fileSize, b.extInfo AS cFileName, descn, a.cTime
  from sa_file_index a, sa_file_category b
  where a.id=b.fid and b.type1='IMP';

/**016 报告信息[SA_REPORT_INFO]*/
DROP TABLE IF EXISTS sa_report_info;
CREATE TABLE sa_report_info (
  id          varchar(32)      NOT NULL  COMMENT '报告信息表ID(UUID)',
  taskGId     varchar(32)                COMMENT '任务组Id(UUID)',
  fId         varchar(32)      NOT NULL  COMMENT '对应报告文件Id(UUID)',
  ownerType   int(1) unsigned  NOT NULL  COMMENT '用户类型(1-用户，2-session，3-系统)',
  ownerId     varchar(32)      NOT NULL  COMMENT '用户Id或SessionID(或指向用户表)，引起文件生成的用户，可以是系统sys',
  reportType  varchar(100)               COMMENT '报告分类',
  reportName  varchar(100)     NOT NULL  COMMENT '报告名称',
  descn       varchar(500)               COMMENT '报告描述',
  cTime       timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='报告信息';

/**017 报告信息视图[vSA_REPORT_FILE]*/
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_report_file AS
  select a.id, ownerId, ownerType, accessType, filePath, fileName, fileExtName, fileSize, b.type2 reportId, b.type3 tasksId,b.extInfo, descn, a.cTime
  from sa_file_index a, sa_file_category b
  where a.id=b.fid and b.type1='REPORT';

/**018 任务组[SA_TASK_GROUP]*/
DROP TABLE IF EXISTS sa_task_group;
CREATE TABLE sa_task_group (
  id          varchar(32)      NOT NULL  COMMENT '任务组表ID(UUID)',
  reportId    varchar(32)                COMMENT '对应报告信息Id(UUID)，可为空，分析可以没有报告',
  ownerType   int(1) unsigned  NOT NULL  COMMENT '用户类型(1-用户，2-session，3-系统)',
  ownerId     varchar(32)      NOT NULL  COMMENT '用户Id或SessionID(或指向用户表)，引起文件生成的用户，可以是系统sys',
  workName    varchar(100)               COMMENT '任务组名称',
  status      int(1) unsigned  NOT NULL  COMMENT '任务组状态1=准备执行；2=正在执行；3=任务失效；4=执行成功；5=执行失败',
  descn       varchar(500)               COMMENT '任务组说明',
  beginTime   timestamp        NOT NULL  DEFAULT CURRENT_TIMESTAMP  COMMENT '任务组开始执行时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务组信息';

/**019 任务信息[SA_TASK_INFO]*/
DROP TABLE IF EXISTS sa_task_info;
CREATE TABLE sa_task_info (
  id            varchar(32)      NOT NULL                  COMMENT '任务表ID(UUID)',
  taskGId       varchar(32)                                COMMENT '外键任务组表ID(UUID)，可为空，任务可独立',
  resultFileId  varchar(32)                                COMMENT '结果文件Id，若没有执行结果，此字段可以为空',
  taskName      varchar(100)     NOT NULL                  COMMENT '任务名称',
  langType      varchar(50)      NOT NULL  DEFAULT 'java'  COMMENT '任务执行语言：目前只有Java',
  excuteFunc    varchar(200)     NOT NULL                  COMMENT '任务执行方法，要实现一个接口',
  param         varchar(500)                               COMMENT '任务执行所需的参数，用json处理', 
  status        int(1) unsigned  NOT NULL  DEFAULT 1       COMMENT '任务状态1=准备执行；2=等待执行；3=正在执行；4=任务失效；5=执行成功；6=执行失败；',
  excuteCount   int(2) unsigned  NOT NULL  DEFAULT 0       COMMENT '任务执行次数', 
  descn         varchar(500)                               COMMENT '任务说明',
  firstTime     timestamp                                  COMMENT '第一次放入执行队列的时间',
  beginTime     timestamp                  DEFAULT NULL    COMMENT '开始执行时间',
  endTime       timestamp                  DEFAULT NULL    COMMENT '结束执行时间',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务信息';

/**020 任务关系[SA_TASK_REL]*/
DROP TABLE IF EXISTS sa_task_rel;
CREATE TABLE sa_task_rel (
  id           varchar(32)      NOT NULL               COMMENT '任务关系表ID(UUID)',
  taskId       varchar(32)      NOT NULL               COMMENT '任务Id，外键',
  preTaskId    varchar(100)     NOT NULL               COMMENT '前置任务Id',
  usedPreData  int(1) unsigned  NOT NULL  DEFAULT '2'  COMMENT '是否利用前序任务的数据1-利用;2-不利用',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务关系，前序联系，有向图';

/**021 任务全信息视图[vSA_TASKS] 由于mysql不支持子查询的视图，因此需要对视图进行拆分*/
/*
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_tasks AS
select t.*, concat(sf.filePath, '/', sf.fileName) PT_RFile  from (
select
  m.MT_Id, m.MT_RFile, m.MT_langType, m.MT_excuteFunc, m.MT_param, m.MT_status, m.MT_tName, m.MT_descn, m.MT_firstTime, m.MT_beginTime, m.MT_endTime,
  m.TG_Id, m.reportId, m.ownerId, m.ownerType, m.TG_status, m.workName, m.TG_descn, m.TG_beginTime,
  p.id PT_id, p.resultFileid, p.langType PT_langType, p.excuteFunc PT_excuteFunc, p.param PT_param, p.status PT_status, p.taskName PT_tName, p.descn PT_descn,
  p.firstTime PT_firstTime, p.beginTime PT_beginTime, p.endTime PT_endTime
from (
  select
    a.id MT_Id, concat(d.filePath, '/', d.fileName) MT_RFile,a.langType MT_langType, a.excuteFunc MT_excuteFunc, a.param MT_param,
    a.status MT_status, a.taskName MT_tName, a.descn MT_descn, a.firstTime MT_firstTime, a.beginTime MT_beginTime, a.endTime MT_endTime, 
    c.id TG_Id, c.reportId, c.ownerId, c.ownerType, c.status TG_status, c.workName, c.descn TG_descn, c.beginTime TG_beginTime,
    b.preTaskId, b.usedPreData
  from sa_task_info a
  inner join sa_task_rel b on a.id=b.taskId
  left join sa_task_group c on a.taskGId=c.id
  left join sa_file_index d on a.resultFileid=d.id
) m
left join sa_task_info p on p.id=m.preTaskId
) t
left join sa_file_index sf on t.resultFileid=sf.id;
*/
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vpsa_tasks1 AS (
  select
    a.id MT_Id, concat(d.filePath, '/', d.fileName) MT_RFile,a.langType MT_langType, a.excuteFunc MT_excuteFunc, a.param MT_param,
    a.status MT_status, a.taskName MT_tName, a.descn MT_descn, a.firstTime MT_firstTime, a.beginTime MT_beginTime, a.endTime MT_endTime, 
    c.id TG_Id, c.reportId, c.ownerId, c.ownerType, c.status TG_status, c.workName, c.descn TG_descn, c.beginTime TG_beginTime,
    b.preTaskId, b.usedPreData
  from sa_task_info a
  inner join sa_task_rel b on a.id=b.taskId
  left join sa_task_group c on a.taskGId=c.id
  left join sa_file_index d on a.resultFileid=d.id
);
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vpsa_tasks2 AS (
  select
    m.MT_Id, m.MT_RFile, m.MT_langType, m.MT_excuteFunc, m.MT_param, m.MT_status, m.MT_tName, m.MT_descn, m.MT_firstTime, m.MT_beginTime, m.MT_endTime,
    m.TG_Id, m.reportId, m.ownerId, m.ownerType, m.TG_status, m.workName, m.TG_descn, m.TG_beginTime,
    p.id PT_id, p.resultFileid, p.langType PT_langType, p.excuteFunc PT_excuteFunc, p.param PT_param, p.status PT_status, p.taskName PT_tName, p.descn PT_descn,
    p.firstTime PT_firstTime, p.beginTime PT_beginTime, p.endTime PT_endTime
  from vpsa_tasks1 m
  left join sa_task_info p on p.id=m.preTaskId
);
CREATE OR REPLACE ALGORITHM=UNDEFINED SQL SECURITY DEFINER
VIEW vsa_tasks AS (
  select t.*, concat(sf.filePath, '/', sf.fileName) PT_RFile
  from vpsa_tasks2 t
  left join sa_file_index sf on t.resultFileid=sf.id
);