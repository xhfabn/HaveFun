# 数据库初始化说明

位于 `moment-server/sql` 目录下的脚本用于快速准备 `moment_takeout` 数据库，包含表结构与本地开发示例数据。

## 文件说明

- `schema.sql`：创建 `moment_takeout` 数据库及所有表的 DDL。
- `seed_dev.sql`：清空主要业务表并插入示例数据（管理员账号、菜品、套餐、地址、订单等）。
- `moment_init.sql`：包装脚本，依次 `SOURCE schema.sql` 与 `SOURCE seed_dev.sql`，适合一键初始化。

## 使用步骤

1. 登录数据库：`mysql -u root -p`
2. （可选）修改 `schema.sql` 顶部的数据库名称，默认 `moment_takeout`。
3. 在 MySQL 控制台执行：`SOURCE c:/Users/.../moment-server/sql/moment_init.sql;`
4. 完成后即可启动后端与前端，使用以下默认账号：
   - 管理端：`admin` / `123456`
   - 用户端：前端模拟登录默认写入 `mock-openid`。

重复执行 `moment_init.sql` 会重新建表并写入相同示例数据，适合清空环境后快速回到初始状态。
