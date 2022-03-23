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
            String query = "SELECT id, name, address, gender, phone, email, bank_account, account_number, status FROM supplier";
            try (PreparedStatement ps = connection.prepareStatement(query)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        Supplier supplier = new Supplier();
                        supplier.setId(rs.getString("id"));
                        supplier.setName(rs.getString("name"));
                        supplier.setAddress(rs.getString("address"));
                        supplier.setGender(rs.getString("gender"));
                        supplier.setPhone(rs.getString("phone"));
                        supplier.setEmail(rs.getString("email"));
                        supplier.setBankAccount(rs.getString("bank_account"));
                        supplier.setAccountNumber(rs.getString("account_number"));
                        supplier.setProductAmount(ProductDaoImpl.getProductAmountSupplier(supplier));
                        if (rs.getString("status").equals("1")) {
                            supplier.setStatus("Aktif");
                        } else {
                            supplier.setStatus("Tidak Aktif");
                        }
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
            String query = "INSERT INTO supplier(id, name, address, gender, phone, email, bank_account, account_number, status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getName());
                ps.setString(3, object.getAddress());
                ps.setString(4, object.getGender());
                ps.setString(5, object.getPhone());
                ps.setString(6, object.getEmail());
                ps.setString(7, object.getBankAccount());
                ps.setString(8, object.getAccountNumber());
                ps.setString(9, object.getStatus());

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
            String query = "UPDATE supplier SET name = ?, address = ?, gender = ?, phone = ?, email = ?, bank_account = ?, account_number = ?, status = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setString(2, object.getAddress());
                ps.setString(3, object.getGender());
                ps.setString(4, object.getPhone());
                ps.setString(5, object.getEmail());
                ps.setString(6, object.getBankAccount());
                ps.setString(7, object.getAccountNumber());
                ps.setString(8, object.getStatus());
                ps.setString(9, object.getId());

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
