package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleDao {
    public int getNowSaleId() throws SQLException, ClassNotFoundException {
        int nowSaleId = 1111;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id FROM sale ORDER BY id DESC LIMIT 1";
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

    public int addSale(Sale sale) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO sale(id, username, date, time, barcode, quantity, total_amount) VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, sale.getId());
                ps.setString(2, sale.getUsername());
                ps.setString(3, sale.getDate());
                ps.setString(4, sale.getTime());
                ps.setString(5, sale.getBarcodes());
                ps.setString(6, sale.getQts());
                ps.setDouble(7, sale.getTotalAmount());

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
}
