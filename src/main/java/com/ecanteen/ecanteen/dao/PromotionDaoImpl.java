package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionDaoImpl implements DaoService<Promotion> {
    @Override
    public List<Promotion> fetchAll() throws SQLException, ClassNotFoundException {
        List<Promotion> promotions = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id, name, percentage, date_added, expired_date FROM promotion WHERE id != -1";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Promotion promotion = new Promotion();
                        promotion.setId(rs.getString("id"));
                        promotion.setName(rs.getString("name"));
                        promotion.setPercentage(rs.getInt("percentage"));
                        promotion.setDateAdded(rs.getString("date_added"));
                        promotion.setExpiredDate(rs.getString("expired_date"));
                        if (LocalDate.now().isBefore(LocalDate.parse(rs.getString("expired_date"))) || LocalDate.now().isEqual(LocalDate.parse(rs.getString("expired_date")))) {
                            promotion.setStatus("Aktif");
                        } else {
                            promotion.setStatus("Kedaluwarsa");
                        }
                        promotions.add(promotion);
                    }
                }
            }
        }

        return promotions;
    }

    @Override
    public int addData(Promotion object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "INSERT INTO promotion(id, name, percentage, date_added, expired_date) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getId());
                ps.setString(2, object.getName());
                ps.setInt(3, object.getPercentage());
                ps.setString(4, object.getDateAdded());
                ps.setString(5, object.getExpiredDate());

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
    public int updateData(Promotion object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE promotion SET name = ?, percentage = ?, date_added = ?, expired_date = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setInt(2, object.getPercentage());
                ps.setString(3, object.getDateAdded());
                ps.setString(4, object.getExpiredDate());
                ps.setString(5, object.getId());

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
    public int deleteData(Promotion object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM promotion WHERE id = ?";
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

    public int getId(String id) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id FROM promotion WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, id);

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
