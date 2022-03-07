package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.utils.DaoService;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromotionDaoImpl implements DaoService<Promotion> {
    @Override
    public List<Promotion> fetchAll() throws SQLException, ClassNotFoundException {
        List<Promotion> promotions = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT pm.id, pm.name, pm.product_barcode, pm.percentage, pm.description, pd.name AS product_name FROM promotion pm JOIN product pd ON pm.product_barcode = pd.barcode ORDER BY pm.id";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setBarcode(rs.getString("product_barcode"));
                        product.setName(rs.getString("product_name"));

                        Promotion promotion = new Promotion();
                        promotion.setId(rs.getString("id"));
                        promotion.setName(rs.getString("name"));
                        promotion.setProduct(product);
                        promotion.setPercentage(rs.getInt("percentage"));
                        promotion.setDescription(rs.getString("description"));
                        promotions.add(promotion);
                    }
                }
            }
        }

        return promotions;
    }

    @Override
    public int addData(Promotion object) throws SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public int updateData(Promotion object) throws SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public int deleteData(Promotion object) throws SQLException, ClassNotFoundException {
        return 0;
    }
}
