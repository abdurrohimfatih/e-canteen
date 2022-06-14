package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Stock;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDaoImpl implements DaoService<Stock> {
    @Override
    public List<Stock> fetchAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public int addData(Stock object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO stock(id, barcode, previous_stock, qty, date, type) VALUES(?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());
                ps.setString(2, object.getBarcode());
                ps.setInt(3, object.getPreviousStock());
                ps.setInt(4, object.getQty());
                ps.setString(5, object.getDate());
                ps.setString(6, object.getType());

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    @Override
    public int updateData(Stock object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE stock SET barcode = ?, previous_stock = ?, qty = ?, date = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getBarcode());
                ps.setInt(2, object.getPreviousStock());
                ps.setInt(3, object.getQty());
                ps.setString(4, object.getDate());
                ps.setInt(5, object.getId());

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    @Override
    public int deleteData(Stock object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM stock WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    public int getNowId() throws SQLException, ClassNotFoundException {
        int nowId = 1;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id FROM stock ORDER BY id DESC LIMIT 1";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        nowId = rs.getInt("id") + 1;
                    }
                }
            }
        }

        return nowId;
    }

    public List<Stock> fetchStocksReport(String date) throws SQLException, ClassNotFoundException {
        List<Stock> stocks = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String queryProduct = "SELECT p.barcode, p.name FROM product p JOIN stock st ON p.barcode = st.barcode WHERE st.date = ? GROUP BY p.barcode, p.name ORDER BY p.name";

            String queryPreviousStock = "SELECT previous_stock FROM stock WHERE barcode = ? AND date < ? ORDER BY id DESC LIMIT 1";

            String queryStock = "SELECT SUM(qty) AS qty FROM stock WHERE barcode = ? AND date = ? AND type = ?";

            try (PreparedStatement ps = connection.prepareStatement(queryProduct)) {
                ps.setString(1, date);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));

                        Stock stock = new Stock();
                        stock.setProduct(product);
                        stock.setBarcode(product.getBarcode());
                        stock.setName(product.getName());

                        try (PreparedStatement ps2 = connection.prepareStatement(queryPreviousStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, date);

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setPreviousStock(rs2.getInt("previous_stock"));
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, date);
                            ps2.setString(3, "add");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setAdded(rs2.getInt("qty"));
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, date);
                            ps2.setString(3, "sale");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setSold(rs2.getInt("qty"));
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, date);
                            ps2.setString(3, "return");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setReturned(rs2.getInt("qty"));
                                }
                            }
                        }

                        int added = stock.getAdded();
                        int sold = stock.getSold();
                        int returned = stock.getReturned();
                        int previousStock = stock.getPreviousStock();
                        int subtotal = previousStock + added - sold - returned;

                        stock.setSubtotal(subtotal);

                        stocks.add(stock);
                    }
                }
            }
        }

        return stocks;
    }

    public List<Stock> fetchStocksRecap(String fromDate, String toDate) throws SQLException, ClassNotFoundException {
        List<Stock> stocks = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String queryProduct = "SELECT st.date, p.barcode, p.name FROM product p JOIN stock st ON p.barcode = st.barcode WHERE st.date >= ? AND st.date <= ? GROUP BY p.barcode, p.name, st.date ORDER BY p.name";

            String queryPreviousStock = "SELECT previous_stock FROM stock WHERE barcode = ? AND date < ? ORDER BY id DESC LIMIT 1";

            String queryStock = "SELECT SUM(qty) AS qty FROM stock WHERE barcode = ? AND date = ? AND type = ?";

            try (PreparedStatement ps = connection.prepareStatement(queryProduct)) {
                ps.setString(1, fromDate);
                ps.setString(2, toDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));

                        Stock stock = new Stock();
                        stock.setProduct(product);
                        stock.setBarcode(product.getBarcode());
                        stock.setName(product.getName());
                        stock.setDate(rs.getString("date"));

                        try (PreparedStatement ps2 = connection.prepareStatement(queryPreviousStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, stock.getDate());

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setPreviousStock(rs2.getInt("previous_stock"));
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, stock.getDate());
                            ps2.setString(3, "add");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setAdded(rs2.getInt("qty"));
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, stock.getDate());
                            ps2.setString(3, "sale");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setSold(rs2.getInt("qty"));
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryStock)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, stock.getDate());
                            ps2.setString(3, "return");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setReturned(rs2.getInt("qty"));
                                }
                            }
                        }

                        int added = stock.getAdded();
                        int sold = stock.getSold();
                        int returned = stock.getReturned();
                        int previousStock = stock.getPreviousStock();
                        int subtotal = previousStock + added - sold - returned;

                        stock.setSubtotal(subtotal);

                        stocks.add(stock);
                    }
                }
            }
        }

        return stocks;
    }
}