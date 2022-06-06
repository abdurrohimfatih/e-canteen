package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Stock;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddStockController implements Initializable {
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
    private TextField idTextField;
    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField amountTextField;
    @FXML
    private DatePicker expiredDateDatePicker;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button resetButton;
    @FXML
    private TableView<Stock> stockTableView;
    @FXML
    private TableColumn<Stock, String> barcodeTableColumn;
    @FXML
    private TableColumn<Stock, String> nameTableColumn;
    @FXML
    private TableColumn<Stock, Integer> amountTableColumn;
    @FXML
    private TableColumn<Stock, String> supplierTableColumn;
    @FXML
    private TableColumn<Stock, String> expiredDateTableColumn;
    @FXML
    private Button printButton;

    private ObservableList<Product> products;
    private ProductDaoImpl productDao;
    private String content;
    private Stock selectedStock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        products = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.toNumberField(amountTextField);
        Helper.addTextLimiter(amountTextField, 11);
        Helper.addTextLimiter(expiredDateDatePicker.getEditor(), 10);
        Helper.formatDatePicker(expiredDateDatePicker);
        expiredDateDatePicker.getEditor().setDisable(true);
        expiredDateDatePicker.getEditor().setOpacity(1);
        productComboBox.setItems(products);
        stockTableView.setPlaceholder(new Label("Tidak ada data."));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        amountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQty()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getSupplier().getName()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (productComboBox.getValue() == null) {
            resetError();
            productComboBox.setStyle("-fx-border-color: RED");
            content = "Pilih produk terlebih dahulu!";
            Helper.alert(Alert.AlertType.ERROR, content);
            productComboBox.requestFocus();
            return;
        }

        if (amountTextField.getText().trim().isEmpty() || Integer.parseInt(amountTextField.getText().trim()) < 1) {
            resetError();
            amountTextField.setStyle("-fx-border-color: RED");
            content = "Jumlah tidak boleh kosong!";
            Helper.alert(Alert.AlertType.ERROR, content);
            amountTextField.requestFocus();
            return;
        }

        resetError();
        Product product = productComboBox.getValue();
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQty(Integer.parseInt(amountTextField.getText()));
        if (expiredDateDatePicker.getEditor().getText().trim().isEmpty()) {
            stock.setExpiredDate("-");
        } else {
            stock.setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        stock.setType("add");
        stockTableView.getItems().add(stock);
        resetButtonAction(actionEvent);
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        resetError();
        selectedStock.setProduct(productComboBox.getValue());
        selectedStock.setQty(Integer.parseInt(amountTextField.getText().trim()));
        if (expiredDateDatePicker.getEditor().getText().trim().isEmpty()) {
            selectedStock.setExpiredDate("-");
        } else {
            selectedStock.setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }

        content = "Anda yakin ingin mengubah?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            stockTableView.getItems().set(stockTableView.getSelectionModel().getSelectedIndex(), selectedStock);
            resetButtonAction(actionEvent);
            content = "Data berhasil diubah!";
            Helper.alert(Alert.AlertType.INFORMATION, content);
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        resetError();
        content = "Anda yakin ingin menghapus?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        stockTableView.getItems().remove(stockTableView.getSelectionModel().getSelectedIndex());
        resetButtonAction(actionEvent);
        content = "Data berhasil dihapus!";
        Helper.alert(Alert.AlertType.INFORMATION, content);
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        productComboBox.setValue(null);
        amountTextField.clear();
        expiredDateDatePicker.setValue(null);
        selectedStock = null;
        stockTableView.getSelectionModel().clearSelection();
        resetError();
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        productComboBox.requestFocus();
    }

    @FXML
    private void stockTableViewClicked(MouseEvent mouseEvent) {
        selectedStock = stockTableView.getSelectionModel().getSelectedItem();
        if (selectedStock != null) {
            productComboBox.setValue(selectedStock.getProduct());
            amountTextField.setText(String.valueOf(selectedStock.getQty()));
            if (selectedStock.getExpiredDate().equals("-")) {
                expiredDateDatePicker.setValue(null);
            } else {
                expiredDateDatePicker.setValue(Helper.formatter(selectedStock.getExpiredDate()));
            }
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void printButtonAction(ActionEvent actionEvent) {
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
        Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "supplier-recap-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }

    private void resetError() {
        productComboBox.setStyle("-fx-border-color: #424242");
        amountTextField.setStyle("-fx-border-color: #424242");
        expiredDateDatePicker.setStyle("-fx-border-color: #424242");
    }
}
