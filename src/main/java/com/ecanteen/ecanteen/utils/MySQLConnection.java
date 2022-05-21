package com.ecanteen.ecanteen.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://192.168.1.1:3306/ecanteen";
    private static final String URL2 = "jdbc:mysql://localhost:3306/ecanteen";
    private static final String USERNAME = "ecanteen";
    private static final String USERNAME2 = "root";
    private static final String PASSWORD = "idc2022";
    private static final String PASSWORD2 = "";

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection connection;

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            connection = DriverManager.getConnection(URL2, USERNAME2, PASSWORD2);
        }

        connection.setAutoCommit(false);
        return connection;
    }
}
