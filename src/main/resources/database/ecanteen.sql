-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 14, 2022 at 12:17 AM
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
(3, 'Makanan', '2022-06-17'),
(4, 'Sembako', '2022-07-01');

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
('1000', 'Kopi Panas', 1, '1.000', '2.000', 3306, 'ID123', '2022-05-21', '0001-01-01'),
('10000', 'Susu Frisian Flag', 1, '1.000', '1.500', 22, 'ID123', '2022-05-21', '0001-01-01'),
('123', 'Nu Milk Tea', 1, '4.500', '5.000', 3252, '123', '2022-05-22', '0001-01-01'),
('12321093821', 'Teh Pucuk Harum', 1, '3.000', '5.000', 211, 'ID123', '2022-05-30', '0001-01-01'),
('2343', 'Mountoya Cup', 1, '20.800', '22.000', 31, 'ID123', '2022-07-01', '2023-06-03'),
('321', 'Kopi Luwak', 1, '1.000', '2.000', 281, 'ID123', '2022-07-01', '0001-01-01'),
('456', 'Beras', 3, '8.650', '9.500', 123, 'ID123', '2022-07-01', '0001-01-01'),
('4569871023', 'Oreo Manis', 3, '2.000', '3.000', 1215, '123', '2022-06-13', '0001-01-01');

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
(1, 1, '321', 1, '2.000'),
(2, 2, '1000', 1, '2.000'),
(3, 2, '321', 1, '2.000'),
(4, 2, '4569871023', 1, '3.000'),
(5, 3, '1000', 6, '12.000'),
(6, 5, '321', 1, '2.000'),
(7, 5, '123', 1, '5.000'),
(8, 6, '4569871023', 1, '3.000'),
(9, 6, '1000', 1, '2.000'),
(10, 7, '123', 20, '100.000'),
(11, 7, '123', 1, '5.000'),
(12, 7, '123', 1, '5.000'),
(13, 7, '123', 1, '5.000'),
(14, 7, '123', 1, '5.000'),
(15, 7, '123', 1, '5.000'),
(16, 7, '123', 1, '5.000'),
(17, 7, '123', 1, '5.000'),
(18, 7, '123', 1, '5.000'),
(19, 7, '123', 1, '5.000'),
(20, 8, '123', 10, '50.000'),
(21, 9, '321', 2, '4.000'),
(22, 10, '321', 3, '6.000'),
(23, 11, '321', 10, '20.000'),
(24, 12, '321', 2, '4.000'),
(25, 13, '321', 1, '2.000'),
(26, 14, '321', 1, '2.000'),
(27, 15, '321', 1, '2.000'),
(28, 16, '321', 1, '2.000'),
(29, 17, '321', 1, '2.000'),
(30, 18, '321', 1, '2.000'),
(31, 19, '321', 1, '2.000'),
(32, 20, '321', 1, '2.000'),
(33, 21, '321', 2, '4.000'),
(34, 21, '123', 1, '5.000'),
(35, 22, '123', 100, '500.000'),
(36, 22, '321', 1, '2.000'),
(37, 23, '123', 1, '5.000'),
(38, 24, '2343', 15, '330.000'),
(39, 25, '2343', 2, '44.000');

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
(1, '321', 0, 100, '2022-06-15', 'add'),
(2, '321', 100, 20, '2022-06-15', 'return'),
(3, '321', 80, 1, '2022-06-15', 'sale'),
(4, '1000', 0, 100, '2022-06-16', 'add'),
(5, '321', 79, 20, '2022-06-16', 'return'),
(6, '4569871023', 0, 20, '2022-06-16', 'add'),
(7, '1000', 100, 1, '2022-06-16', 'sale'),
(8, '321', 59, 1, '2022-06-16', 'sale'),
(9, '4569871023', 20, 1, '2022-06-16', 'sale'),
(10, '1000', 99, 100, '2022-06-16', 'add'),
(11, '123', 0, 200, '2022-06-16', 'add'),
(12, '1000', 199, 20, '2022-06-16', 'return'),
(13, '123', 200, 20, '2022-06-16', 'return'),
(14, '123', 180, 10, '2022-06-16', 'add'),
(15, '1000', 179, 6, '2022-06-17', 'sale'),
(16, '321', 58, 1, '2022-06-17', 'sale'),
(17, '123', 190, 1, '2022-06-17', 'sale'),
(18, '321', 57, 1, '2022-06-17', 'sale'),
(19, '123', 189, 1, '2022-06-17', 'sale'),
(20, '4569871023', 19, 1, '2022-06-20', 'sale'),
(21, '1000', 173, 1, '2022-06-20', 'sale'),
(22, '123', 188, 20, '2022-06-20', 'sale'),
(23, '123', 168, 1, '2022-06-20', 'sale'),
(24, '123', 167, 1, '2022-06-20', 'sale'),
(25, '123', 166, 1, '2022-06-20', 'sale'),
(26, '123', 165, 1, '2022-06-20', 'sale'),
(27, '123', 164, 1, '2022-06-20', 'sale'),
(28, '123', 163, 1, '2022-06-20', 'sale'),
(29, '123', 162, 1, '2022-06-20', 'sale'),
(30, '123', 161, 1, '2022-06-20', 'sale'),
(31, '123', 160, 1, '2022-06-20', 'sale'),
(32, '123', 159, 10, '2022-06-22', 'sale'),
(33, '321', 56, 2, '2022-06-22', 'sale'),
(34, '321', 54, 3, '2022-06-22', 'sale'),
(35, '321', 51, 10, '2022-06-22', 'sale'),
(36, '321', 41, 2, '2022-06-22', 'sale'),
(37, '321', 39, 1, '2022-06-22', 'sale'),
(38, '321', 38, 1, '2022-06-22', 'sale'),
(39, '321', 37, 1, '2022-06-22', 'sale'),
(40, '321', 36, 1, '2022-06-22', 'sale'),
(41, '321', 35, 1, '2022-06-22', 'sale'),
(42, '321', 34, 1, '2022-06-22', 'sale'),
(43, '321', 33, 1, '2022-06-22', 'sale'),
(44, '321', 32, 1, '2022-06-22', 'sale'),
(45, '321', 31, 2, '2022-06-22', 'sale'),
(46, '123', 149, 1, '2022-06-22', 'sale'),
(47, '123', 148, 100, '2022-06-22', 'sale'),
(48, '321', 29, 1, '2022-06-22', 'sale'),
(49, '1000', 172, 2000, '2022-06-22', 'add'),
(50, '321', 28, 123, '2022-06-22', 'add'),
(51, '123', 48, 1000, '2022-06-22', 'add'),
(52, '1000', 2172, 1000, '2022-06-22', 'add'),
(53, '123', 1048, 1000, '2022-06-22', 'add'),
(54, '4569871023', 18, 1000, '2022-06-22', 'add'),
(55, '1000', 3172, 100, '2022-06-22', 'add'),
(56, '4569871023', 1018, 200, '2022-06-22', 'add'),
(57, '4569871023', 1218, 20, '2022-06-22', 'add'),
(58, '123', 2048, 20, '2022-06-22', 'add'),
(59, '123', 2068, 20, '2022-06-22', 'add'),
(60, '10000', 0, 10, '2022-06-22', 'add'),
(61, '12321093821', 0, 10, '2022-06-22', 'add'),
(62, '123', 2088, 10, '2022-06-22', 'add'),
(63, '123', 2098, 10, '2022-06-22', 'add'),
(64, '1000', 3272, 20, '2022-06-22', 'add'),
(65, '123', 2108, 20, '2022-06-22', 'add'),
(66, '123', 2108, 20, '2022-06-22', 'add'),
(67, '123', 2148, 100, '2022-06-22', 'add'),
(68, '123', 2248, 10, '2022-06-22', 'add'),
(69, '123', 2258, 10, '2022-06-22', 'add'),
(70, '4569871023', 1238, 20, '2022-06-22', 'add'),
(71, '10000', 10, 10, '2022-06-22', 'return'),
(72, '12321093821', 10, 100, '2022-06-22', 'add'),
(73, '12321093821', 110, 100, '2022-06-22', 'return'),
(74, '123', 2268, 20, '2022-06-22', 'return'),
(75, '123', 2268, 100, '2022-06-22', 'return'),
(76, '1000', 3292, 10, '2022-06-23', 'return'),
(77, '4569871023', 1258, 123, '2022-06-23', 'return'),
(78, '4569871023', 1135, 123, '2022-06-23', 'return'),
(79, '321', 151, 120, '2022-06-23', 'add'),
(80, '123', 2148, 1000, '2022-06-23', 'add'),
(81, '12321093821', 10, 200, '2022-06-23', 'add'),
(82, '4569871023', 1012, 200, '2022-06-23', 'add'),
(83, '1000', 3282, 20, '2022-06-23', 'add'),
(84, '10000', 0, 20, '2022-06-23', 'add'),
(85, '123', 3148, 100, '2022-06-23', 'add'),
(86, '321', 271, 1, '2022-06-23', 'add'),
(87, '1000', 3302, 1, '2022-06-23', 'add'),
(88, '123', 3248, 1, '2022-06-23', 'add'),
(89, '4569871023', 1212, 1, '2022-06-23', 'add'),
(90, '10000', 20, 1, '2022-06-23', 'add'),
(91, '12321093821', 210, 1, '2022-06-23', 'add'),
(92, '321', 271, 1, '2022-06-23', 'add'),
(93, '1000', 3302, 1, '2022-06-23', 'add'),
(94, '321', 271, 1, '2022-06-23', 'add'),
(95, '321', 271, 1, '2022-06-23', 'add'),
(96, '321', 271, 1, '2022-06-23', 'add'),
(97, '123', 3248, 1, '2022-06-23', 'add'),
(98, '321', 271, 1, '2022-06-23', 'add'),
(99, '123', 3248, 1, '2022-06-23', 'add'),
(100, '321', 271, 1, '2022-06-23', 'add'),
(101, '123', 3248, 1, '2022-06-23', 'add'),
(102, '10000', 20, 1, '2022-06-23', 'add'),
(103, '1000', 3302, 1, '2022-06-23', 'add'),
(104, '321', 271, 1, '2022-06-23', 'add'),
(105, '123', 3248, 1, '2022-06-23', 'add'),
(106, '4569871023', 1212, 1, '2022-06-23', 'add'),
(107, '1000', 3302, 1, '2022-06-23', 'add'),
(108, '321', 271, 1, '2022-06-23', 'add'),
(109, '4569871023', 1212, 1, '2022-06-23', 'add'),
(110, '321', 271, 1, '2022-06-23', 'add'),
(111, '123', 3253, 1, '2022-06-24', 'sale'),
(112, '2343', 0, 50, '2022-07-01', 'add'),
(113, '2343', 50, 2, '2022-07-01', 'return'),
(114, '2343', 48, 15, '2022-07-01', 'sale'),
(115, '2343', 33, 2, '2022-07-01', 'sale'),
(116, '456', 0, 123, '2022-07-13', 'add');

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `id` varchar(16) NOT NULL,
  `name` varchar(30) NOT NULL,
  `address` varchar(15) NOT NULL,
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
  `customer_id` int(5) NOT NULL DEFAULT 0,
  `limit_id` int(5) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`id`, `username`, `date`, `time`, `total_amount`, `customer_id`, `limit_id`) VALUES
(1, 'sinta', '2022-06-15', '14:45:26', 2000, 0, 0),
(2, 'sinta', '2022-06-16', '15:16:46', 7000, 0, 0),
(3, 'sinta', '2022-06-17', '20:54:39', 12000, 0, 0),
(4, 'sinta', '2022-06-17', '21:13:46', 7000, 0, 0),
(5, 'sinta', '2022-06-17', '21:13:56', 7000, 0, 0),
(6, 'sinta', '2022-06-20', '10:00:36', 5000, 0, 0),
(7, 'sinta', '2022-06-20', '14:53:08', 145000, 0, 0),
(8, 'kasir', '2022-06-22', '13:46:47', 50000, 0, 0),
(9, 'kasir', '2022-06-22', '13:47:21', 4000, 0, 0),
(10, 'kasir', '2022-06-22', '13:48:26', 6000, 0, 0),
(11, 'kasir', '2022-06-22', '13:49:19', 20000, 0, 0),
(12, 'erika', '2022-06-22', '13:53:14', 4000, 0, 0),
(13, 'sinta', '2022-06-22', '14:18:05', 2000, 0, 0),
(14, 'sinta', '2022-06-22', '14:19:31', 2000, 0, 0),
(15, 'sinta', '2022-06-22', '14:21:45', 2000, 0, 0),
(16, 'sinta', '2022-06-22', '14:22:51', 2000, 0, 0),
(17, 'sinta', '2022-06-22', '14:23:56', 2000, 0, 0),
(18, 'sinta', '2022-06-22', '14:24:39', 2000, 0, 0),
(19, 'sinta', '2022-06-22', '14:25:32', 2000, 0, 0),
(20, 'sinta', '2022-06-22', '14:31:54', 2000, 0, 0),
(21, 'kasir', '2022-06-22', '15:07:19', 9000, 0, 0),
(22, 'kasir', '2022-06-22', '15:42:21', 502000, 0, 0),
(23, 'kasir', '2022-06-24', '09:54:41', 5000, 0, 0),
(24, 'kasir', '2022-07-01', '17:13:19', 330000, 0, 0),
(25, 'kasir', '2022-07-01', '17:14:37', 44000, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `username` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(30) NOT NULL,
  `address` varchar(15) NOT NULL,
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
('admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 'Nur Siti Fatimah', 'Cirebon', 'Laki-laki', '089999999999', '-', 'Admin', '0000-00-00', 1),
('erika', 'be65c8af71949aec8e42b79e17fa3ef5e70c972a6248104c5649547a595771cd4d8051332366d979cdd00dd54834cabdfbc6246ea03859408c738e84e55cf018', 'Erika Nurjanah', 'Bandung', 'Perempuan', '081234567890', '-', 'Kasir', '0000-00-00', 1),
('gina', '7ece985a1fef494d8d7664a2d3405c0e68c4fd71538a71767fe5254a4eb5021af43a39f822e707f518b5aa0c2343ca3ebcd96ee768d2796653b1b0209581b934', 'Gina Maulani Habibah', 'Bandung', 'Perempuan', '0777777777777', '-', 'Kasir', '2022-06-16', 0),
('kasir', 'e2c23518e63445135a75cb5b39585b6a2e3f7261108674fe606c16947aa2d4e1f5ceb3766c2a2b60e93e79e6b4d267f5054f361ce4f364d2f2e95bdd7db9678d', 'Kasir', 'Cirebon', 'Perempuan', '0811111111111', '-', 'Kasir', '2022-06-20', 1),
('nurul', '2eeb4ad2e7135e671dc0f7f7e14bdc6446ecefb6aba91d5d0ecaa238fe2f7de136b803fd09c1dffb62a02e1903a86368e7fbfa7ea200d58a5ea733722ced2fc8', 'Nurul Siti Awaliyah', 'Bandung', 'Perempuan', '0822222222222', '-', 'Kasir', '2022-06-13', 1),
('sinta', '38562b58706469b79113ee3a7a1540edcb41f36806e6155ef209fd4fa9036c1c00b5a81b8c62d84375a4405798d72b5e965e328b1deba512b9a64cef7e810600', 'Sinta Aulia', 'Kuningan', 'Perempuan', '08123456789', '-', 'Kasir', '0000-00-00', 1);

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
  MODIFY `id` int(5) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `sale`
--
ALTER TABLE `sale`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `stock`
--
ALTER TABLE `stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=117;

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
