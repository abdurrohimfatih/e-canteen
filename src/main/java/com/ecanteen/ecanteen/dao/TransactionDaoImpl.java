package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.entities.Transaction;
import com.ecanteen.ecanteen.entities.User;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements DaoService<Transaction> {
    public int getNowSaleId() throws SQLException, ClassNotFoundException {
        int nowSaleId = 1;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id FROM transaction ORDER BY id DESC LIMIT 1";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        nowSaleId = Integer.parseInt(rs.getString("id")) + 1;
                    }
                }
            }
        }

        return nowSaleId;
    }

    @Override
    public int addData(Transaction object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO transaction(id, username, date, time, total_amount) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getUsername());
                ps.setString(3, object.getDate());
                ps.setString(4, object.getTime());
                ps.setString(5, object.getTotalAmount());

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
    public List<Transaction> fetchAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public int updateData(Transaction object) throws SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public int deleteData(Transaction object) throws SQLException, ClassNotFoundException {
        return 0;
    }

    public void addSale(ObservableList<Sale> sales, String transactionId) throws SQLException, ClassNotFoundException {
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO sale (transaction_id, barcode, quantity, subtotal) VALUES (?, ?, ?, ?)";

            for (Sale item : sales) {
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, transactionId);
                    ps.setString(2, item.getBarcode());
                    ps.setInt(3, item.getQuantity());
                    ps.setString(4, item.getSubtotal());

                    if (ps.executeUpdate() == 1) {
                        connection.commit();
                    } else {
                        connection.rollback();
                    }
                }
            }
        }
    }

    public boolean getProductInSale(String barcode) throws SQLException, ClassNotFoundException {
        boolean result = false;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT * FROM sale WHERE barcode = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, barcode);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    public static int getTransactionAmount(User object) throws SQLException, ClassNotFoundException {
        int transactionAmount = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT COUNT(*) AS amount FROM transaction WHERE username = ? GROUP BY username";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getUsername());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        transactionAmount = rs.getInt("amount");
                    }
                }
            }
        }

        return transactionAmount;
    }

    public List<String> getDateReport(String month) throws SQLException, ClassNotFoundException {
        List<String> date = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT DISTINCT date FROM transaction WHERE date LIKE ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, '%' + month);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        date.add(rs.getString("date"));
                    }
                }
            }
        }

        return date;
    }

    public double getTotalAmount(String date) throws SQLException, ClassNotFoundException {
        double totalAmount = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT SUM(total_amount) as total FROM transaction WHERE date = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        totalAmount = Double.parseDouble(rs.getString("total"));
                    }
                }
            }
        }

        return totalAmount;
    }

    public List<String> getSoldProductName(String month) throws SQLException, ClassNotFoundException {
        List<String> soldProduct = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT DISTINCT p.name FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE t.date LIKE ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, '%' + month);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        soldProduct.add(rs.getString("name"));
                    }
                }
            }
        }

        return soldProduct;
    }

    public int getSoldProductQty(String name, String month) throws SQLException, ClassNotFoundException {
        int soldProduct = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT SUM(sa.quantity) AS sold FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE p.name = ? AND t.date LIKE ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, '%' + month);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        soldProduct = rs.getInt("sold");
                    }
                }
            }
        }

        return soldProduct;
    }

    public List<String> getFavoriteProduct(String month) throws SQLException, ClassNotFoundException {
        List<String> favoriteProduct = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT DISTINCT p.name, SUM(sa.quantity) AS sold FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE t.date LIKE ? GROUP BY p.barcode ORDER BY sold DESC LIMIT 5";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, '%' + month);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        favoriteProduct.add(rs.getString("name"));
                    }
                }
            }
        }

        return favoriteProduct;
    }
}
