-- 添加gender和age字段到sys_user表
-- 如果数据库是 intelligent_recruitment2，请根据实际情况修改

-- 检查字段是否存在，如果不存在则添加
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS gender TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女' AFTER avatar;
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS age INT COMMENT '年龄' AFTER gender;

-- 或者使用以下命令（MySQL 5.7及以下版本）
-- ALTER TABLE sys_user ADD COLUMN gender TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女' AFTER avatar;
-- ALTER TABLE sys_user ADD COLUMN age INT COMMENT '年龄' AFTER gender;
