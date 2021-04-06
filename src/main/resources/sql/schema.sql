SET CHARACTER_SET_CLIENT=utf8;
SET CHARACTER_SET_CONNECTION=utf8;

CREATE DATABASE `sq_nppa` DEFAULT CHARACTER SET utf8  COLLATE utf8_general_ci;
USE `sq_nppa`;

CREATE TABLE `t_user`(
    `id` bigint(20) NOT NULL COMMENT '平台ID',
    `pi` varchar(128) DEFAULT NULL COMMENT '已通过实名认证用户的唯一标识',
    `passportName` varchar(255) DEFAULT NULL COMMENT '角色在飞豆平台的名字',
    `realName` varchar(128) DEFAULT NULL COMMENT '玩家的名字',
    `idNum` varchar(64) DEFAULT NULL COMMENT '玩家的idn',
    `createTime` datetime DEFAULT NULL COMMENT 'CREATE TIME',
    `authStatus`  tinyint(4) NOT NULL DEFAULT 4 COMMENT '认证结果0认证成功1认证中2认证失败 4待认证',
    `authTime` datetime DEFAULT NULL COMMENT '认证的时间，每个status会更新一下TIME',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;