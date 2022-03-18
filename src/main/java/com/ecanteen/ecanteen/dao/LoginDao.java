package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {
    public boolean validate(String username, String password) throws SQLException, ClassNotFoundException {
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT username, password FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return true;
                }
            }
        }

        return false;
    }

    public String getLevel(String username, String password) throws SQLException, ClassNotFoundException {
        String level = null;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT level FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        level = rs.getString("level");
                    }
                }
            }
        }

        return level;
    }

    public String getStatus(String username, String password) throws SQLException, ClassNotFoundException {
        String status = null;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT status FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        status = rs.getString("status");
                    }
                }
            }
        }

        return status;
    }
}
