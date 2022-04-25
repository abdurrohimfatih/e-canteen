package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Transaction;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            String query = "INSERT INTO transaction(id, username, date, time, barcode, quantity, total_amount) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getUsername());
                ps.setString(3, object.getDate());
                ps.setString(4, object.getTime());
                ps.setString(5, object.getBarcodes());
                ps.setString(6, object.getQts());
                ps.setString(7, object.getTotalAmount());

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
}
