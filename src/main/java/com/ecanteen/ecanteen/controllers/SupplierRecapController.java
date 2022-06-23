package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.IncomeDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.entities.Supply;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.ReportGenerator;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.ResourceBundle;

public class SupplierRecapController implements Initializable {
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
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TableView<Supply> supplyTableView;
    @FXML
    private TableColumn<Supply, Integer> noTableColumn;
    @FXML
    private TableColumn<Supply, String> dateTableColumn;
    @FXML
    private TableColumn<Supply, String> nameTableColumn;
    @FXML
    private TableColumn<Supply, Integer> addedTableColumn;
    @FXML
    private TableColumn<Supply, Integer> soldTableColumn;
    @FXML
    private TableColumn<Supply, Integer> returnedTableColumn;
    @FXML
    private TableColumn<Supply, String> subtotalTableColumn;
    @FXML
    private TextField totalTextField;
    @FXML
    private Button printButton;

    private IncomeDaoImpl incomeDao;
    private ObservableList<Supply> supplies;
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

        Helper.formatDatePicker(fromDatePicker);
        Helper.formatDatePicker(toDatePicker);
        fromDatePicker.getEditor().setDisable(true);
        toDatePicker.getEditor().setDisable(true);
        fromDatePicker.getEditor().setOpacity(1);
        toDatePicker.getEditor().setOpacity(1);
        fromDatePicker.setValue(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        toDatePicker.setValue(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));

        supplyTableView.setPlaceholder(new Label("Pilih supplier terlebih dahulu."));
        supplierComboBox.setItems(suppliers);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(supplyTableView.getItems().indexOf(data.getValue()) + 1));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        addedTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAdded()).asObject());
        soldTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSold()).asObject());
        returnedTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getReturned()).asObject());
        subtotalTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubtotal()));
    }

    @FXML
    private void supplierComboBoxAction(ActionEvent actionEvent) {
        filterAction();
    }

    @FXML
    private void fromDatePickerAction(ActionEvent actionEvent) {
        filterAction();
    }

    @FXML
    private void toDatePickerAction(ActionEvent actionEvent) {
        filterAction();
    }

    private void filterAction() {
        if (supplierComboBox.getValue() == null || fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            return;
        }

        String supplierId = supplierComboBox.getValue().getId();
        String fromDate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toDate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            supplies.clear();
            supplies.addAll(incomeDao.fetchSupplierRecap(supplierId, fromDate, toDate));
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

        printButton.setDisable(suppliesData.isEmpty());
    }

    @FXML
    private void printButtonAction(ActionEvent actionEvent) {
        suppliesData = supplyTableView.getItems();
        String supplier = supplierComboBox.getValue().getName();
        String fromDate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String toDate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String total = totalTextField.getText();

        int totalAdd = 0;
        int totalSold = 0;
        int totalReturn = 0;

        for (Supply item : suppliesData) {
            totalAdd += item.getAdded();
            totalSold += item.getSold();
            totalReturn += item.getReturned();
        }

        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeNow = Helper.formattedTimeNow();

        new ReportGenerator().printSupplierRecap(suppliesData, supplier, fromDate, toDate, totalAdd, totalSold, totalReturn, total, dateNow, timeNow);
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
    private void supplierReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Laporan Supplier", "supplier-report-view.fxml");
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
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
