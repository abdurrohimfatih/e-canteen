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
            String query = "INSERT INTO stock(id, barcode, old_stock, qty, date, type) VALUES(?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());
                ps.setString(2, object.getProduct().getBarcode());
                ps.setInt(3, object.getOldStock());
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
            String query = "UPDATE stock SET barcode = ?, qty = ?, date = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getProduct().getBarcode());
                ps.setInt(2, object.getQty());
                ps.setString(3, object.getDate());
                ps.setInt(4, object.getId());

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

    public List<Stock> fetchStocks(String date) throws SQLException, ClassNotFoundException {
        List<Stock> stocks = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
//            String queryProduct = "SELECT p.barcode, p.name, p.stock_amount FROM product p JOIN stock st ON p.barcode = st.barcode JOIN sale sa ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE st.date = ? OR t.date = ? GROUP BY p.name ORDER BY p.name";
            String queryProduct = "SELECT p.barcode, p.name, p.stock_amount FROM product p ORDER BY p.name";

            String queryStock = "SELECT SUM(st.qty) AS qty FROM stock st WHERE st.barcode = ? AND st.date = ? AND st.type = ?";

            String querySale = "SELECT SUM(sa.quantity) AS quantity FROM sale sa JOIN transaction t on t.id = sa.transaction_id WHERE sa.barcode = ? AND t.date = ?";

            String queryOldStock = "";

            try (PreparedStatement ps = connection.prepareStatement(queryProduct)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));
                        product.setStockAmount(rs.getInt("stock_amount"));

                        Stock stock = new Stock();
                        stock.setProduct(product);

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

                        try (PreparedStatement ps2 = connection.prepareStatement(querySale)) {
                            ps2.setString(1, stock.getProduct().getBarcode());
                            ps2.setString(2, date);

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    stock.setSold(rs2.getInt("quantity"));
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
                        int subtotal = stock.getProduct().getStockAmount();
                        int oldStock = subtotal + sold + returned - added;

                        stock.setOldStock(oldStock);
                        stock.setSubtotal(subtotal);

                        stocks.add(stock);
                    }
                }
            }
        }

        return stocks;
    }
}