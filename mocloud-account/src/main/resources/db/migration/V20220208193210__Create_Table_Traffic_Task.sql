drop table  if exists  traffic_task;
CREATE TABLE traffic_task (
     id bigint(20) unsigned NOT NULL AUTO_INCREMENT,
     account_no bigint DEFAULT NULL COMMENT '账号',
     traffic_id bigint DEFAULT NULL COMMENT '流量包id',
     use_times int DEFAULT NULL COMMENT '流量包已使用次数',
     lock_state varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '锁定状态锁定LOCK  完成FINISH-取消CANCEL',
     message_id varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '唯一标识',
     create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
     PRIMARY KEY (id),
     UNIQUE KEY uk_msg_id (message_id) USING BTREE,
     KEY idx_release (account_no,id) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流量包任务表';