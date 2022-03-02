package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements DaoService<Product> {
    @Override
    public List<Product> fetchAll() throws SQLException, ClassNotFoundException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()){
            String query =
                    "SELECT p.barcode, p.name, p.category_id, p.price, p.stock_amount, p.supplier_id, p.date_added, p.expired_date, p.count, c.name AS category_name, s.name AS supplier_name FROM product p JOIN category c ON p.category_id = c.id JOIN supplier s ON p.supplier_id = s.id ORDER BY barcode";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Category category = new Category();
                        category.setId(rs.getString("category_id"));
                        category.setName(rs.getString("category_name"));

                        Supplier supplier = new Supplier();
                        supplier.setId(rs.getString("supplier_id"));
                        supplier.setName(rs.getString("supplier_name"));

                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));
                        product.setCategory(category);
                        product.setPrice(rs.getInt("price"));
                        product.setStockAmount(rs.getInt("stock_amount"));
                        product.setSupplier(supplier);
                        product.setDateAdded(rs.getString("date_added"));
                        product.setExpiredDate(rs.getString("expired_date"));
                        product.setCount(rs.getInt("count"));
                        products.add(product);
                    }
                }
            }
        }

        return products;
    }

    @Override
    public int addData(Product object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO product(barcode, name, category_id, price, stock_amount, supplier_id, date_added, expired_date, count) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getBarcode());
                ps.setString(2, object.getName());
                ps.setString(3, object.getCategory().getId());
                ps.setInt(4, object.getPrice());
                ps.setInt(5, object.getStockAmount());
                ps.setString(6, object.getSupplier().getId());
                ps.setString(7, object.getDateAdded());
                ps.setString(8, object.getExpiredDate());
                ps.setInt(9, object.getCount());

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
    public int updateData(Product object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE product SET name = ?, category_id = ?, price = ?, stock_amount = ?, supplier_id = ?, date_added = ?, expired_date = ?, count = ? WHERE barcode = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setString(2, object.getCategory().getId());
                ps.setInt(3, object.getPrice());
                ps.setInt(4, object.getStockAmount());
                ps.setString(5, object.getSupplier().getId());
                ps.setString(6, object.getDateAdded());
                ps.setString(7, object.getExpiredDate());
                ps.setInt(8, object.getCount());
                ps.setString(9, object.getBarcode());

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
    public int deleteData(Product object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM product WHERE barcode = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getBarcode());

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
