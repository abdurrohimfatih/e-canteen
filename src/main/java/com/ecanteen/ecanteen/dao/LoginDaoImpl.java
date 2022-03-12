package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Login;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginDaoImpl {
    public List<Login> fetchAll() throws SQLException, ClassNotFoundException {
        List<Login> logins = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT username, password, name, level FROM user ORDER BY username";
            try (PreparedStatement ps = connection.prepareStatement(query)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        Login login = new Login();
                        login.setUsername(rs.getString("username"));
                        login.setPassword(rs.getString("password"));
                        login.setName(rs.getString("name"));
                        login.setLevel(rs.getString("level"));
                        logins.add(login);
                    }
                }
            }
        }

        return logins;
    }
}
