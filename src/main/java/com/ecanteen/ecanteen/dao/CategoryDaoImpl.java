package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements DaoService<Category> {
    @Override
    public List<Category> fetchAll() throws SQLException, ClassNotFoundException {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id, name, date_created FROM category ORDER BY id";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Category category = new Category();
                        category.setId(rs.getString("id"));
                        category.setName(rs.getString("name"));
                        category.setDateCreated(rs.getString("date_created"));
                        categories.add(category);
                    }
                }
            }
        }

        return categories;
    }

    @Override
    public int addData(Category object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO category(id, name, date_created) VALUES(?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getName());
                ps.setString(3, object.getDateCreated());

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
    public int updateData(Category object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE category SET name = ?, date_created = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setString(2, object.getDateCreated());
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
    public int deleteData(Category object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM category WHERE id = ?";
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
