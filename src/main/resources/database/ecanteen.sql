-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 22, 2022 at 07:22 AM
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
  `id` varchar(5) NOT NULL,
  `name` varchar(30) NOT NULL,
  `date_created` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `date_created`) VALUES
('CT123', 'Makanan Ringan', '2022-03-15'),
('CT321', 'Minuman Bersoda', '2022-02-25'),
('CT12', 'Minuman Panas', '2022-03-02'),
('K123', 'Makanan Berat', '2022-03-15');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `barcode` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category_id` varchar(5) NOT NULL,
  `purchase_price` int(7) NOT NULL,
  `selling_price` int(7) NOT NULL,
  `stock_amount` int(11) NOT NULL,
  `supplier_id` varchar(11) NOT NULL,
  `date_added` date NOT NULL,
  `expired_date` date NOT NULL,
  `promotion_id` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`barcode`, `name`, `category_id`, `purchase_price`, `selling_price`, `stock_amount`, `supplier_id`, `date_added`, `expired_date`, `promotion_id`) VALUES
('1234567895678456', 'Kripik Singkong', 'CT123', 2000, 1000, 100, '14', '2022-03-01', '2022-03-31', '4567898765'),
('23456789234567', 'Oreo Manis', 'CT123', 1000, 500, 200, '16', '2022-03-02', '2023-03-04', '4567898765'),
('455678976545367', 'Kapal Api', 'CT12', 2000, 1000, 5, '17', '2022-03-15', '2022-04-09', 'DSC8762'),
('932648726293', 'Big Cola', 'CT321', 5000, 4000, 127, '10', '2022-03-02', '2022-04-08', '4567898765'),
('987064278463272', 'Kentang Goreng', 'CT123', 5000, 4000, 202, 'WGS123', '2022-03-04', '2022-04-07', '4567898765');

-- --------------------------------------------------------

--
-- Table structure for table `promotion`
--

CREATE TABLE `promotion` (
  `id` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `percentage` int(3) NOT NULL,
  `date_added` date NOT NULL,
  `expired_date` date NOT NULL,
  `status` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `promotion`
--

INSERT INTO `promotion` (`id`, `name`, `percentage`, `date_added`, `expired_date`, `status`) VALUES
('123', 'Diskon Akhir Pekan', 30, '2022-03-16', '2022-03-02', NULL),
('4567898765', 'Diskon Idul Fitri', 70, '2022-03-15', '2022-03-14', 'Kedaluwara'),
('DSC8762', 'Diskon Lebaran', 50, '2022-03-15', '2022-03-31', 'Aktif');

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
  `account_number` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `supplier`
--

INSERT INTO `supplier` (`id`, `name`, `address`, `gender`, `phone`, `email`, `bank_account`, `account_number`) VALUES
('10', 'Andi', 'Cirebon', 'Laki-laki', '08222222222222', '-', '-', '-'),
('14', 'Budi', 'Kuningan', 'Laki-laki', '08333333333333', '-', '-', '-'),
('16', 'Coki', 'Majalengka', 'Laki-laki', '08444444444444', '-', '-', '-'),
('17', 'Dani', 'Indramayu', 'Laki-laki', '08555555555555', '-', '-', '-'),
('4', 'Eki', 'Kab. Cirebon', 'Laki-laki', '08111111111111', '', '', ''),
('SP123', 'Feri', 'Bandung', 'Laki-laki', '08777777777777', '-', '-', '-'),
('WGS123', 'Gani', 'Jakarta', 'Laki-laki', '08666666666666', '-', '-', '-');

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
  `status` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`username`, `password`, `name`, `address`, `gender`, `phone`, `email`, `level`, `date_created`, `status`) VALUES
('admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 'Administrator', 'Cirebon', 'Laki-laki', '-', '-', 'Admin', '2022-03-13', 'Aktif'),
('andi', '3c9909afec25354d551dae21590bb26e38d53f2173b8d3dc3eee4c047e7ab1c1eb8b85103e3be7ba613b31bb5c9c36214dc9f14a42fd7a2fdb84856bca5c44c2', 'Abdurrohim', 'Cirebon', 'Laki-laki', '0899999999999', 'ohim@gmail.com', 'Admin', '2022-03-15', 'Tidak Aktif'),
('kasir', 'e2c23518e63445135a75cb5b39585b6a2e3f7261108674fe606c16947aa2d4e1f5ceb3766c2a2b60e93e79e6b4d267f5054f361ce4f364d2f2e95bdd7db9678d', 'Kasir', 'Kuningan', 'Perempuan', '-', '-', 'Kasir', '2022-03-12', 'Aktif');

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

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
