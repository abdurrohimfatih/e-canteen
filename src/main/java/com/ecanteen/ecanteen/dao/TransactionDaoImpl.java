package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.*;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
            String query = "INSERT INTO transaction(id, username, date, time, total, customer_id) VALUES(?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getUsername());
                ps.setString(3, object.getDate());
                ps.setString(4, object.getTime());
                ps.setInt(5, object.getTotal());
                ps.setInt(6, object.getCustomer().getId());

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
        ProductDaoImpl productDao = new ProductDaoImpl();

        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String querySale = "SELECT barcode, quantity FROM sale WHERE transaction_id = ?";
            String queryTransaction = "DELETE FROM transaction WHERE id = ?";

            try (PreparedStatement ps1 = connection.prepareStatement(querySale)) {
                ps1.setString(1, object.getId());

                try (ResultSet rs = ps1.executeQuery()) {
                    while (rs.next()) {
                        String barcode = rs.getString("barcode");
                        int quantity = rs.getInt("quantity");
                        int oldStock = productDao.getStockAmount(barcode);
                        int newStock = oldStock + quantity;
                        productDao.updateStock(newStock, barcode);
                    }
                }
            }

            try (PreparedStatement ps2 = connection.prepareStatement(queryTransaction)) {
                ps2.setString(1, object.getId());
                if (ps2.executeUpdate() == 1) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    public void addSale(ObservableList<Sale> sales, String transactionId) throws SQLException, ClassNotFoundException {
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO sale (transaction_id, barcode, quantity, purchase_price, selling_price, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

            for (Sale item : sales) {
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, transactionId);
                    ps.setString(2, item.getBarcode());
                    ps.setInt(3, item.getQuantity());
                    ps.setString(4, item.getPurchasePrice());
                    ps.setString(5, item.getSellingPrice());
                    ps.setString(6, item.getSubtotal());

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

    public static int getUserTransactionAmount(User object) throws SQLException, ClassNotFoundException {
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

    public static int getCustomerTransactionAmount(Customer object) throws SQLException, ClassNotFoundException {
        int transactionAmount = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT COUNT(*) AS amount FROM transaction WHERE customer_id = ? GROUP BY customer_id";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        transactionAmount = rs.getInt("amount");
                    }
                }
            }
        }

        return transactionAmount;
    }

    public List<Transaction> fetchTransactionCashier() throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT t.id, c.name AS customer_name, t.total, t.time FROM transaction t JOIN customer c on c.id = t.customer_id WHERE username = ? AND date = ? ORDER BY t.id DESC";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, Common.user.getUsername());
                ps.setString(2, String.valueOf(LocalDate.now()));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Transaction transaction = new Transaction();
                        transaction.setId(rs.getString("id"));

                        Customer customer = new Customer();
                        customer.setName(rs.getString("customer_name"));

                        transaction.setCustomer(customer);
                        transaction.setTotal(rs.getInt("total"));
                        transaction.setTime(rs.getString("time"));

                        transactions.add(transaction);
                    }
                }
            }
        }

        return transactions;
    }
}
