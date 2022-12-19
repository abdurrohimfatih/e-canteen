package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Income;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supply;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeDaoImpl {
    public List<Income> fetchIncomeAdmin(String date) throws SQLException, ClassNotFoundException {
        List<Income> incomes = new ArrayList<>();

        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT p.name AS product_name, p.purchase_price AS ppp, p.selling_price AS spp, SUM(s.quantity) AS quantity, s.purchase_price, s.selling_price FROM sale s JOIN transaction t on s.transaction_id = t.id JOIN product p on p.barcode = s.barcode WHERE t.date = ? GROUP BY p.barcode ORDER BY p.barcode";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, date);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setName(rs.getString("product_name"));

                        Income income = new Income();
                        income.setProduct(product);
                        income.setQty(rs.getInt("quantity"));

                        String purchasePrice = rs.getString("purchase_price");
                        String sellingPrice = rs.getString("selling_price");

                        int purchasePriceInt = Helper.currencyToInt(rs.getString("purchase_price"));
                        int sellingPriceInt = Helper.currencyToInt(rs.getString("selling_price"));

                        if (purchasePrice.equals("") || sellingPrice.equals("") || purchasePriceInt == 0 || sellingPriceInt == 0) {
                            purchasePriceInt = Helper.currencyToInt(rs.getString("ppp"));
                            sellingPriceInt = Helper.currencyToInt(rs.getString("spp"));
                        }

                        int incomeInt = income.getQty() * sellingPriceInt;
                        int profitInt = (sellingPriceInt - purchasePriceInt) * income.getQty();
                        String incomeString = Helper.currencyToString(incomeInt);
                        String profitString = Helper.currencyToString(profitInt);

                        income.setIncome(incomeString);
                        income.setProfit(profitString);
                        incomes.add(income);
                    }
                }
            }
        }

        return incomes;
    }

    public List<Income> fetchIncomeRecap(String fromDate, String toDate) throws SQLException, ClassNotFoundException {
        List<Income> incomes = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT DATE_FORMAT(t.date, '%d-%m-%Y') AS date, p.name AS product_name, p.purchase_price AS ppp, p.selling_price AS spp, SUM(s.quantity) AS quantity, s.purchase_price, s.selling_price FROM sale s JOIN transaction t on s.transaction_id = t.id JOIN product p on p.barcode = s.barcode WHERE(t.date >= ? AND t.date <= ?) GROUP BY p.barcode, t.date ORDER BY t.date, p.barcode";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, fromDate);
                ps.setString(2, toDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setName(rs.getString("product_name"));

                        Income income = new Income();
                        income.setProduct(product);
                        income.setDate(rs.getString("date"));
                        income.setQty(rs.getInt("quantity"));

                        String purchasePrice = rs.getString("purchase_price");
                        String sellingPrice = rs.getString("selling_price");

                        int purchasePriceInt = Helper.currencyToInt(rs.getString("purchase_price"));
                        int sellingPriceInt = Helper.currencyToInt(rs.getString("selling_price"));

                        if (purchasePrice.equals("") || sellingPrice.equals("") || purchasePriceInt == 0 || sellingPriceInt == 0) {
                            purchasePriceInt = Helper.currencyToInt(rs.getString("ppp"));
                            sellingPriceInt = Helper.currencyToInt(rs.getString("spp"));
                        }

                        int incomeInt = income.getQty() * sellingPriceInt;
                        int profitInt = (sellingPriceInt - purchasePriceInt) * income.getQty();
                        String incomeString = Helper.currencyToString(incomeInt);
                        String profitString = Helper.currencyToString(profitInt);
                        income.setIncome(incomeString);
                        income.setProfit(profitString);
                        incomes.add(income);
                    }
                }
            }
        }

        return incomes;
    }

    public List<Income> fetchIncomeCashier() throws SQLException, ClassNotFoundException {
        List<Income> incomes = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String q = "SELECT p.name AS product_name, p.selling_price AS spp, SUM(s.quantity) AS quantity, s.selling_price FROM sale s JOIN transaction t on s.transaction_id = t.id JOIN product p on p.barcode = s.barcode WHERE  t.username = ? AND t.date = ? GROUP BY p.barcode ORDER BY p.barcode";

            try (PreparedStatement ps = connection.prepareStatement(q)) {
                ps.setString(1, Common.user.getUsername());
                ps.setString(2, String.valueOf(LocalDate.now()));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setName(rs.getString("product_name"));

                        Income income = new Income();
                        income.setProduct(product);
                        income.setQty(rs.getInt("quantity"));

                        String sellingPrice = rs.getString("selling_price");
                        int sellingPriceInt = Helper.currencyToInt(rs.getString("selling_price"));

                        if (sellingPrice.equals("") || sellingPriceInt == 0) {
                            sellingPriceInt = Helper.currencyToInt(rs.getString("spp"));
                        }

                        int incomeInt = income.getQty() * sellingPriceInt;
                        String incomeString = Helper.currencyToString(incomeInt);
                        income.setIncome(incomeString);
                        incomes.add(income);
                    }
                }
            }
        }

        return incomes;
    }

    public List<Supply> fetchSupplierReport(String supplierId, String transactionDate) throws SQLException, ClassNotFoundException {
        List<Supply> supplies = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String querySold = "SELECT p.barcode, p.name, p.purchase_price AS ppp, sa.purchase_price, SUM(sa.quantity) AS sold FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE p.supplier_id = ? AND t.date = ? GROUP BY sa.barcode";

            String queryAdded = "SELECT SUM(st.qty) AS added FROM stock st WHERE st.barcode = ? AND st.date = ? AND st.type = ?";

            String queryReturned = "SELECT SUM(st.qty) AS returned FROM stock st WHERE st.barcode = ? AND st.date = ? AND st.type = ?";

            try (PreparedStatement ps = connection.prepareStatement(querySold)) {
                ps.setString(1, supplierId);
                ps.setString(2, transactionDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product();
                        product.setBarcode(rs.getString("barcode"));
                        product.setName(rs.getString("name"));

                        Supply supply = new Supply();
                        supply.setProduct(product);
                        supply.setBarcode(product.getBarcode());
                        supply.setName(product.getName());
                        supply.setSold(rs.getInt("sold"));

                        String purchasePrice = rs.getString("purchase_price");
                        int sold = supply.getSold();
                        int ppInt = Helper.currencyToInt(purchasePrice);

                        if (ppInt == 0) {
                            ppInt = Helper.currencyToInt(rs.getString("ppp"));
                        }

                        int subtotalInt = sold * ppInt;
                        String subtotalString = Helper.currencyToString(subtotalInt);

                        supply.setSubtotal(subtotalString);

                        int added = 0;
                        int returned = 0;

                        try (PreparedStatement ps2 = connection.prepareStatement(queryAdded)) {
                            ps2.setString(1, rs.getString("barcode"));
                            ps2.setString(2, transactionDate);
                            ps2.setString(3, "add");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    added = rs2.getInt("added");
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryReturned)) {
                            ps2.setString(1, rs.getString("barcode"));
                            ps2.setString(2, transactionDate);
                            ps2.setString(3, "return");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    returned = rs2.getInt("returned");
                                }
                            }
                        }

                        supply.setAdded(added);
                        supply.setReturned(returned);
                        supplies.add(supply);
                    }
                }
            }
        }

        return supplies;
    }

    public List<Supply> fetchSupplierRecap(String supplierId, String fromDate, String toDate) throws SQLException, ClassNotFoundException {
        List<Supply> supplies = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String querySold = "SELECT DATE_FORMAT(t.date, '%d-%m-%Y') AS dt, t.date, p.barcode, p.name, p.purchase_price AS ppp, sa.purchase_price, SUM(sa.quantity) AS sold FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE p.supplier_id = ? AND (t.date >= ? AND t.date <= ?) GROUP BY t.date, sa.barcode";

            String queryAdded = "SELECT SUM(st.qty) AS added FROM stock st WHERE st.barcode = ? AND st.date = ? AND st.type = ?";

            String queryReturned = "SELECT SUM(st.qty) AS returned FROM stock st WHERE st.barcode = ? AND st.date = ? AND st.type = ?";

            try (PreparedStatement ps = connection.prepareStatement(querySold)) {
                ps.setString(1, supplierId);
                ps.setString(2, fromDate);
                ps.setString(3, toDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Supply supply = new Supply();
                        supply.setDate(rs.getString("dt"));
                        supply.setName(rs.getString("name"));
                        supply.setSold(rs.getInt("sold"));

                        String purchasePrice = rs.getString("purchase_price");
                        int sold = supply.getSold();
                        int ppInt = Helper.currencyToInt(purchasePrice);

                        if (ppInt == 0) {
                            ppInt = Helper.currencyToInt(rs.getString("ppp"));
                        }

                        int subtotalInt = sold * ppInt;
                        String subtotalString = Helper.currencyToString(subtotalInt);

                        supply.setSubtotal(subtotalString);

                        int added = 0;
                        int returned = 0;

                        try (PreparedStatement ps2 = connection.prepareStatement(queryAdded)) {
                            ps2.setString(1, rs.getString("barcode"));
                            ps2.setString(2, rs.getString("date"));
                            ps2.setString(3, "add");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    added = rs2.getInt("added");
                                }
                            }
                        }

                        try (PreparedStatement ps2 = connection.prepareStatement(queryReturned)) {
                            ps2.setString(1, rs.getString("barcode"));
                            ps2.setString(2, rs.getString("date"));
                            ps2.setString(3, "return");

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    returned = rs2.getInt("returned");
                                }
                            }
                        }

                        supply.setAdded(added);
                        supply.setReturned(returned);
                        supplies.add(supply);
                    }
                }
            }
        }

        return supplies;
    }
}
