-- Moment Takeout - seed data for local development
-- Assumes schema.sql has already been executed.

SET NAMES utf8mb4;
USE moment_takeout;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE order_detail;
TRUNCATE TABLE orders;
TRUNCATE TABLE shopping_cart;
TRUNCATE TABLE address_book;
TRUNCATE TABLE setmeal_dish;
TRUNCATE TABLE dish_flavor;
TRUNCATE TABLE dish;
TRUNCATE TABLE setmeal;
TRUNCATE TABLE category;
TRUNCATE TABLE user;
TRUNCATE TABLE employee;

INSERT INTO employee (id, username, password, name, phone, sex, id_number, status, create_time, update_time)
VALUES
  (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13800138000', '1', '440301199001011234', 1, NOW(), NOW());

INSERT INTO user (id, openid, name, phone, avatar, sex, status, create_time) VALUES
  (1, 'mock-openid', '测试用户A', '13800000000', 'https://picsum.photos/200', '1', 1, NOW()),
  (2, 'mock-openid-2', '测试用户B', '13900000000', 'https://picsum.photos/201', '2', 1, NOW());

INSERT INTO category (id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES
  (1, 1, '招牌热菜', 1, 1, NOW(), NOW(), 1, 1),
  (2, 1, '经典主食', 2, 1, NOW(), NOW(), 1, 1),
  (3, 1, '清爽饮品', 3, 1, NOW(), NOW(), 1, 1),
  (4, 2, '精选套餐', 4, 1, NOW(), NOW(), 1, 1);

INSERT INTO dish (id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) VALUES
  (1, '宫保鸡丁', 1, 32.00, 'https://picsum.photos/seed/dish1/400/300', '花生+鸡丁+辣椒的经典搭配', 1, NOW(), NOW(), 1, 1),
  (2, '番茄牛腩饭', 2, 28.50, 'https://picsum.photos/seed/dish2/400/300', '慢炖牛腩配番茄酱汁', 1, NOW(), NOW(), 1, 1),
  (3, '杨枝甘露', 3, 18.00, 'https://picsum.photos/seed/dish3/400/300', '港式招牌甜品', 1, NOW(), NOW(), 1, 1),
  (4, '招牌煎饺', 1, 22.00, 'https://picsum.photos/seed/dish4/400/300', '手工煎饺配秘制酱料', 1, NOW(), NOW(), 1, 1);

INSERT INTO dish_flavor (id, dish_id, name, value) VALUES
  (1, 1, '辣度', '不辣,微辣,中辣,特辣'),
  (2, 2, '加料', '加蛋,加牛肉,加芝士'),
  (3, 3, '甜度', '三分糖,五分糖,七分糖,全糖'),
  (4, 4, '蘸料', '醋汁,蒜蓉,椒盐');

INSERT INTO setmeal (id, category_id, name, price, description, image, status, create_time, update_time, create_user, update_user) VALUES
  (1, 4, '双人工作餐', 58.00, '两荤一素+饮品', 'https://picsum.photos/seed/setmeal1/400/300', 1, NOW(), NOW(), 1, 1);

INSERT INTO setmeal_dish (id, setmeal_id, dish_id, name, price, copies) VALUES
  (1, 1, 1, '宫保鸡丁', 32.00, 1),
  (2, 1, 2, '番茄牛腩饭', 28.50, 1);

INSERT INTO address_book (id, user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default)
VALUES
  (1, 1, '张三', '13800000000', '1', '440000', '广东省', '440300', '深圳市', '440305', '南山区', '科技园一路 100 号', '公司', 1),
  (2, 1, '张三', '13800000000', '1', '440000', '广东省', '440300', '深圳市', '440305', '南山区', '学府路 88 号', '家', 0);

INSERT INTO shopping_cart (id, name, user_id, dish_id, dish_flavor, number, amount, image, create_time)
VALUES
  (1, '宫保鸡丁', 1, 1, '中辣', 2, 64.00, 'https://picsum.photos/seed/dish1/200/150', NOW());

INSERT INTO orders (id, number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, user_name, phone, address, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status)
VALUES
  (1, '202311150001', 4, 1, 1, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY + INTERVAL 30 MINUTE, 1, 1, 96.00, '午餐加辣', '测试用户A', '13800000000', '广东省深圳市南山区科技园一路100号', '张三', NULL, NULL, NULL, NOW() - INTERVAL 1 DAY + INTERVAL 20 MINUTE, 1, NOW() - INTERVAL 1 DAY + INTERVAL 45 MINUTE, 4, 2, 1),
  (2, '202311160001', 2, 2, NULL, NOW() - INTERVAL 1 DAY, NULL, 1, 0, 58.00, '请尽快配送', '测试用户B', '13900000000', '线上地址', '李四', NULL, NULL, NULL, NOW() - INTERVAL 1 DAY + INTERVAL 1 HOUR, 0, NULL, 2, 1, 1);

INSERT INTO order_detail (id, name, order_id, dish_id, dish_flavor, number, amount, image) VALUES
  (1, '宫保鸡丁', 1, 1, '中辣', 2, 64.00, 'https://picsum.photos/seed/dish1/200/150'),
  (2, '杨枝甘露', 1, 3, '五分糖', 1, 18.00, 'https://picsum.photos/seed/dish3/200/150'),
  (3, '双人工作餐', 2, NULL, '标准', 1, 58.00, 'https://picsum.photos/seed/setmeal1/200/150');

SET FOREIGN_KEY_CHECKS = 1;
