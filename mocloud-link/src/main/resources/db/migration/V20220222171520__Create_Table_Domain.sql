drop table if exists  domain;
CREATE TABLE domain (
  id bigint unsigned NOT NULL ,
  account_no bigint DEFAULT NULL COMMENT '用户自己绑定的域名',
  domain_type varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '域名类型，自建custom, 官方offical',
  value varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  del int(1) unsigned zerofill DEFAULT '0' COMMENT '0是默认，1是禁用',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin  COMMENT='短链域名表';