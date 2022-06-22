package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Income;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supply;
import com.ecanteen.ecanteen.entities.User;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IncomeDaoImpl {
    public List<Income> fetchIncomeAdmin(String date) throws SQLException, ClassNotFoundException {
        List<Income> incomes = new ArrayList<>();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT t.username AS cashier, u.name, SUM(total_amount) AS income FROM transaction t JOIN user u ON u.username = t.username WHERE t.date = ? GROUP BY t.username";

            String query2 = "SELECT s.quantity AS qty, p.purchase_price AS pp, p.selling_price AS sp FROM sale s JOIN product p ON s.barcode = p.barcode JOIN transaction t ON s.transaction_id = t.id WHERE t.date = ? AND t.username = ?";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, date);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        User user = new User();
                        user.setUsername(rs.getString("cashier"));
                        user.setName(rs.getString("name"));

                        Income income = new Income();
                        income.setCashier(user);

                        String incomeValue = formatter.format(rs.getInt("income"));

                        income.setIncome(incomeValue);

                        int profitInt = 0;

                        try (PreparedStatement ps2 = connection.prepareStatement(query2)) {
                            ps2.setString(1, date);
                            ps2.setString(2, income.getCashier().getUsername());

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    int qty = rs2.getInt("qty");
                                    String pp = rs2.getString("pp");
                                    String sp = rs2.getString("sp");

                                    String[] ppArray = pp.split("\\.");
                                    String[] spArray = sp.split("\\.");
                                    StringBuilder ppSb = new StringBuilder();
                                    StringBuilder spSb = new StringBuilder();
                                    for (String s : ppArray) {
                                        ppSb.append(s);
                                    }
                                    for (String s : spArray) {
                                        spSb.append(s);
                                    }
                                    int ppInt = Integer.parseInt(String.valueOf(ppSb));
                                    int spInt = Integer.parseInt(String.valueOf(spSb));

                                    int subProfitInt = (spInt - ppInt) * qty;
                                    profitInt += subProfitInt;
                                }
                            }
                        }

                        String profitString = formatter.format(profitInt);
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
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT DATE_FORMAT(t.date, '%d-%m-%Y') AS dt, t.date, SUM(total_amount) AS income FROM transaction t WHERE(t.date >= ? AND t.date <= ?) GROUP BY t.date";

            String query2 = "SELECT s.quantity AS qty, p.purchase_price AS pp, p.selling_price AS sp FROM sale s JOIN product p ON s.barcode = p.barcode JOIN transaction t ON s.transaction_id = t.id WHERE t.date = ?";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, fromDate);
                ps.setString(2, toDate);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Income income = new Income();
                        income.setDate(rs.getString("dt"));

                        String incomeValue = formatter.format(rs.getInt("income"));

                        income.setIncome(incomeValue);
                        int profitInt = 0;

                        try (PreparedStatement ps2 = connection.prepareStatement(query2)) {
                            ps2.setString(1, rs.getString("date"));

                            try (ResultSet rs2 = ps2.executeQuery()) {
                                while (rs2.next()) {
                                    int qty = rs2.getInt("qty");
                                    String pp = rs2.getString("pp");
                                    String sp = rs2.getString("sp");

                                    String[] ppArray = pp.split("\\.");
                                    String[] spArray = sp.split("\\.");
                                    StringBuilder ppSb = new StringBuilder();
                                    StringBuilder spSb = new StringBuilder();
                                    for (String s : ppArray) {
                                        ppSb.append(s);
                                    }
                                    for (String s : spArray) {
                                        spSb.append(s);
                                    }
                                    int ppInt = Integer.parseInt(String.valueOf(ppSb));
                                    int spInt = Integer.parseInt(String.valueOf(spSb));

                                    int subProfitInt = (spInt - ppInt) * qty;
                                    profitInt += subProfitInt;
                                }
                            }
                        }

                        String profitString = formatter.format(profitInt);
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
            String query = "SELECT id, DATE_FORMAT(date, '%d-%m-%Y') AS date, SUM(total_amount) AS income FROM transaction WHERE username = ? GROUP BY date ORDER BY 1 DESC";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, Common.user.getUsername());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Income income = new Income();
                        income.setDate(rs.getString("date"));

                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                        symbols.setGroupingSeparator('.');
                        formatter.setDecimalFormatSymbols(symbols);
                        String incomeValue = formatter.format(rs.getInt("income"));

                        income.setIncome(incomeValue);

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
            String querySold = "SELECT p.barcode, p.name, p.purchase_price, SUM(sa.quantity) AS sold FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE p.supplier_id = ? AND t.date = ? GROUP BY sa.barcode";

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

                        String[] ppArray = purchasePrice.split("\\.");
                        StringBuilder ppSb = new StringBuilder();
                        for (String s : ppArray) {
                            ppSb.append(s);
                        }
                        int ppInt = Integer.parseInt(String.valueOf(ppSb));
                        int subtotalInt = sold * ppInt;

                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                        symbols.setGroupingSeparator('.');
                        formatter.setDecimalFormatSymbols(symbols);
                        String subtotalString = formatter.format(subtotalInt);

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
            String querySold = "SELECT DATE_FORMAT(t.date, '%d-%m-%Y') AS dt, t.date, p.barcode, p.name, p.purchase_price, SUM(sa.quantity) AS sold FROM sale sa JOIN product p ON p.barcode = sa.barcode JOIN transaction t ON sa.transaction_id = t.id WHERE p.supplier_id = ? AND (t.date >= ? AND t.date <= ?) GROUP BY t.date, sa.barcode";

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

                        String[] ppArray = purchasePrice.split("\\.");
                        StringBuilder ppSb = new StringBuilder();
                        for (String s : ppArray) {
                            ppSb.append(s);
                        }
                        int ppInt = Integer.parseInt(String.valueOf(ppSb));
                        int subtotalInt = sold * ppInt;

                        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                        symbols.setGroupingSeparator('.');
                        formatter.setDecimalFormatSymbols(symbols);
                        String subtotalString = formatter.format(subtotalInt);

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
