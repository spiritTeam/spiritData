/**001 PLAT_USER���û���*/
DROP TABLE IF EXISTS plat_user;
CREATE TABLE plat_user (
  id         varchar(36)     NOT NULL              COMMENT 'uuid���û�id��',
  loginName  varchar(15)     NOT NULL              COMMENT '��¼�˺�',
  userName   varchar(100)    NOT NULL              COMMENT '��¼�˺�',
  password   varchar(30)              DEFAULT NULL,
  mailAdress varchar(100)    NOT NULL              COMMENT '����(�ǿ�Ϊһ����)',
  nickName   varchar(100)             DEFAULT NULL COMMENT '�ǳƣ��ɿ�',
  userType   int(1) unsigned NOT NULL DEFAULT 1    COMMENT '�û����ࣺ1��Ȼ���û���2�����û�',
  descn      varchar(2000)            DEFAULT NULL COMMENT '��ע',
  cTime      timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��:����ʱ��ϵͳʱ��',
  lmTime     timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����޸ģ�ÿ�θ��µ�ʱ��',
  PRIMARY KEY (id),
  UNIQUE KEY loginName (loginName) USING BTREE,
  UNIQUE KEY mailAdress (mailAdress) USING BTREE
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='�û���';

/**002 Ԫ����ģʽ[SA_MD_TABMODEL]*/
DROP TABLE IF EXISTS sa_md_tabmodel;
CREATE TABLE sa_md_tabmodel (
  id        varchar(36)     NOT NULL              COMMENT 'uuid��ģʽID��',
  ownerId   varchar(36)     NOT NULL              COMMENT 'ָ���û���(�û�id��sessionId)',
  ownerType int(1) unsigned NOT NULL              COMMENT '�û�����(1-�û���2-session)',
  tableName varchar(40)     NOT NULL              COMMENT 'ҵ�� ���еĻ��۱������',
  descn     varchar(400)             DEFAULT NULL COMMENT '��ע',
  cTime     timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '������ϵͳʱ��',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='Ԫ����ģʽ��';

/**003 Ԫ����������[SA_MD_COLUMN]*/
DROP TABLE IF EXISTS sa_md_column;
CREATE TABLE sa_md_column (
  id          varchar(36) NOT NULL      COMMENT 'ID����,��֧��UUID',
  tmId        varchar(36) NOT NULL      COMMENT '��ģʽid(���)',
  columnName  varchar(10) NOT NULL      COMMENT '��������Ϊ��������',
  titleName   varchar(100) DEFAULT NULL COMMENT '����������(�������ƣ���Ϊexcel����Ϊ��ͷ���ƣ���ΪDB��Ƚϸ���)',
  columnType  varchar(10) NOT NULL DEFAULT 'String',
  columnIndex int(3) unsigned NOT NULL DEFAULT '0',
  pkSign      int(1) unsigned NOT NULL DEFAULT '2' COMMENT '�Ƿ�������(0-��������,1-ȷ������,2-��ȷ������)',
  cTime       timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��ϵͳʱ��',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='Ԫ����������';

/**004Ԫ����������[SA_MD_COLSEMANTEME]*/
DROP TABLE IF EXISTS sa_md_colsemanteme;
CREATE TABLE  sa_md_colsemanteme (
  id                varchar(36) NOT NULL      COMMENT 'uuid',
  cId               varchar(36) NOT NULL      COMMENT '������Id(���)',
  tmId              varchar(36) NOT NULL      COMMENT 'Ԫ����ģʽId(ģʽid���������)',
  semantemeCode     int(1) unsigned NOT NULL  COMMENT '�������(ĳ����ȷ���������ͺ󣬶Ը����͵����ݵľ���������Ŀǰֻ���ֵ���������)',
  semantemeType     int(1) unsigned NOT NULL  COMMENT '��������(1-���֤��2-�ֵ��������)',
  cTime timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '����ʱ��',
  lmTime timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT 'ÿ�θ���ʱ�޸�',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='Ԫ���������';

/**005 Ԫ���ݱ����[SA_MD_TABMAP_ORG]*/
DROP TABLE IF EXISTS sa_md_tabmap_org;
CREATE TABLE  sa_md_tabmap_org (
  id                varchar(36) NOT NULL        COMMENT 'uuid(���ձ�id)',
  ownerId           varchar(36) NOT NULL        COMMENT '�û�Id����sessionId',
  tmId              varchar(36) NOT NULL        COMMENT 'Ԫ����ģʽId(��ģʽID)',
  tableName         varchar(40) NOT NULL        COMMENT '�����ƣ���������ҵ�����ݱ����ƶ�Ӧ��',
  tableType         int(1) unsigned NOT NULL DEFAULT '1' COMMENT '������(1-���۱�2-��ʱ��)',
  tableDescn        varchar(400) COMMENT '��ע',
  cTime             timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��ϵͳʱ��',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='Ԫ���ݶ��չ�ϵ��';

/**006 Ԫ���ݱ�ָ��[SA_MD_TABLEQUOTA]*/
DROP TABLE IF EXISTS sa_md_tabquota;
CREATE TABLE  sa_md_tabquota (
  id            varchar(36) NOT NULL         COMMENT '��ָ��Id(UUID)',
  tmoId         varchar(36) NOT NULL         COMMENT '���ձ�Id(Ԫ���ݶ��ձ�Id�����)',
  tmId          varchar(36) NOT NULL         COMMENT 'Ԫ����ģʽId(ģʽId���������)',
  tableName     varchar(40) NOT NULL         COMMENT '������(�����ƣ��������)',
  allCount      int(10) unsigned NOT NULL DEFAULT '0' COMMENT '������(���м�¼����)',
  cTime         timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP COMMENT '����ʱ��',
  lmTime        timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����޸�ʱ��',
  laTime        timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP COMMENT '�������޸�',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='Ԫ���ݱ�ָ��';

/**007 Ԫ������ָ��[SA_MD_COLUMNQUOTA]*/
DROP TABLE IF EXISTS sa_md_colquota;
CREATE TABLE  sa_md_colquota (
  id            varchar(36) NOT NULL          COMMENT '��ָ��Id(UUID)',
  cId           varchar(36) NOT NULL          COMMENT '������Id(������Id�����',
  tqId          varchar(36) NOT NULL          COMMENT '��ָ��Id(ʵ���ָ��Id���)',
  max           varchar(4000) NOT NULL        COMMENT '������ʲô���ͣ���Ҫת���ַ���',
  min           varchar(4000) NOT NULL        COMMENT '������ʲô���ͣ���Ҫת���ַ���',
  nullCount     int(10) unsigned NOT NULL DEFAULT '0' COMMENT '��ֵ��(���б��еĿ�ֵ��)',
  distinctCount int(10) unsigned NOT NULL DEFAULT '0' COMMENT '��ֵ��(���б������غ�ĸ���)',
  cTime         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
  lmTime        timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����޸�',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='Ԫ������ָ���';

/**008 ���ݵ�����־[SA_IMP_LOG]*/
DROP TABLE IF EXISTS sa_imp_log;
CREATE TABLE  sa_imp_log (
  id            varchar(36) NOT NULL          COMMENT '��־id(UUID)',
  ownerId       varchar(36) NOT NULL          COMMENT '�û�Id��SessionID(��ָ���û���)',
  sFileName     varchar(500) NOT NULL         COMMENT '������ļ���(�����ļ�·��',
  fileSize      int(10) unsigned NOT NULL     COMMENT '�ļ���С',
  cFileName     varchar(500) DEFAULT NULL     COMMENT '�ͻ����ļ���(�����ļ�·��)',
  cTime         timestamp NOT NULL DEFAULT    CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��(Ҳ������Ϊ�ϴ�ʱ��)',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='���ݵ�����־';

/**009 �����ļ�/ʵ����Ӧ[SA_IMP_TABLOG_ORG]*/
DROP TABLE IF EXISTS sa_imp_tablog_org;
CREATE TABLE  sa_imp_tablog_org (
  id            varchar(36) NOT NULL         COMMENT '�ļ�/ʵ���Ӧ��ϵID(UUID)',
  ufId          varchar(36) NOT NULL         COMMENT '�ļ���־ID(�ļ������)',
  tmoId         varchar(36) NOT NULL         COMMENT '���ձ�Id(Ԫ����ʵ������Id�����)',
  tmId          varchar(36) NOT NULL         COMMENT 'Ԫ����ģʽId(��ģʽId���)',
  sheetName     varchar(100) NOT NULL        COMMENT 'ҳǩ����',
  sheetIndex    int(3) unsigned NOT NULL     COMMENT 'ҳǩ����',
  cTime         timestamp NOT NULL DEFAULT   CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����ʱ��',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='�����ļ�/ʵ����Ӧ��ϵ��';

/**011 �ֵ���[PLAT_DICTM]*/
DROP TABLE IF EXISTS plat_dictm;
CREATE TABLE  plat_dictm (
  id         varchar(36)     NOT NULL           COMMENT '�ֵ����ID(UUID)',
  ownerId    varchar(36)     NOT NULL           COMMENT '������Id',
  ownerType  int(1) unsigned NOT NULL DEFAULT 1 COMMENT '����������(1-�û�,2-session)',
  dmName     varchar(200)    NOT NULL           COMMENT '�ֵ�������',
  nPy        varchar(800)                       COMMENT '����ƴ��',
  sort       int(5) unsigned NOT NULL DEFAULT 0 COMMENT '�ֵ�������',
  isValidate int(1) unsigned NOT NULL DEFAULT 1 COMMENT '�Ƿ���Ч(1-��Ч,2-��Ч)',
  mType      int(1) unsigned NOT NULL DEFAULT 3 COMMENT '�ֵ�����(1-ϵͳ����,2-ϵͳ,3-�Զ���)',
  mRef       varchar(4000)                      COMMENT '����ʱ��',
  descn      varchar(500)                       COMMENT '˵��',
  cTime      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
  lmTime     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����޸�ʱ��',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='�ֵ���';

/**012 �ֵ���[PLAT_DICTD]*/
DROP TABLE IF EXISTS plat_dictd;
CREATE TABLE  plat_dictd (
  id         varchar(36)     NOT NULL           COMMENT '�ֵ����ID(UUID)',
  mId        varchar(36)     NOT NULL           COMMENT '�ֵ������(UUID)',
  pId        varchar(36)     NOT NULL           COMMENT '�����ID(UUID)',
  sort       int(5) unsigned NOT NULL DEFAULT 0 COMMENT '�ֵ�������,ֻ�ڱ�������������',
  isValidate int(1) unsigned NOT NULL DEFAULT 1 COMMENT '�Ƿ���Ч(1-��Ч,2-��Ч)',
  ddName     varchar(200)    NOT NULL           COMMENT '�ֵ�������',
  nPy        varchar(800)                       COMMENT '����ƴ��',
  aliasName  varchar(200)                       COMMENT '�ֵ������',
  anPy       varchar(800)                       COMMENT '����ƴ��',
  bCode      varchar(50)     NOT NULL           COMMENT 'ҵ�����',
  mType      int(1) unsigned NOT NULL DEFAULT 3 COMMENT '�ֵ�����(1-ϵͳ����,2-ϵͳ,3-�Զ���,4����-�����ֵ���ID��)',
  dRef       varchar(4000)                      COMMENT '����ʱ��',
  descn      varchar(500)                       COMMENT '˵��',
  cTime      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
  lmTime     timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '����޸�ʱ��',
  PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='�ֵ���';