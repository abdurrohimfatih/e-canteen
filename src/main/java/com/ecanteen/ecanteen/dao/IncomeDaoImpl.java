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
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Income income = new Income();
                        income.setDate(rs.getString("date"));
                        income.setCashier(rs.getString("cashier"));
                        income.setIncome(rs.getString("income"));
                        income.setProfit(rs.getString("profit"));

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
