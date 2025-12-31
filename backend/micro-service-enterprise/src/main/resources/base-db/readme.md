一、迁移脚本命名规则详解
1.1 基本命名格式
text
前缀 + 版本号 + 分隔符 + 描述 + 后缀
1.2 前缀类型详解
V - 版本化迁移 (Versioned Migrations)
用途：数据库结构变更（CREATE、ALTER、DROP等）

特点：只执行一次，版本号必须唯一且递增

示例：

sql
-- V1.0.1__Create_user_table.sql
CREATE TABLE user (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
username VARCHAR(50) NOT NULL
);

-- V1.0.2__Add_email_column.sql
ALTER TABLE user ADD COLUMN email VARCHAR(100);
U - 撤销迁移 (Undo Migrations) - 商业版功能
用途：回滚对应的版本化迁移

特点：与V前缀脚本一一对应

示例：

sql
-- U1.0.2__Remove_email_column.sql
ALTER TABLE user DROP COLUMN email;

-- U1.0.1__Drop_user_table.sql
DROP TABLE user;
R - 可重复迁移 (Repeatable Migrations)
用途：可重复执行的脚本（视图、存储过程、函数等）

特点：没有版本号，按描述字母顺序执行，内容变更时重新执行

示例：

sql
-- R__Create_user_view.sql
CREATE OR REPLACE VIEW active_users AS
SELECT * FROM user WHERE status = 'ACTIVE';

-- R__Update_reporting_procedure.sql
CREATE OR REPLACE PROCEDURE generate_user_report()
BEGIN
-- 存储过程逻辑
END;
1.3 版本号格式
数字版本号
text
V1__...
V2__...
V3__...
时间戳版本号（推荐）
text
V202310201200__...    -- 2023年10月20日12:00
V202310201230__...    -- 2023年10月20日12:30
语义化版本号
text
V1.0.1__...
V1.0.2__...
V1.1.0__...
1.4 描述部分规范
使用下划线分隔单词

简明扼要描述变更内容

使用动词开头（Create、Add、Modify等）

示例：

✅ V1__Create_user_table.sql

✅ V2__Add_email_to_user.sql

❌ V1__user_table.sql（缺少动词）

❌ V2__email.sql（描述不清晰）