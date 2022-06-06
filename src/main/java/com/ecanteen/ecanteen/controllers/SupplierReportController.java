package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.IncomeDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.entities.Supply;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class SupplierReportController implements Initializable {
    @FXML
    private MenuButton masterMenuButton;
    @FXML
    private MenuItem productMenuItem;
    @FXML
    private MenuItem categoryMenuItem;
    @FXML
    private MenuButton stockMenuButton;
    @FXML
    private MenuItem addStockMenuItem;
    @FXML
    private MenuItem returnStockMenuItem;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button customerMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private MenuButton reportMenuButton;
    @FXML
    private MenuItem stockReportMenuItem;
    @FXML
    private MenuItem incomeReportMenuItem;
    @FXML
    private MenuItem supplierReportMenuItem;
    @FXML
    private MenuButton recapMenuButton;
    @FXML
    private MenuItem incomeRecapMenuItem;
    @FXML
    private MenuItem stockRecapMenuItem;
    @FXML
    private MenuItem supplierRecapMenuItem;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private SearchableComboBox<Supplier> supplierComboBox;
    @FXML
    private DatePicker dateDatePicker;
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
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();

        try {
            suppliers.addAll(supplierDao.fetchSuppliedSupplier());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Helper.formatDatePicker(dateDatePicker);
        dateDatePicker.getEditor().setDisable(true);
        dateDatePicker.getEditor().setOpacity(1);
        dateDatePicker.setValue(LocalDate.now());
        supplierComboBox.setItems(suppliers);
        supplyTableView.setPlaceholder(new Label("Tidak ada data."));
        productTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct()));
        soldTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSold()).asObject());
        subtotalTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubtotal()));
    }

    @FXML
    private void supplierComboBoxAction(ActionEvent actionEvent) {
        if (supplierComboBox.getValue() == null || dateDatePicker.getValue() == null) {
            return;
        }

        String supplierId = supplierComboBox.getValue().getId();
        String transactionDate = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

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

        if (totalInt != 0) {
            totalTextField.setText(totalString);
        } else {
            totalTextField.setText("");
        }

        printSupplierButton.setDisable(suppliesData.isEmpty());
    }

    @FXML
    private void dateDatePickerAction(ActionEvent actionEvent) {
        if (supplierComboBox.getValue() == null || dateDatePicker.getValue() == null) {
            return;
        }

        String supplierId = supplierComboBox.getValue().getId();
        String transactionDate = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

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

        if (totalInt != 0) {
            totalTextField.setText(totalString);
        } else {
            totalTextField.setText("");
        }

        printSupplierButton.setDisable(suppliesData.isEmpty());
    }

    @FXML
    private void printSupplierButtonAction(ActionEvent actionEvent) {
        suppliesData = supplyTableView.getItems();
        String supplier = supplierComboBox.getValue().getName();
        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String total = totalTextField.getText();

        new ReportGenerator().printSupplierHistory(suppliesData, supplier, date, total);
    }

    @FXML
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(masterMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(masterMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void addStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Tambah Stok", "add-stock-view.fxml");
    }

    @FXML
    private void returnStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Return Stok", "return-stock-view.fxml");
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
    private void stockReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Stok", "stock-report-view.fxml");
    }

    @FXML
    private void incomeReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Pendapatan", "income-report-view.fxml");
    }

    @FXML
    private void stockRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Stok", "stock-recap-view.fxml");
    }

    @FXML
    private void incomeRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "income-recap-view.fxml");
    }

    @FXML
    private void supplierRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "supplier-recap-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
