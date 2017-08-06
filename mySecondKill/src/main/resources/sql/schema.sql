#数据库初始化脚本

#创建数据库
DROP database IF EXISTS mySecondKill;
CREATE database mySecondKill DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

#使用数据库
use mySecondKill;

#建表seckill，秒杀商品信息表
#ENGINE是引擎，有MyIASM和INNODB两种，MyIASM读取速度更快，且不大量占用内存。INNoDB支持事物
#AUTO_INCREMENT规定自增的起点
#索引加快搜索速度，但降低插入、删除的性能，并且增大内存占用
#注意：字段名要用英文的钝点`
CREATE TABLE seckill(
	`seckill_id` BIGINT NOT NUll AUTO_INCREMENT COMMENT '商品库存ID',
	`name` VARCHAR(120) NOT NULL COMMENT '商品名称',
	`number` INT NOT NULL COMMENT '库存数量',
	`start_time` TIMESTAMP NOT NULL COMMENT '秒杀开始时间', #没有指定默认值，自动为CURRENT_TIMESTAMP
	`end_time` TIMESTAMP NOT NULL DEFAULT '2017-01-01 00:00:00'  COMMENT '秒杀结束时间',	#mysql5.7以上版本TIMESTAMP默认时间不能为0
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',		#mysql5.7版本以下不能有两个默认值是CURRENT_TIMESTAMP,5.7就可以
	PRIMARY KEY (seckill_id),
	KEY index_start_time(start_time),
	KEY index_end_time(end_time),
	KEY index_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

#初始化seckill表数据
INSERT INTO seckill(name,number,start_time,end_time)
VALUES
	('1000元秒杀iPhone7',100,'2017-01-01 00:00:00','2018-12-12 00:00:00'),
	('800元秒杀ipad',200,'2017-01-01 00:00:00','2017-2-12 00:00:00'),
	('5000元秒杀Mac Pro',300,'2018-12-11 00:00:00','2018-12-12 00:00:00');

#建表success_killed，秒杀成功明细表
#用户登录认证信息（简化为手机号）
#注意：字段名要用英文的钝点`
CREATE TABLE success_killed(
	`seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
	`user_phone` BIGINT NOT NULL COMMENT '用户手机号',
	`state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
	`create_time` TIMESTAMP NOT NULL COMMENT '订单创建时间',
	PRIMARY KEY (seckill_id,user_phone),/*联合主键，确保一个用户只能秒杀一个商品*/
	KEY index_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表'