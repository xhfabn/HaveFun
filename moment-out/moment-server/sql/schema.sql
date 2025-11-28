-- Moment Takeout - database schema
-- This script only contains DDL. Run seed_dev.sql afterwards for sample data.

SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS moment_takeout DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE moment_takeout;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS shopping_cart;
DROP TABLE IF EXISTS address_book;
DROP TABLE IF EXISTS setmeal_dish;
DROP TABLE IF EXISTS dish_flavor;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS setmeal;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS employee;

CREATE TABLE employee (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(128) NOT NULL,
  name VARCHAR(50) NOT NULL,
  phone VARCHAR(20),
  sex VARCHAR(2),
  id_number VARCHAR(20),
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_user BIGINT,
  update_user BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  openid VARCHAR(64) UNIQUE,
  name VARCHAR(50),
  phone VARCHAR(20),
  avatar VARCHAR(255),
  sex VARCHAR(2),
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type TINYINT NOT NULL COMMENT '1 菜品 2 套餐',
  name VARCHAR(64) NOT NULL,
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_user BIGINT,
  update_user BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  category_id BIGINT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  image VARCHAR(255),
  description VARCHAR(255),
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_user BIGINT,
  update_user BIGINT,
  FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE dish_flavor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dish_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  value VARCHAR(255) NOT NULL,
  FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE setmeal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  description VARCHAR(255),
  image VARCHAR(255),
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_user BIGINT,
  update_user BIGINT,
  FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE setmeal_dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setmeal_id BIGINT NOT NULL,
  dish_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  copies INT DEFAULT 1,
  FOREIGN KEY (setmeal_id) REFERENCES setmeal(id) ON DELETE CASCADE,
  FOREIGN KEY (dish_id) REFERENCES dish(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE address_book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  consignee VARCHAR(50) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  sex VARCHAR(2),
  province_code VARCHAR(12),
  province_name VARCHAR(20),
  city_code VARCHAR(12),
  city_name VARCHAR(20),
  district_code VARCHAR(12),
  district_name VARCHAR(20),
  detail VARCHAR(200) NOT NULL,
  label VARCHAR(20),
  is_default TINYINT DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shopping_cart (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64),
  user_id BIGINT NOT NULL,
  dish_id BIGINT,
  setmeal_id BIGINT,
  dish_flavor VARCHAR(255),
  number INT DEFAULT 1,
  amount DECIMAL(10,2) NOT NULL,
  image VARCHAR(255),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  number VARCHAR(64) NOT NULL UNIQUE,
  status TINYINT DEFAULT 1,
  user_id BIGINT NOT NULL,
  address_book_id BIGINT,
  order_time DATETIME,
  checkout_time DATETIME,
  pay_method TINYINT,
  pay_status TINYINT DEFAULT 0,
  amount DECIMAL(10,2) NOT NULL,
  remark VARCHAR(255),
  user_name VARCHAR(50),
  phone VARCHAR(20),
  address VARCHAR(255),
  consignee VARCHAR(50),
  cancel_reason VARCHAR(255),
  rejection_reason VARCHAR(255),
  cancel_time DATETIME,
  estimated_delivery_time DATETIME,
  delivery_status TINYINT,
  delivery_time DATETIME,
  pack_amount INT DEFAULT 0,
  tableware_number INT DEFAULT 0,
  tableware_status TINYINT DEFAULT 1,
  FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  order_id BIGINT NOT NULL,
  dish_id BIGINT,
  setmeal_id BIGINT,
  dish_flavor VARCHAR(255),
  number INT DEFAULT 1,
  amount DECIMAL(10,2) NOT NULL,
  image VARCHAR(255),
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
