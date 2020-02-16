-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
--
-- Database: `magazin`
--

--
-- Create table `admin_users` and `admin_history`
--

CREATE TABLE `admin_users` (
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `user` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `pass` varchar(255) NOT NULL
)

CREATE TABLE `admin_history` (
  `id` int(11) PRIMARY KEY NOT NULL,
  `user` varchar(50) NOT NULL,
  `name_before_update` varchar(100) NOT NULL,
  `edit_date` DATETIME NOT NULL
)

--
-- trigger for logging the admin updates
--

CREATE TRIGGER trigger_before_update BEFORE UPDATE ON admin_users
FOR EACH ROW INSERT INTO admin_history (`id`, `name_before_update`, `user`, `edit_date`)
VALUES( OLD.id, OLD.name, OLD.user, NOW());



-- check trigger 
SHOW TRIGGERS;
UPDATE `admin_users` SET `name` = "admin_name_updated" WHERE id = 1;
SELECT * from `admin_history`;

--
-- Procedure to initialise admins
--

--DELIMITER $

CREATE PROCEDURE initialize_admins (pass VARCHAR(35)) 
BEGIN
INSERT INTO `admin_users` (`user`, `name`, `email`, `pass`) VALUES
('admin', 'Administrator', 'ciprian.reghina@gmail.com', pass),
('cipa', 'Ciprian Reghina', 'ghidy77@yahoo.com', pass);
END 

--DELIMITER ;

SET @pass = "21232f297a57a5a743894a0e4a801fc3";
CALL initialize_admins(@pass);


--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `stock` TINYINT NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(200) DEFAULT NULL,
  `price` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Procedure to initialise products
--

CREATE PROCEDURE initialize_products (stock TINYINT) 
BEGIN
INSERT INTO `products` (`name`, `stock`, `description`, `image`, `price`) VALUES
('3_Clatite', stock, '3 clatite bunicele', '3_clatite.jpg', 10),
('6_Clatite', stock, '6 clatite bunicele', '6_clatite.jpg', 18),
('9_Clatite', stock, 'Pachet 9 Clatite', '9_clatite.jpg', 25),
('50_Clatite', stock, 'Pachet 50 clatite', '50_clatite.jpg', 120),
('100_Clatite', stock, 'Pachet 100 Clatite', '100_clatite.jpg', 240),
('Gogosica', stock, 'Gogosica calda si proaspata', '', 2);
END;

CALL initialize_products(50);


-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `idsale` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `totalPrice` float NOT NULL,
  `idshopper` INT(11) NOT NULL,
  `date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=1000; 

--
-- Table structure for table `shoppers`
--

CREATE TABLE `shoppers` (
  `idshopper` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `email` varchar(200) NOT NULL,
  `address` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL
);

CREATE INDEX idx_shop_name ON shoppers (`name`, `email`);


--
-- generate sales or shoppers
--

CREATE PROCEDURE generate_shoppers (prefix VARCHAR(50)) 
BEGIN
INSERT INTO `shoppers` (`name`, `email`, `address`, `phone`) VALUES
('Test User', (SELECT CONCAT(prefix, '@gmail.com')), 'Bucuresti', '000000000');
END


CREATE PROCEDURE generate_sales_for_shopper (idshopper TINYINT) 
BEGIN
	INSERT INTO `sales` (
  	  `totalPrice`,`idshopper`) VALUES (999, idshopper);
END

CALL generate_shoppers('test');
CALL generate_sales_for_shopper(100);


--
-- get random shopper function
--
CREATE FUNCTION get_shopper ( id TINYINT) RETURNS VARCHAR(50)
BEGIN
   RETURN (SELECT `name` FROM `shoppers` WHERE `idshopper` = id);
END


SELECT get_shopper( (SELECT CEIL( RAND() * 10)) );


