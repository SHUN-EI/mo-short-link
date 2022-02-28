drop table if exists  product;
CREATE TABLE product (
  id bigint NOT NULL,
  title varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商品标题',
  detail varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详情',
  img varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图片',
  level varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '产品层级：FIRST青铜、SECOND黄金、THIRD钻石',
  old_amount decimal(16,0) DEFAULT NULL COMMENT '原价',
  amount decimal(16,0) DEFAULT NULL COMMENT '现价',
  plugin_type varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '工具类型 short_link、qrcode',
  day_times int DEFAULT NULL COMMENT '日次数：短链类型',
  total_times int DEFAULT NULL COMMENT '总次数：活码才有',
  valid_day int DEFAULT NULL COMMENT '有效天数',
  gmt_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  gmt_create datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='商品表';