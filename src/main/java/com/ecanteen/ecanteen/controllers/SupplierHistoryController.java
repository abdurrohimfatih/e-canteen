package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.IncomeDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.entities.Supply;
import com.ecanteen.ecanteen.entities.Transaction;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.ReportGenerator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class SupplierHistoryController implements Initializable {
    @FXML
    private MenuButton reportMenuButton;
    @FXML
    private MenuItem incomeMenuItem;
    @FXML
    private MenuItem soldProductMenuItem;
    @FXML
    private MenuItem favoriteProductMenuItem;
    @FXML
    private MenuItem supplierMenuItem;
    @FXML
    private MenuItem benefitMenuItem;
    @FXML
    private MenuButton stockMenuButton;
    @FXML
    private MenuItem productMenuItem;
    @FXML
    private MenuItem categoryMenuItem;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button customerMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private MenuButton historyMenuButton;
    @FXML
    private MenuItem incomeHistoryMenuItem;
    @FXML
    private MenuItem supplierHistoryMenuItem;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private SearchableComboBox<Supplier> supplierComboBox;
    @FXML
    private SearchableComboBox<Transaction> dateComboBox;
    @FXML
    private TableView<Supply> supplyTableView;
    @FXML
    private TableColumn<Supply, String> productTableColumn;
    @FXML
    private TableColumn<Supply, Integer> soldTableColumn;
    @FXML
    private TableColumn<Supply, String> subtotalTableColumn;
    @FXML
    private TextField totalTextField;
    @FXML
    private Button printSupplierButton;

    private ObservableList<Supply> supplies;
    private IncomeDaoImpl incomeDao;
    private ObservableList<Supply> suppliesData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        incomeDao = new IncomeDaoImpl();
        supplies = FXCollections.observableArrayList();
        SupplierDaoImpl supplierDao = new SupplierDaoImpl();
        TransactionDaoImpl transactionDao = new TransactionDaoImpl();
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        try {
            suppliers.addAll(supplierDao.fetchSuppliedSupplier());
            transactions.addAll(transactionDao.getTransactionDate());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        profileButton.setText(Common.user.getName());
        supplierComboBox.setItems(suppliers);
        dateComboBox.setItems(transactions);
        productTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct()));
        soldTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSold()).asObject());
        subtotalTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubtotal()));
    }

    @FXML
    private void supplierComboBoxAction(ActionEvent actionEvent) {
        if (supplierComboBox.getValue() != null && dateComboBox.getValue() != null) {
            String supplierId = supplierComboBox.getValue().getId();
            String transactionDate = dateComboBox.getValue().getDate();

            try {
                supplies.clear();
                supplies.addAll(incomeDao.fetchSupplierHistory(supplierId, transactionDate));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            supplyTableView.setItems(supplies);
            suppliesData = supplyTableView.getItems();

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);

            int totalInt = 0;
            for (Supply i : suppliesData) {
                String[] subtotalArray = i.getSubtotal().split("\\.");
                StringBuilder sub = new StringBuilder();
                for (String s : subtotalArray) {
                    sub.append(s);
                }
                int subtotalInt = Integer.parseInt(String.valueOf(sub));
                totalInt += subtotalInt;
            }
            String totalString = formatter.format(totalInt);

            totalTextField.setText(totalString);
            printSupplierButton.setDisable(false);
        }
    }

    @FXML
    private void dateComboBoxAction(ActionEvent actionEvent) {
        if (supplierComboBox.getValue() != null && dateComboBox.getValue() != null) {
            String supplierId = supplierComboBox.getValue().getId();
            String transactionDate = dateComboBox.getValue().getDate();

            try {
                supplies.clear();
                supplies.addAll(incomeDao.fetchSupplierHistory(supplierId, transactionDate));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            supplyTableView.setItems(supplies);
            suppliesData = supplyTableView.getItems();

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);

            int totalInt = 0;
            for (Supply i : suppliesData) {
                String[] subtotalArray = i.getSubtotal().split("\\.");
                StringBuilder sub = new StringBuilder();
                for (String s : subtotalArray) {
                    sub.append(s);
                }
                int subtotalInt = Integer.parseInt(String.valueOf(sub));
                totalInt += subtotalInt;
            }
            String totalString = formatter.format(totalInt);

            totalTextField.setText(totalString);
            printSupplierButton.setDisable(false);
        }
    }

    @FXML
    private void printSupplierButtonAction(ActionEvent actionEvent) {
        suppliesData = supplyTableView.getItems();
        String supplier = supplierComboBox.getValue().getName();
        String date = dateComboBox.getValue().getDate();
        String total = totalTextField.getText();

        new ReportGenerator().printSupplierHistory(suppliesData, supplier, date, total);
    }

    @FXML
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void userButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
    }

    @FXML
    private void supplierButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
    }

    @FXML
    private void incomeHistoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(historyMenuButton, "Admin - Riwayat Pendapatan", "income-admin-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
