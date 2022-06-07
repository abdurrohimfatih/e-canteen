-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 07, 2022 at 11:23 AM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.1.1

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
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `date_created` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `date_created`) VALUES
(1, 'Minuman', '21-05-2022');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `barcode` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category_id` int(11) NOT NULL,
  `purchase_price` varchar(9) NOT NULL,
  `selling_price` varchar(9) NOT NULL,
  `stock_amount` int(11) NOT NULL,
  `supplier_id` varchar(16) NOT NULL,
  `date_added` varchar(10) NOT NULL,
  `expired_date` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`barcode`, `name`, `category_id`, `purchase_price`, `selling_price`, `stock_amount`, `supplier_id`, `date_added`, `expired_date`) VALUES
('1000', 'Kopi Panas', 1, '1.000', '2.000', 0, 'ID123', '06-06-2022', '14-06-2022'),
('10000', 'Susu Frisian Flag', 1, '1.000', '1.500', 0, 'ID123', '06-06-2022', '-'),
('123', 'Nu Milk Tea', 1, '4.500', '5.000', 117, '123', '21-05-2022', '25-06-2022'),
('321', 'Es Teh Manis', 1, '1.000', '2.000', 45, 'ID123', '06-06-2022', '17-06-2022');

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
  `quantity` int(11) NOT NULL,
  `subtotal` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `sale`
--

INSERT INTO `sale` (`id`, `transaction_id`, `barcode`, `quantity`, `subtotal`) VALUES
(1, 1, '123', 1, '5.000'),
(2, 2, '123', 1, '5.000'),
(3, 3, '123', 1, '5.000'),
(4, 4, '321', 1, '2.000');

-- --------------------------------------------------------

--
-- Table structure for table `stock`
--

CREATE TABLE `stock` (
  `id` int(11) NOT NULL,
  `barcode` varchar(20) NOT NULL,
  `qty` int(11) NOT NULL,
  `type` enum('add','return') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `stock`
--

INSERT INTO `stock` (`id`, `barcode`, `qty`, `type`) VALUES
(1, '1000', 10, 'add'),
(2, '10000', 20, 'add'),
(3, '10000', 20, 'add'),
(4, '321', 20, 'add'),
(5, '321', 20, 'add'),
(6, '1000', 20, 'add'),
(7, '1000', 10, 'add'),
(8, '10000', 10, 'add'),
(9, '1000', 20, 'add'),
(10, '1000', 10, 'add'),
(11, '321', 20, 'add'),
(12, '1000', 10, 'add'),
(13, '1000', 10, 'add'),
(14, '321', 10, 'add'),
(15, '1000', 10, 'add'),
(16, '1000', 10, 'add'),
(17, '1000', 10, 'add'),
(18, '1000', 20, 'add'),
(19, '1000', 20, 'add'),
(20, '1000', 10, 'add'),
(21, '10000', 20, 'add'),
(22, '321', 10, 'add'),
(23, '1000', 10, 'add'),
(24, '1000', 20, 'add'),
(25, '321', 20, 'add'),
(26, '1000', 20, 'add'),
(27, '321', 10, 'add'),
(28, '1000', 20, 'add'),
(29, '1000', 30, 'add'),
(30, '10000', 20, 'add'),
(31, '321', 200, 'add'),
(32, '10000', 20, 'add'),
(33, '1000', 10, 'return'),
(34, '321', 20, 'return'),
(35, '321', 1000, 'return'),
(36, '321', 500, 'add'),
(37, '321', 100, 'return'),
(38, '321', 10, 'add'),
(39, '1000', 100, 'add'),
(40, '321', 5, 'return'),
(41, '321', 20, 'add'),
(42, '321', 0, 'add'),
(43, '321', 10, 'add'),
(44, '123', 20, 'add'),
(45, '321', 10, 'add'),
(46, '1000', 20, 'return'),
(47, '1000', 80, 'return'),
(48, '1000', 100, 'add'),
(49, '1000', 100, 'return'),
(50, '1000', 100, 'add'),
(51, '1000', 80, 'return'),
(52, '1000', 10, 'return'),
(53, '1000', 90, 'add'),
(54, '10000', 100, 'add'),
(55, '1000', 100, 'return'),
(56, '10000', 100, 'return');

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
  `status` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`id`, `name`, `address`, `gender`, `phone`, `email`, `bank_account`, `account_number`, `status`) VALUES
('123', 'Budi', 'Kuningan', 'Laki-laki', '089999999999', 'budi@gmail.com', 'BNI', '8712937912', '1'),
('ID123', 'Andi', 'Cirebon', 'Laki-laki', '081234567890', 'andi@gmail.com', 'BRI', '398127832', '1');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `date` varchar(10) NOT NULL,
  `time` varchar(8) NOT NULL,
  `total_amount` varchar(12) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`id`, `username`, `date`, `time`, `total_amount`) VALUES
(1, 'kasir', '21-05-2022', '09:23:58', '5000'),
(2, 'kasir', '21-05-2022', '09:56:37', '5000'),
(3, 'kasir', '21-05-2022', '09:57:08', '5000'),
(4, 'kasir', '21-05-2022', '10:45:08', '2000');

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
  `date_created` varchar(10) NOT NULL,
  `status` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`username`, `password`, `name`, `address`, `gender`, `phone`, `email`, `level`, `date_created`, `status`) VALUES
('admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 'Administrator', 'Cirebon', 'Laki-laki', '089999999999', '-', 'Admin', '09-04-2022', '1'),
('kasir', 'e2c23518e63445135a75cb5b39585b6a2e3f7261108674fe606c16947aa2d4e1f5ceb3766c2a2b60e93e79e6b4d267f5054f361ce4f364d2f2e95bdd7db9678d', 'Kasir', 'Kuningan', 'Perempuan', '08123456789', '-', 'Kasir', '09-04-2022', '1');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `sale`
--
ALTER TABLE `sale`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `stock`
--
ALTER TABLE `stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

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
