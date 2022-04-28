package com.ecanteen.ecanteen.dao;

import com.ecanteen.ecanteen.entities.Income;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.DaoService;
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

public class IncomeDaoImpl implements DaoService<Income> {
    @Override
    public List<Income> fetchAll() throws SQLException, ClassNotFoundException {
        List<Income> incomes = new ArrayList<>();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT date, username AS cashier, SUM(total_amount) AS income FROM transaction GROUP BY date, username";

            String query2 = "SELECT s.quantity AS qty, p.purchase_price AS pp, p.selling_price AS sp FROM sale s JOIN product p ON s.barcode = p.barcode JOIn transaction t ON s.transaction_id = t.id WHERE t.date = ? && t.username = ?";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Income income = new Income();
                        income.setDate(rs.getString("date"));
                        income.setCashier(rs.getString("cashier"));

                        String incomeValue = formatter.format(rs.getInt("income"));

                        income.setIncome(incomeValue);

                        int profitInt = 0;
                        try (PreparedStatement ps2 = connection.prepareStatement(query2)) {
                            ps2.setString(1, rs.getString("date"));
                            ps2.setString(2, rs.getString("cashier"));

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
            String query = "SELECT date, SUM(total_amount) AS income FROM transaction WHERE username = ? GROUP BY date";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, Common.user.getName());

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

    @Override
    public int addData(Income object) throws SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public int updateData(Income object) throws SQLException, ClassNotFoundException {
        return 0;
    }

    @Override
    public int deleteData(Income object) throws SQLException, ClassNotFoundException {
        return 0;
    }
}
