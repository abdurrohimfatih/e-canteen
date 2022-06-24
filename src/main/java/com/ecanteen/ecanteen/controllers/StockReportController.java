package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.StockDaoImpl;
import com.ecanteen.ecanteen.entities.Stock;
import com.ecanteen.ecanteen.utils.Common;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class StockReportController implements Initializable {
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
    private DatePicker dateDatePicker;
    @FXML
    private TableView<Stock> stockTableView;
    @FXML
    private TableColumn<Stock, Integer> noTableColumn;
    @FXML
    private TableColumn<Stock, String> barcodeTableColumn;
    @FXML
    private TableColumn<Stock, String> nameTableColumn;
    @FXML
    private TableColumn<Stock, Integer> previousStockTableColumn;
    @FXML
    private TableColumn<Stock, Integer> addedTableColumn;
    @FXML
    private TableColumn<Stock, Integer> soldTableColumn;
    @FXML
    private TableColumn<Stock, Integer> returnedTableColumn;
    @FXML
    private TableColumn<Stock, Integer> subtotalTableColumn;
    @FXML
    private Button printButton;

    private ObservableList<Stock> stocks;
    private StockDaoImpl stockDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stockDao = new StockDaoImpl();
        stocks = FXCollections.observableArrayList();

        Helper.formatDatePicker(dateDatePicker);
        dateDatePicker.getEditor().setDisable(true);
        dateDatePicker.getEditor().setOpacity(1);
        dateDatePicker.setValue(LocalDate.now());

        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            stocks.addAll(stockDao.fetchStocksReport(date));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        stockTableView.setPlaceholder(new Label("Tidak ada data."));
        stockTableView.setItems(stocks);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(stockTableView.getItems().indexOf(data.getValue()) + 1));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        previousStockTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPreviousStock()).asObject());
        addedTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAdded()).asObject());
        soldTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSold()).asObject());
        returnedTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getReturned()).asObject());
        subtotalTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSubtotal()).asObject());

        printButton.setDisable(stockTableView.getItems().isEmpty());
    }

    @FXML
    private void dateDatePickerAction(ActionEvent actionEvent) {
        if (dateDatePicker.getValue() == null) {
            return;
        }

        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            stocks.clear();
            stocks.addAll(stockDao.fetchStocksReport(date));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        stockTableView.setItems(stocks);
        printButton.setDisable(stockTableView.getItems().isEmpty());
    }

    @FXML
    private void printButtonAction(ActionEvent actionEvent) {
        String date = dateDatePicker.getValue().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id")));
        String employee = Common.user.getName();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeNow = Helper.formattedTimeNow();

        new ReportGenerator().printStockReport(stockTableView.getItems(), date, employee, dateNow, timeNow);
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
    private void incomeReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Pendapatan", "income-report-view.fxml");
    }

    @FXML
    private void supplierReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Supplier", "supplier-report-view.fxml");
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
        Helper.changePage(recapMenuButton, "Admin - Rekap Supplier", "supplier-recap-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
