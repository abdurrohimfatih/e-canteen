package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Customer;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements DaoService<Customer> {
    @Override
    public List<Customer> fetchAll() throws SQLException, ClassNotFoundException {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id, name, gender, role, balance FROM customer WHERE id > 0";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getInt("id"));
                        customer.setName(rs.getString("name"));
                        customer.setGender(rs.getString("gender"));
                        customer.setRole(rs.getString("role"));
                        String balance = Helper.currencyToString(rs.getInt("balance"));
                        customer.setBalance(balance);
                        customer.setTransactionAmount(TransactionDaoImpl.getCustomerTransactionAmount(customer));
                        customers.add(customer);
                    }
                }
            }
        }

        return customers;
    }

    @Override
    public int addData(Customer object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO customer(name, gender, role) VALUES(?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setString(2, object.getGender());
                ps.setString(3, object.getRole());

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
    public int updateData(Customer object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE customer SET name = ?, gender = ?, role = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setString(2, object.getGender());
                ps.setString(3, object.getRole());
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
    public int deleteData(Customer object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM customer WHERE id = ?";
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

    public int updateBalance(int balance, Customer object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE customer SET balance = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, balance);
                ps.setInt(2, object.getId());

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
