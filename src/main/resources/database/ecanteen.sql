-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 15, 2022 at 08:58 AM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ecanteen`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(5) NOT NULL,
  `name` varchar(30) NOT NULL,
  `date_created` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `date_created`) VALUES
(1, 'Minuman', '2022-06-13'),
(3, 'Makanan Ringan', '2022-06-13');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `barcode` varchar(20) NOT NULL,
  `name` varchar(25) NOT NULL,
  `category_id` int(5) NOT NULL,
  `purchase_price` varchar(9) NOT NULL,
  `selling_price` varchar(9) NOT NULL,
  `stock_amount` int(11) NOT NULL,
  `supplier_id` varchar(16) NOT NULL,
  `date_added` date NOT NULL,
  `expired_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`barcode`, `name`, `category_id`, `purchase_price`, `selling_price`, `stock_amount`, `supplier_id`, `date_added`, `expired_date`) VALUES
('1000', 'Kopi Panas', 1, '1.000', '2.000', 392, 'ID123', '2022-05-21', '0001-01-01'),
('10000', 'Susu Frisian Flag', 1, '1.000', '1.500', 208, 'ID123', '2022-05-21', '2022-07-29'),
('123', 'Nu Milk Tea', 1, '4.500', '5.000', 466, '123', '2022-05-22', '2022-06-24'),
('12321093821', 'Teh Pucuk Harum', 1, '3.000', '5.000', 300, 'ID123', '2022-05-30', '2022-07-08'),
('321', 'Es Teh Manissssssssssssss', 1, '1.000', '2.000', 196, 'ID123', '2022-06-14', '0001-01-01'),
('4569871023', 'Oreo Manis', 3, '2.000', '3.000', 240, '123', '2022-06-13', '2022-07-09');

-- --------------------------------------------------------

--
-- Table structure for table `promotion`
--

CREATE TABLE `promotion` (
  `id` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `percentage` int(3) NOT NULL,
  `date_added` varchar(10) NOT NULL,
  `expired_date` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `promotion`
--

INSERT INTO `promotion` (`id`, `name`, `percentage`, `date_added`, `expired_date`) VALUES
('-1', '', 0, '09-04-2022', '09-04-2022');

-- --------------------------------------------------------

--
-- Table structure for table `sale`
--

CREATE TABLE `sale` (
  `id` int(11) NOT NULL,
  `transaction_id` int(11) NOT NULL,
  `barcode` varchar(20) NOT NULL,
  `quantity` int(5) NOT NULL,
  `subtotal` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `sale`
--

INSERT INTO `sale` (`id`, `transaction_id`, `barcode`, `quantity`, `subtotal`) VALUES
(1, 1, '123', 1, '5.000'),
(2, 2, '123', 1, '5.000'),
(3, 3, '123', 1, '5.000'),
(4, 4, '321', 1, '2.000'),
(5, 6, '321', 1, '2.000'),
(6, 7, '321', 5, '10.000'),
(7, 7, '123', 1, '5.000'),
(8, 8, '321', 10, '20.000'),
(9, 9, '1000', 4, '8.000'),
(10, 10, '321', 7, '14.000'),
(11, 11, '1000', 8, '16.000'),
(13, 12, '1000', 1, '2.000'),
(14, 13, '321', 1, '2.000'),
(15, 14, '321', 1, '2.000'),
(16, 15, '321', 10, '20.000'),
(17, 16, '1000', 5, '10.000');

-- --------------------------------------------------------

--
-- Table structure for table `stock`
--

CREATE TABLE `stock` (
  `id` int(11) NOT NULL,
  `barcode` varchar(20) NOT NULL,
  `previous_stock` int(5) NOT NULL DEFAULT 0,
  `qty` int(5) NOT NULL,
  `date` date NOT NULL,
  `type` enum('add','return','sale') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `stock`
--

INSERT INTO `stock` (`id`, `barcode`, `previous_stock`, `qty`, `date`, `type`) VALUES
(1, '123', 0, 20, '2022-06-13', 'add'),
(2, '4569871023', 0, 20, '2022-06-13', 'return'),
(3, '321', 0, 100, '2022-06-13', 'add'),
(4, '321', 200, 100, '2022-06-13', 'add'),
(5, '321', 207, 1, '2022-06-14', 'sale'),
(6, '321', 207, 1, '2022-06-14', 'sale'),
(7, '321', 206, 100, '2022-06-14', 'add'),
(8, '321', 306, 50, '2022-06-14', 'return'),
(9, '321', 256, 50, '2022-06-14', 'return'),
(10, '321', 206, 20, '2022-06-14', 'add'),
(11, '321', 226, 20, '2022-06-14', 'return'),
(12, '321', 206, 10, '2022-06-14', 'sale'),
(13, '1000', 397, 10, '2022-06-14', 'add'),
(14, '1000', 407, 10, '2022-06-14', 'return'),
(15, '1000', 397, 5, '2022-06-14', 'sale');

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `id` varchar(16) NOT NULL,
  `name` varchar(30) NOT NULL,
  `address` varchar(255) NOT NULL,
  `gender` varchar(11) NOT NULL,
  `phone` varchar(14) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `bank_account` varchar(30) NOT NULL,
  `account_number` varchar(25) NOT NULL,
  `status` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`id`, `name`, `address`, `gender`, `phone`, `email`, `bank_account`, `account_number`, `status`) VALUES
('123', 'Budi', 'Kuningan', 'Laki-laki', '089999999999', 'budi@gmail.com', 'BNI', '8712937912', 1),
('ID123', 'Andi', 'Cirebon', 'Laki-laki', '081234567890', 'andi@gmail.com', 'BRI', '398127832', 1);

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `date` date NOT NULL,
  `time` varchar(8) NOT NULL,
  `total_amount` int(11) NOT NULL,
  `customer_id` int(5) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`id`, `username`, `date`, `time`, `total_amount`, `customer_id`) VALUES
(1, 'kasir', '2022-05-21', '09:23:58', 5000, 0),
(2, 'kasir', '2022-05-21', '09:56:37', 5000, 0),
(3, 'kasir', '2022-05-22', '09:57:08', 5000, 0),
(4, 'kasir', '2022-06-08', '10:45:08', 2000, 0),
(6, 'kasir', '2022-06-08', '14:16:24', 2000, 0),
(7, 'andi', '2022-06-08', '15:20:33', 15000, 0),
(8, 'kasir', '2022-06-08', '15:21:41', 20000, 0),
(9, 'kasir', '2022-06-09', '13:58:51', 8000, 0),
(10, 'kasir', '2022-06-09', '13:59:45', 14000, 0),
(11, 'kasir', '2022-06-11', '14:00:53', 16000, 0),
(12, 'kasir', '2022-06-13', '18:46:08', 2000, 0),
(13, 'kasir', '2022-06-14', '08:17:38', 2000, 0),
(14, 'kasir', '2022-06-14', '08:19:40', 2000, 0),
(15, 'kasir', '2022-06-14', '10:21:43', 20000, 0),
(16, 'kasir', '2022-06-14', '10:29:26', 10000, 0);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `username` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(30) NOT NULL,
  `address` varchar(255) NOT NULL,
  `gender` varchar(11) NOT NULL,
  `phone` varchar(14) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `level` varchar(10) NOT NULL,
  `date_created` date NOT NULL,
  `status` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`username`, `password`, `name`, `address`, `gender`, `phone`, `email`, `level`, `date_created`, `status`) VALUES
('admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 'Admin', 'Cirebon', 'Laki-laki', '089999999999', '-', 'Admin', '0000-00-00', 1),
('andi', 'ed0d587073b2a487fa0638d970255179f0f4d298b33ed39317797681bb57e2277c560ffb9a3f75a81adc261d4d7cee06769380751d44e0669226d4cf042e44b0', 'Andi', 'Karangmekar, Karangsembung, Cirebon', 'Laki-laki', '081234567890', '-', 'Kasir', '0000-00-00', 1),
('feri', 'a7e4aee696782ca8708d21986a721f55b26929cd9927c3df9b4db75fe84d78842dfbbb5467d6fc7e9f80fcf3bdf6b6f13b31aacfbcafcb7a778e71d80777a513', 'Feri', 'Cirebon', 'Laki-laki', '0822222222222', '-', 'Kasir', '2022-06-13', 1),
('kasir', 'e2c23518e63445135a75cb5b39585b6a2e3f7261108674fe606c16947aa2d4e1f5ceb3766c2a2b60e93e79e6b4d267f5054f361ce4f364d2f2e95bdd7db9678d', 'Kasir', 'Kuningan', 'Perempuan', '08123456789', '-', 'Kasir', '0000-00-00', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`barcode`),
  ADD KEY `product_ibfk_3` (`supplier_id`),
  ADD KEY `product_ibfk_2` (`category_id`);

--
-- Indexes for table `promotion`
--
ALTER TABLE `promotion`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sale`
--
ALTER TABLE `sale`
  ADD PRIMARY KEY (`id`),
  ADD KEY `transaction_id` (`transaction_id`),
  ADD KEY `sale_ibfk_1` (`barcode`);

--
-- Indexes for table `stock`
--
ALTER TABLE `stock`
  ADD PRIMARY KEY (`id`),
  ADD KEY `barcode` (`barcode`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `sale`
--
ALTER TABLE `sale`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `stock`
--
ALTER TABLE `stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `product_ibfk_3` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `sale`
--
ALTER TABLE `sale`
  ADD CONSTRAINT `sale_ibfk_1` FOREIGN KEY (`barcode`) REFERENCES `product` (`barcode`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `sale_ibfk_2` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `stock`
--
ALTER TABLE `stock`
  ADD CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`barcode`) REFERENCES `product` (`barcode`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
