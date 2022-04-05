-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 05, 2022 at 12:20 PM
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
  `date_created` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `name`, `date_created`) VALUES
(1, 'Minuman Panas', '2022-03-28'),
(2, 'Makanan Ringan', '2022-03-28'),
(6, 'Makanan Berat', '2022-03-28');

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

CREATE TABLE `product` (
  `barcode` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category_id` int(11) NOT NULL,
  `purchase_price` int(7) NOT NULL,
  `selling_price` int(7) NOT NULL,
  `stock_amount` int(11) NOT NULL,
  `supplier_id` varchar(11) NOT NULL,
  `date_added` date NOT NULL,
  `expired_date` date NOT NULL,
  `promotion_id` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`barcode`, `name`, `category_id`, `purchase_price`, `selling_price`, `stock_amount`, `supplier_id`, `date_added`, `expired_date`, `promotion_id`) VALUES
('1234567895678456', 'Kripik Singkong', 2, 2000, 1000, 100, '14', '2022-03-28', '2022-03-31', '-1'),
('23456789234567', 'Oreo Manis', 2, 1000, 500, 200, '16', '2022-03-28', '2023-03-04', '123'),
('455678976545367', 'Kapal Api', 1, 2000, 1000, 5, '17', '2022-03-26', '2022-04-09', '-1'),
('4970129727514', 'Spidol', 2, 3000, 5000, 97, '10', '2022-03-30', '2022-03-23', 'DSC8762'),
('8997214190103', 'Tissue Peipa Today', 2, 8000, 9000, 1000, 'WGS123', '2022-03-30', '2022-03-10', 'DSC8762'),
('987064278463272', 'Buku Binder Joyko', 2, 5000, 4000, 202, '10', '2022-03-29', '2022-04-07', 'DSC8762'),
('CT652', 'Barcode Scanner', 2, 50000, 60000, -41, '4', '2022-03-30', '2022-04-07', '123');

-- --------------------------------------------------------

--
-- Table structure for table `promotion`
--

CREATE TABLE `promotion` (
  `id` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `percentage` int(3) NOT NULL,
  `date_added` date NOT NULL,
  `expired_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `promotion`
--

INSERT INTO `promotion` (`id`, `name`, `percentage`, `date_added`, `expired_date`) VALUES
('-1', '', 0, '2022-03-26', '2022-03-31'),
('123', 'Diskon Akhir Pekan', 30, '2022-03-16', '2022-03-02'),
('4567898765', 'Diskon Idul Fitri', 70, '2022-03-15', '2022-03-31'),
('8923', 'Diskon Akhir Tahun', 20, '2022-03-23', '2022-03-23'),
('DSC8762', 'Diskon Lebaran', 50, '2022-03-15', '2022-03-24');

-- --------------------------------------------------------

--
-- Table structure for table `sale`
--

CREATE TABLE `sale` (
  `id` varchar(15) NOT NULL,
  `username` varchar(20) NOT NULL,
  `date` varchar(10) NOT NULL,
  `time` varchar(8) NOT NULL,
  `barcode` varchar(700) NOT NULL,
  `quantity` varchar(300) NOT NULL,
  `total_amount` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `sale`
--

INSERT INTO `sale` (`id`, `username`, `date`, `time`, `barcode`, `quantity`, `total_amount`) VALUES
('1111', 'kasir', '01/04/2022', '21:40:43', 'CT652,CT652,', '1,1,', '84000.0'),
('1112', 'kasir', '02/04/2022', '00:23:14', 'CT652,', '1,', '42000.0'),
('1113', 'kasir', '02/04/2022', '00:31:47', 'CT652,', '1,', '42000.0'),
('1114', 'kasir', '02/04/2022', '10:01:30', '4970129727514,', '1,', '2500.0'),
('1115', 'kasir', '02/04/2022', '10:02:20', '4970129727514,4970129727514,', '100,1,', '252500.0'),
('1116', 'kasir', '02/04/2022', '10:03:49', '4970129727514,CT652,', '1,1,', '44500.0'),
('1117', 'Kasir', '02/04/2022', '20:57:40', 'CT652,CT652,', '1,1,', '84000.0'),
('1118', 'Kasir', '02/04/2022', '21:06:30', 'CT652,CT652,', '1,1,', '84000.0'),
('1119', 'Kasir', '02/04/2022', '21:10:57', 'CT652,CT652,', '1,1,', '84000.0'),
('1120', 'Kasir', '02/04/2022', '21:15:11', 'CT652,CT652,', '1,1,', '84000.0'),
('1121', 'Kasir', '03/04/2022', '13:41:17', 'CT652,CT652,', '1,1,', '84000.0'),
('1122', 'Kasir', '03/04/2022', '13:48:57', 'CT652,CT652,', '1,1,', '84000.0'),
('1123', 'Kasir', '03/04/2022', '14:07:44', 'CT652,CT652,', '1,1,', '84000.0'),
('1124', 'Kasir', '03/04/2022', '16:36:46', 'CT652,CT652,', '1,1,', '84000.0'),
('1125', 'Kasir', '03/04/2022', '16:37:56', 'CT652,CT652,', '1,1,', '84000.0'),
('1126', 'Kasir', '03/04/2022', '16:41:13', 'CT652,CT652,', '1,1,', '84000.0'),
('1127', 'Kasir', '03/04/2022', '16:42:46', 'CT652,CT652,', '1,1,', '84000.0'),
('1128', 'Kasir', '03/04/2022', '16:44:49', 'CT652,', '1,', '42000.0'),
('1129', 'Kasir', '03/04/2022', '16:48:48', 'CT652,', '1,', '42000.0'),
('1130', 'Kasir', '03/04/2022', '16:55:08', 'CT652,CT652,', '1,1,', '84000.0'),
('1131', 'Kasir', '03/04/2022', '16:59:09', 'CT652,', '1,', '42000.0'),
('1132', 'Kasir', '03/04/2022', '16:59:49', 'CT652,', '1,', '42000.0'),
('1133', 'Kasir', '03/04/2022', '17:13:54', 'CT652,', '1,', '42000.0'),
('1134', 'Kasir', '03/04/2022', '17:36:08', 'CT652,CT652,', '1,1,', '84000.0'),
('1135', 'Kasir', '03/04/2022', '17:37:04', 'CT652,', '1,', '42000.0'),
('1136', 'Kasir', '03/04/2022', '17:39:05', 'CT652,CT652,', '1,1,', '84000.0'),
('1137', 'Kasir', '03/04/2022', '17:40:52', 'CT652,', '1,', '42000.0'),
('1138', 'Kasir', '03/04/2022', '17:41:29', 'CT652,', '1,', '42000.0'),
('1139', 'Kasir', '03/04/2022', '21:05:55', 'CT652,', '1,', '42000.0'),
('1140', 'Kasir', '03/04/2022', '21:10:04', 'CT652,', '5,', '210000.0'),
('1141', 'Kasir', '03/04/2022', '21:12:20', 'CT652,', '1,', '42000.0'),
('1142', 'Kasir', '03/04/2022', '21:13:25', 'CT652,', '1,', '42000.0'),
('1143', 'Kasir', '03/04/2022', '21:14:59', 'CT652,', '1,', '42000.0'),
('1144', 'Kasir', '03/04/2022', '21:15:57', 'CT652,', '1,', '42000.0'),
('1145', 'Kasir', '03/04/2022', '21:18:50', '', '', '42000.0'),
('1146', 'Kasir', '03/04/2022', '21:19:56', 'CT652,', '1,', '42000.0'),
('1147', 'Kasir', '03/04/2022', '21:23:18', 'CT652,', '1,', '42000.0'),
('1148', 'Kasir', '03/04/2022', '21:24:24', 'CT652,', '1,', '42000.0'),
('1149', 'Kasir', '03/04/2022', '21:26:01', 'CT652,', '1,', '42000.0'),
('1150', 'Kasir', '03/04/2022', '21:27:24', 'CT652,', '1,', '42000.0'),
('1151', 'Kasir', '03/04/2022', '21:36:50', 'CT652,', '1,', '42000.0'),
('1152', 'Kasir', '03/04/2022', '21:39:19', 'CT652,', '1,', '42000.0'),
('1153', 'Kasir', '03/04/2022', '21:39:54', 'CT652,CT652,', '1,1,', '84000.0'),
('1154', 'Kasir', '03/04/2022', '21:41:29', 'CT652,', '1,', '42000.0'),
('1155', 'Kasir', '03/04/2022', '21:42:13', 'CT652,', '1,', '42000.0'),
('1156', 'Kasir', '03/04/2022', '21:43:14', 'CT652,', '1,', '42000.0'),
('1157', 'Kasir', '03/04/2022', '22:06:40', 'CT652,', '1,', '42000.0'),
('1158', 'Kasir', '03/04/2022', '22:09:09', 'CT652,', '1,', '42000.0'),
('1159', 'Kasir', '03/04/2022', '22:26:59', 'CT652,', '1,', '42000.0'),
('1160', 'Kasir', '03/04/2022', '22:38:00', 'CT652,', '1,', '42000.0'),
('1161', 'Kasir', '03/04/2022', '22:47:03', 'CT652,', '1,', '42000.0'),
('1162', 'Kasir', '03/04/2022', '22:48:18', 'CT652,', '1,', '42000.0'),
('1163', 'Kasir', '03/04/2022', '22:49:52', 'CT652,', '1,', '42000.0'),
('1164', 'Kasir', '03/04/2022', '22:52:30', 'CT652,', '1,', '42000.0'),
('1165', 'Kasir', '03/04/2022', '22:57:39', 'CT652,', '1,', '42000.0'),
('1166', 'Kasir', '03/04/2022', '23:00:54', 'CT652,', '1,', '42000.0'),
('1167', 'Kasir', '03/04/2022', '23:05:52', 'CT652,CT652,', '1,1,', '84000.0'),
('1168', 'Kasir', '03/04/2022', '23:10:23', 'CT652,', '1,', '42000.0'),
('1169', 'Kasir', '04/04/2022', '10:44:12', 'CT652,', '1,', '42000.0'),
('1170', 'Kasir', '04/04/2022', '10:48:45', 'CT652,', '1,', '42000.0'),
('1171', 'Kasir', '04/04/2022', '10:52:16', 'CT652,', '1,', '42000.0'),
('1172', 'Kasir', '04/04/2022', '10:55:13', 'CT652,', '1,', '42000.0'),
('1173', 'Kasir', '04/04/2022', '11:04:01', 'CT652,', '1,', '42000.0'),
('1174', 'Kasir', '04/04/2022', '11:05:22', 'CT652,CT652,', '1,1,', '84000.0'),
('1175', 'Kasir', '04/04/2022', '11:07:49', 'CT652,', '1,', '42000.0'),
('1176', 'Kasir', '04/04/2022', '11:09:17', 'CT652,CT652,', '1,1,', '84000.0'),
('1177', 'Kasir', '04/04/2022', '11:54:17', 'CT652,', '1,', '42000.0'),
('1178', 'Kasir', '04/04/2022', '12:07:14', 'CT652,', '1,', '42000.0'),
('1179', 'Kasir', '04/04/2022', '12:07:48', 'CT652,', '1,', '42000.0'),
('1180', 'Kasir', '04/04/2022', '12:26:50', 'CT652,', '1,', '42000.0'),
('1181', 'Kasir', '04/04/2022', '12:52:38', 'CT652,', '1,', '42000.0'),
('1182', 'Kasir', '04/04/2022', '12:56:06', 'CT652,', '1,', '42000.0'),
('1183', 'Kasir', '04/04/2022', '13:00:04', 'CT652,', '1,', '42000.0'),
('1184', 'Kasir', '04/04/2022', '13:01:24', 'CT652,', '1,', '42000.0'),
('1185', 'Kasir', '04/04/2022', '13:02:38', 'CT652,', '1,', '42000.0'),
('1186', 'Kasir', '04/04/2022', '13:04:22', 'CT652,', '1,', '42000.0'),
('1187', 'Kasir', '04/04/2022', '13:16:08', 'CT652,', '1,', '42000.0'),
('1188', 'Kasir', '04/04/2022', '13:18:46', 'CT652,', '1,', '42000.0'),
('1189', 'Kasir', '04/04/2022', '16:25:53', 'CT652,', '1,', '42000.0'),
('1190', 'Kasir', '04/04/2022', '16:29:01', 'CT652,', '1,', '42000.0'),
('1191', 'Kasir', '04/04/2022', '16:39:59', 'CT652,', '1,', '42000.0'),
('1192', 'Kasir', '04/04/2022', '16:44:01', 'CT652,', '1,', '42000.0'),
('1193', 'Kasir', '04/04/2022', '16:45:41', 'CT652,', '1,', '42000.0'),
('1194', 'Kasir', '04/04/2022', '16:46:39', 'CT652,', '1,', '42000.0'),
('1195', 'Kasir', '04/04/2022', '16:49:48', 'CT652,', '1,', '42000.0'),
('1196', 'Kasir', '04/04/2022', '16:59:47', 'CT652,', '1,', '42000.0'),
('1197', 'Kasir', '04/04/2022', '17:03:00', 'CT652,', '1,', '42000.0'),
('1198', 'Kasir', '04/04/2022', '17:06:08', 'CT652,', '1,', '42000.0'),
('1199', 'Kasir', '04/04/2022', '17:07:13', 'CT652,', '1,', '42000.0'),
('1200', 'Kasir', '04/04/2022', '17:10:11', 'CT652,', '1,', '42000.0'),
('1201', 'Kasir', '04/04/2022', '17:11:48', 'CT652,', '1,', '42000.0'),
('1202', 'Kasir', '04/04/2022', '17:13:16', 'CT652,', '1,', '42000.0'),
('1203', 'Kasir', '04/04/2022', '17:18:57', 'CT652,', '1,', '42000.0'),
('1204', 'Kasir', '04/04/2022', '17:29:03', 'CT652,', '1,', '42000.0'),
('1205', 'Kasir', '04/04/2022', '17:31:01', 'CT652,', '1,', '42000.0'),
('1206', 'Kasir', '04/04/2022', '20:44:54', 'CT652,', '1,', '42000.0'),
('1207', 'Kasir', '04/04/2022', '20:49:43', 'CT652,', '1,', '42000.0'),
('1208', 'Kasir', '04/04/2022', '21:57:47', 'CT652,', '1,', '42000.0'),
('1209', 'Kasir', '04/04/2022', '22:00:45', 'CT652,', '1,', '42000.0'),
('1210', 'Kasir', '04/04/2022', '22:03:06', 'CT652,', '1,', '42000.0'),
('1211', 'Kasir', '04/04/2022', '22:04:54', 'CT652,CT652,', '1,1,', '84000.0'),
('1212', 'Kasir', '04/04/2022', '22:06:25', 'CT652,', '1,', '42000.0'),
('1213', 'Kasir', '04/04/2022', '22:16:18', 'CT652,', '1,', '42000.0'),
('1214', 'Kasir', '04/04/2022', '22:17:13', 'CT652,', '1,', '42000.0'),
('1215', 'Kasir', '04/04/2022', '22:20:58', 'CT652,', '1,', '42000.0'),
('1216', 'Kasir', '04/04/2022', '22:21:37', 'CT652,', '1,', '42000.0'),
('1217', 'Kasir', '04/04/2022', '22:24:15', 'CT652,CT652,', '1,1,', '84000.0'),
('1218', 'Kasir', '04/04/2022', '22:26:37', 'CT652,', '1,', '42000.0'),
('1219', 'Kasir', '05/04/2022', '15:29:53', 'CT652,', '1,', '42000.0'),
('1220', 'Kasir', '05/04/2022', '15:45:55', 'CT652,', '1,', '42000.0'),
('1221', 'Kasir', '05/04/2022', '15:46:46', 'CT652,CT652,', '1,1,', '84000.0'),
('1222', 'Kasir', '05/04/2022', '15:47:47', 'CT652,CT652,', '1,1,', '84000.0'),
('1223', 'Kasir', '05/04/2022', '15:50:21', 'CT652,', '1,', '42000.0'),
('1224', 'Kasir', '05/04/2022', '15:56:51', 'CT652,', '1,', '42000.0'),
('1225', 'Kasir', '05/04/2022', '16:05:21', 'CT652,', '1,', '42000.0'),
('1226', 'Kasir', '05/04/2022', '16:20:34', 'CT652,', '1,', '42000.0'),
('1227', 'Kasir', '05/04/2022', '16:21:52', 'CT652,', '1,', '42000.0'),
('1228', 'Kasir', '05/04/2022', '16:22:33', 'CT652,', '1,', '42000.0'),
('1229', 'Kasir', '05/04/2022', '16:23:52', 'CT652,', '1,', '42000.0'),
('1230', 'Kasir', '05/04/2022', '16:52:58', 'CT652,', '1,', '42000.0'),
('1231', 'Kasir', '05/04/2022', '16:53:47', 'CT652,', '1,', '42000.0');

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
('10', 'Andi', 'Cirebon', 'Laki-laki', '08222222222222', '-', '-', '-', '1'),
('14', 'Budi', 'Kuningan', 'Laki-laki', '08333333333333', '-', '-', '-', '1'),
('16', 'Coki', 'Majalengka', 'Laki-laki', '08444444444444', '-', '-', '-', '0'),
('17', 'Dani', 'Indramayu', 'Laki-laki', '08555555555555', '-', '-', '-', '1'),
('4', 'Eki', 'Kab. Cirebon', 'Laki-laki', '08111111111111', '-', '-', '-', '1'),
('SP123', 'Feri', 'Bandung', 'Laki-laki', '08777777777777', '-', '-', '-', '1'),
('WGS123', 'Gani', 'Jakarta', 'Laki-laki', '08666666666666', '-', '-', '-', '0');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `id` varchar(15) NOT NULL,
  `username` varchar(20) NOT NULL,
  `id_sale` varchar(15) NOT NULL,
  `total_amount` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
  `status` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`username`, `password`, `name`, `address`, `gender`, `phone`, `email`, `level`, `date_created`, `status`) VALUES
('admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', 'Administrator', 'Cirebon', 'Laki-laki', '-', '-', 'Admin', '2022-03-13', '1'),
('andi', 'ed0d587073b2a487fa0638d970255179f0f4d298b33ed39317797681bb57e2277c560ffb9a3f75a81adc261d4d7cee06769380751d44e0669226d4cf042e44b0', 'Andi', 'Cirebon', 'Laki-laki', '0899999999999', 'andi@gmail.com', 'Kasir', '2022-03-15', '0'),
('kasir', 'e2c23518e63445135a75cb5b39585b6a2e3f7261108674fe606c16947aa2d4e1f5ceb3766c2a2b60e93e79e6b4d267f5054f361ce4f364d2f2e95bdd7db9678d', 'Kasir', 'Kuningan', 'Perempuan', '-', '-', 'Kasir', '2022-03-12', '1');

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
  ADD PRIMARY KEY (`barcode`);

--
-- Indexes for table `promotion`
--
ALTER TABLE `promotion`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sale`
--
ALTER TABLE `sale`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`id`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
