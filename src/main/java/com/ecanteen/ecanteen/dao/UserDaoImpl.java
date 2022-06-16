package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.User;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.LoginService;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements LoginService, DaoService<User> {
    @Override
    public User login(String username, String password) throws SQLException, ClassNotFoundException {
        User user = null;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT username, password, name, level, status FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        user = new User();
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setName(rs.getString("name"));
                        user.setLevel(rs.getString("level"));
                        user.setStatus(rs.getString("status"));
                    }
                }
            }
        }

        return user;
    }

    public List<User> fetchAll() throws SQLException, ClassNotFoundException {
        List<User> users = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT username, password, name, address, gender, phone, email, level, DATE_FORMAT(date_created, '%d-%m-%Y') AS date_created, status FROM user";
            try (PreparedStatement ps = connection.prepareStatement(query)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        User user = new User();
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        user.setName(rs.getString("name"));
                        user.setAddress(rs.getString("address"));
                        user.setGender(rs.getString("gender"));
                        user.setPhone(rs.getString("phone"));
                        user.setEmail(rs.getString("email"));
                        user.setLevel(rs.getString("level"));
                        user.setDateCreated(rs.getString("date_created"));
                        if (rs.getString("status").equals("1")) {
                            user.setStatus("Aktif");
                        } else {
                            user.setStatus("Tidak Aktif");
                        }
                        user.setTransactionAmount(TransactionDaoImpl.getTransactionAmount(user));
                        users.add(user);
                    }
                }
            }
        }

        return users;
    }

    @Override
    public int addData(User object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO user(username, password, name, address, gender, phone, email, level, date_created, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getUsername());
                ps.setString(2, object.getPassword());
                ps.setString(3, object.getName());
                ps.setString(4, object.getAddress());
                ps.setString(5, object.getGender());
                ps.setString(6, object.getPhone());
                ps.setString(7, object.getEmail());
                ps.setString(8, object.getLevel());
                ps.setString(9, object.getDateCreated());
                ps.setString(10, object.getStatus());

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
    public int updateData(User object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE user SET username = ?, password = ?, name = ?, address = ?, gender = ?, phone = ?, email = ?, level = ?, status = ? WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getUsername());
                ps.setString(2, object.getPassword());
                ps.setString(3, object.getName());
                ps.setString(4, object.getAddress());
                ps.setString(5, object.getGender());
                ps.setString(6, object.getPhone());
                ps.setString(7, object.getEmail());
                ps.setString(8, object.getLevel());
                ps.setString(9, object.getStatus());
                ps.setString(10, Common.oldUsername);

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

    public int updateDataExceptPassword(User object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE user SET username = ?, name = ?, address = ?, gender = ?, phone = ?, email = ?, level = ?, status = ? WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getUsername());
                ps.setString(2, object.getName());
                ps.setString(3, object.getAddress());
                ps.setString(4, object.getGender());
                ps.setString(5, object.getPhone());
                ps.setString(6, object.getEmail());
                ps.setString(7, object.getLevel());
                ps.setString(8, object.getStatus());
                ps.setString(9, Common.oldUsername);

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
    public int deleteData(User object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM user WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getUsername());

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

    public int getUsername(String username) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT username FROM user WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result = 1;
                    }
                }
            }
        }

        return result;
    }
}
