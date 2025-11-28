-- Moment Takeout 初始化脚本
-- 用法：
-- 1. mysql -u root -p
-- 2. SOURCE /path/to/moment_out/moment-server/sql/moment_init.sql;
-- 该脚本会依次执行 schema.sql 与 seed_dev.sql，生成表结构并插入示例数据。

SOURCE schema.sql;
SOURCE seed_dev.sql;
