package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.IncomeDaoImpl;
import com.ecanteen.ecanteen.entities.Income;
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
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.ResourceBundle;

public class IncomeRecapController implements Initializable {
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
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TableView<Income> incomeTableView;
    @FXML
    private TableColumn<Income, Integer> noTableColumn;
    @FXML
    private TableColumn<Income, String> dateTableColumn;
    @FXML
    private TableColumn<Income, String> productTableColumn;
    @FXML
    private TableColumn<Income, Integer> qtyTableColumn;
    @FXML
    private TableColumn<Income, String> incomeTableColumn;
    @FXML
    private TableColumn<Income, String> profitTableColumn;
    @FXML
    private TextField totalIncomeTextField;
    @FXML
    private TextField totalProfitTextField;
    @FXML
    private Button printButton;

    private IncomeDaoImpl incomeDao;
    private ObservableList<Income> incomes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        incomeDao = new IncomeDaoImpl();
        incomes = FXCollections.observableArrayList();

        Helper.formatDatePicker(fromDatePicker);
        Helper.formatDatePicker(toDatePicker);
        fromDatePicker.getEditor().setDisable(true);
        toDatePicker.getEditor().setDisable(true);
        fromDatePicker.getEditor().setOpacity(1);
        toDatePicker.getEditor().setOpacity(1);
        fromDatePicker.setValue(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
        toDatePicker.setValue(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));

        String fromDate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toDate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            incomes.addAll(incomeDao.fetchIncomeRecap(fromDate, toDate));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        incomeTableView.setPlaceholder(new Label("Tidak ada data."));
        incomeTableView.setItems(incomes);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(incomeTableView.getItems().indexOf(data.getValue()) + 1));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        productTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        qtyTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQty()).asObject());
        incomeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIncome()));
        profitTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProfit()));

        getTotal();
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
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            return;
        }

        String fromDate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toDate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            incomes.clear();
            incomes.addAll(incomeDao.fetchIncomeRecap(fromDate, toDate));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        incomeTableView.setItems(incomes);
        getTotal();
    }

    private void getTotal() {
        getTotal(printButton, incomeTableView, incomes, totalIncomeTextField, totalProfitTextField);
    }

    static void getTotal(Button printButton, TableView<Income> incomeTableView, ObservableList<Income> incomes, TextField totalIncomeTextField, TextField totalProfitTextField) {
        printButton.setDisable(incomeTableView.getItems().isEmpty());

        int totalIncomeInt = 0;
        int totalProfitInt = 0;

        for (Income i : incomes) {
            int incomeInt = Helper.currencyToInt(i.getIncome());
            int profitInt = Helper.currencyToInt(i.getProfit());
            totalIncomeInt += incomeInt;
            totalProfitInt += profitInt;
        }

        String totalIncomeString = Helper.currencyToString(totalIncomeInt);
        String totalProfitString = Helper.currencyToString(totalProfitInt);

        if (totalIncomeInt != 0) {
            totalIncomeTextField.setText(totalIncomeString);
        } else {
            totalIncomeTextField.clear();
        }

        if (totalProfitInt != 0) {
            totalProfitTextField.setText(totalProfitString);
        } else {
            totalProfitTextField.clear();
        }
    }

    @FXML
    private void printButtonAction(ActionEvent actionEvent) {
        incomes = incomeTableView.getItems();

        int totalQtyInt = 0;
        for (Income i : incomes) {
            int qty = i.getQty();
            totalQtyInt += qty;
        }

        String fromDate = fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id")));
        String toDate = toDatePicker.getValue().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id")));
        String employee = Common.user.getName();
        String totalQty = String.valueOf(totalQtyInt);
        String totalIncome = totalIncomeTextField.getText();
        String totalProfit = totalProfitTextField.getText();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeNow = Helper.formattedTimeNow();

        new ReportGenerator().printIncomeRecap(incomes, totalQty, totalIncome, totalProfit, fromDate, toDate, employee, dateNow, timeNow);
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
    private void customerButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(customerMenuButton, "Admin - Pelanggan", "customer-view.fxml");
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
        Helper.changePage(recapMenuButton, "Admin - Laporan Pendapatan", "income-report-view.fxml");
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
