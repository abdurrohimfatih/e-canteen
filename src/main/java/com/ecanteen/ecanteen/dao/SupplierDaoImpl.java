package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoImpl implements DaoService<Supplier> {
    @Override
    public List<Supplier> fetchAll() throws SQLException, ClassNotFoundException {
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id, name, last_supplied_date FROM supplier ORDER BY id";
            try (PreparedStatement ps = connection.prepareStatement(query)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        Supplier supplier = new Supplier();
                        supplier.setId(rs.getString("id"));
                        supplier.setName(rs.getString("name"));
                        supplier.setLastSuppliedDate(rs.getString("last_supplied_date"));
                        suppliers.add(supplier);
                    }
                }
            }
        }

        return suppliers;
    }

    @Override
    public int addData(Supplier object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO supplier(id, name, last_supplied_date) VALUES(?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getName());
                ps.setString(3, object.getLastSuppliedDate());

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
    public int updateData(Supplier object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE supplier SET name = ?, last_supplied_date = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setString(2, object.getLastSuppliedDate());
                ps.setString(3, object.getId());

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
    public int deleteData(Supplier object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM supplier WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());

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
