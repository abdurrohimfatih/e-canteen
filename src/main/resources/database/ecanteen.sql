-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 07, 2022 at 01:57 PM
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
  `id` varchar(20) NOT NULL,
  `name` varchar(30) NOT NULL,
  `date_created` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `date_created`) VALUES
('CT123', 'Makanan Ringan', '2022-02-09'),
('CT321', 'Minuman Bersoda', '2022-02-25'),
('CT12', 'Minuman Panas', '2022-03-02');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `barcode` varchar(20) NOT NULL,
  `name` varchar(30) NOT NULL,
  `category_id` varchar(20) NOT NULL,
  `price` int(11) NOT NULL,
  `stock_amount` int(11) NOT NULL,
  `supplier_id` varchar(20) NOT NULL,
  `date_added` date NOT NULL,
  `expired_date` date NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`barcode`, `name`, `category_id`, `price`, `stock_amount`, `supplier_id`, `date_added`, `expired_date`, `count`) VALUES
('1234567895678456', 'Kripik Singkong', 'CT123', 1000, 100, '10', '2022-03-01', '2022-03-31', 23),
('23456789234567', 'Oreo Manis', 'CT123', 1000, 200, '16', '2022-03-02', '2023-03-04', 20),
('455678976545367', 'Kapal', 'CT12', 2000, 5, '17', '2022-03-02', '2022-04-09', 20),
('932648726293', 'Big Cola', 'CT321', 5000, 127, '4', '2022-03-02', '2022-04-08', 20),
('987064278463272', 'Kentang Goreng', 'CT123', 5000, 202, 'WGS123', '2022-03-04', '2022-04-07', 209);

-- --------------------------------------------------------

--
-- Table structure for table `promotion`
--

CREATE TABLE `promotion` (
  `id` varchar(20) NOT NULL,
  `name` varchar(30) NOT NULL,
  `product_barcode` varchar(20) NOT NULL,
  `percentage` int(11) NOT NULL,
  `description` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `promotion`
--

INSERT INTO `promotion` (`id`, `name`, `product_barcode`, `percentage`, `description`) VALUES
('798012312', 'Diskon Hari Libur', '987064278463272', 90, 'Diskon ketika hari libur'),
('DSC8762', 'Diskon Lebaran', '1234567895678456', 50, 'Diskon lebaran ini diadakan di hari lebaran');

-- --------------------------------------------------------

--
-- Table structure for table `supplier`
--

CREATE TABLE `supplier` (
  `id` varchar(20) NOT NULL,
  `name` varchar(30) NOT NULL,
  `last_supplied_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`id`, `name`, `last_supplied_date`) VALUES
('1', 'Abdurrohim', '2022-02-21'),
('10', 'PT Sentosa', '2022-02-02'),
('14', 'PT Indofood', '2022-02-09'),
('16', 'PT Nabati', '2022-02-24'),
('17', 'PT Indonesia', '2022-02-09'),
('4', 'PT Kaldu Sari', '2022-02-22'),
('SP123', 'PT Telkom', '2022-02-01'),
('WGS123', 'PT Wingsfood', '2022-02-17');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`barcode`);

--
-- Indexes for table `promotion`
--
ALTER TABLE `promotion`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
