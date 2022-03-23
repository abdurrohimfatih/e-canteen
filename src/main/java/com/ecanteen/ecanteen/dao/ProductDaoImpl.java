package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
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
                    "SELECT p.barcode, p.name, p.category_id, p.purchase_price, p.selling_price, p.stock_amount, p.supplier_id, p.date_added, p.expired_date, p.promotion_id, c.name AS category_name, s.name AS supplier_name, pm.name AS promotion_name FROM product p JOIN category c ON p.category_id = c.id JOIN supplier s ON p.supplier_id = s.id JOIN promotion pm ON p.promotion_id = pm.id";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Category category = new Category();
                        category.setId(rs.getInt("category_id"));
                        category.setName(rs.getString("category_name"));

                        Supplier supplier = new Supplier();
                        supplier.setId(rs.getString("supplier_id"));
                        supplier.setName(rs.getString("supplier_name"));

                        Promotion promotion = new Promotion();
                        promotion.setId(rs.getString("promotion_id"));
                        promotion.setName(rs.getString("promotion_name"));

                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));
                        product.setCategory(category);
                        product.setPurchasePrice(rs.getInt("purchase_price"));
                        product.setSellingPrice(rs.getInt("selling_price"));
                        product.setStockAmount(rs.getInt("stock_amount"));
                        product.setSupplier(supplier);
                        product.setDateAdded(rs.getString("date_added"));
                        product.setExpiredDate(rs.getString("expired_date"));
                        product.setPromotion(promotion);
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
            String query = "INSERT INTO product(barcode, name, category_id, purchase_price, selling_price, stock_amount, supplier_id, date_added, expired_date, promotion_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getBarcode());
                ps.setString(2, object.getName());
                ps.setInt(3, object.getCategory().getId());
                ps.setInt(4, object.getPurchasePrice());
                ps.setInt(5, object.getSellingPrice());
                ps.setInt(6, object.getStockAmount());
                ps.setString(7, object.getSupplier().getId());
                ps.setString(8, object.getDateAdded());
                ps.setString(9, object.getExpiredDate());
                ps.setString(10, object.getPromotion().getId());

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
            String query = "UPDATE product SET name = ?, category_id = ?, purchase_price = ?, selling_price = ?, stock_amount = ?, supplier_id = ?, date_added = ?, expired_date = ?, promotion_id = ? WHERE barcode = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setInt(2, object.getCategory().getId());
                ps.setInt(3, object.getPurchasePrice());
                ps.setInt(4, object.getSellingPrice());
                ps.setInt(5, object.getStockAmount());
                ps.setString(6, object.getSupplier().getId());
                ps.setString(7, object.getDateAdded());
                ps.setString(8, object.getExpiredDate());
                ps.setString(9, object.getPromotion().getId());
                ps.setString(10, object.getBarcode());

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

    public static int getProductAmountCategory(Category object) throws SQLException, ClassNotFoundException {
        int productAmount = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT COUNT(*) AS amount FROM product WHERE category_id = ? GROUP BY category_id";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        productAmount = rs.getInt("amount");
                    }
                }
            }
        }

        return productAmount;
    }

    public static int getProductAmountSupplier(Supplier object) throws SQLException, ClassNotFoundException {
        int productAmount = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT COUNT(*) AS amount FROM product WHERE supplier_id = ? GROUP BY supplier_id";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        productAmount = rs.getInt("amount");
                    }
                }
            }
        }

        return productAmount;
    }

    public List<Product> detailCategory(Category object) throws SQLException, ClassNotFoundException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()){
            String query =
                    "SELECT p.barcode, p.name, p.category_id, p.purchase_price, p.selling_price, p.stock_amount, p.supplier_id, s.name AS supplier_name FROM product p JOIN supplier s ON p.supplier_id = s.id WHERE p.category_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Supplier supplier = new Supplier();
                        supplier.setId(rs.getString("supplier_id"));
                        supplier.setName(rs.getString("supplier_name"));

                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));
                        product.setPurchasePrice(rs.getInt("purchase_price"));
                        product.setSellingPrice(rs.getInt("selling_price"));
                        product.setStockAmount(rs.getInt("stock_amount"));
                        product.setSupplier(supplier);
                        products.add(product);
                    }
                }
            }
        }

        return products;
    }

    public List<Product> detailSupplier(Supplier object) throws SQLException, ClassNotFoundException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()){
            String query =
                    "SELECT p.barcode, p.name, p.category_id, p.purchase_price, p.selling_price, p.stock_amount, p.supplier_id, c.name AS category_name FROM product p JOIN category c ON p.category_id = c.id WHERE p.supplier_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Category category = new Category();
                        category.setId(rs.getInt("category_id"));
                        category.setName(rs.getString("category_name"));

                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));
                        product.setPurchasePrice(rs.getInt("purchase_price"));
                        product.setSellingPrice(rs.getInt("selling_price"));
                        product.setStockAmount(rs.getInt("stock_amount"));
                        product.setCategory(category);
                        products.add(product);
                    }
                }
            }
        }

        return products;
    }
}
