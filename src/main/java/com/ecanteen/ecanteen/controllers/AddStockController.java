package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.StockDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
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
    private TableColumn<Stock, Integer> noTableColumn;
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

    private ObservableList<Stock> stocks;
    private ProductDaoImpl productDao;
    private StockDaoImpl stockDao;
    private Stock selectedStock;
    private String content;
    private int oldStock;
    private int newStock;
    private String oldExpiredDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        ObservableList<Product> products = FXCollections.observableArrayList();
        stockDao = new StockDaoImpl();
        stocks = FXCollections.observableArrayList();
        Common.oldStocks = new ArrayList<>();
        Common.oldExpiredDate = new ArrayList<>();

        try {
            products.addAll(productDao.fetchProductsAddStock());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.toNumberField(amountTextField);
        Helper.addTextLimiter(amountTextField, 4);
        Helper.formatDatePicker(expiredDateDatePicker);
        expiredDateDatePicker.getEditor().setDisable(true);
        expiredDateDatePicker.getEditor().setOpacity(1);
        productComboBox.setItems(products);
        stockTableView.setPlaceholder(new Label("Tidak ada data."));
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(stockTableView.getItems().indexOf(data.getValue()) + 1));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        amountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQty()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getSupplier().getName()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getExpiredDate()));
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
        try {
            stock.setId(stockDao.getNowId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        stock.setProduct(product);
        stock.setBarcode(product.getBarcode());
        stock.setPreviousStock(product.getStockAmount());

        try {
            oldStock = productDao.getStockAmount(stock.getProduct().getBarcode());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        stock.setQty(Integer.parseInt(amountTextField.getText()));
        if (expiredDateDatePicker.getEditor().getText().trim().isEmpty()) {
            stock.getProduct().setExpiredDate("0001-01-01");
        } else {
            stock.getProduct().setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        stock.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        stock.setType("add");

        try {
            if (stockDao.addData(stock) == 1) {
                stocks = stockTableView.getItems();

                oldExpiredDate = productDao.getExpiredDate(stock.getProduct().getBarcode());
                newStock = oldStock + stock.getQty();

                productDao.updateStockAndExpired(newStock, stock.getProduct().getExpiredDate(), stock.getProduct().getBarcode());

                Common.oldStocks.add(oldStock);
                Common.oldExpiredDate.add(oldExpiredDate);

                content = "Data berhasil ditambahkan!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
                printButton.setDisable(false);

                if (expiredDateDatePicker.getEditor().getText().trim().isEmpty()) {
                    stock.getProduct().setExpiredDate("-");
                } else {
                    stock.getProduct().setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                }
                stockTableView.getItems().add(stock);

                resetStock();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
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
        selectedStock.setId(Integer.parseInt(idTextField.getText()));
        selectedStock.setProduct(productComboBox.getValue());
        selectedStock.setBarcode(productComboBox.getValue().getBarcode());
        selectedStock.setPreviousStock(productComboBox.getValue().getStockAmount());
        selectedStock.setQty(Integer.parseInt(amountTextField.getText().trim()));
        if (expiredDateDatePicker.getEditor().getText().trim().isEmpty()) {
            selectedStock.getProduct().setExpiredDate("0001-01-01");
        } else {
            selectedStock.getProduct().setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        selectedStock.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        content = "Anda yakin ingin mengubah?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            try {
                if (stockDao.updateData(selectedStock) == 1) {
                    oldStock = Common.oldStocks.get(stockTableView.getSelectionModel().getSelectedIndex());
                    newStock = oldStock + selectedStock.getQty();
                    productDao.updateStockAndExpired(newStock, selectedStock.getProduct().getExpiredDate(), selectedStock.getProduct().getBarcode());

                    content = "Data berhasil diubah!";
                    Helper.alert(Alert.AlertType.INFORMATION, content);

                    if (expiredDateDatePicker.getEditor().getText().trim().isEmpty()) {
                        selectedStock.getProduct().setExpiredDate("-");
                    } else {
                        selectedStock.getProduct().setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    }
                    stockTableView.getItems().set(stockTableView.getSelectionModel().getSelectedIndex(), selectedStock);

                    resetStock();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        resetError();
        content = "Anda yakin ingin menghapus?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (stockDao.deleteData(selectedStock) == 1) {
                oldStock = Common.oldStocks.get(stockTableView.getSelectionModel().getSelectedIndex());
                oldExpiredDate = LocalDate.parse(Common.oldExpiredDate.get(stockTableView.getSelectionModel().getSelectedIndex())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                newStock = oldStock;
                productDao.updateStockAndExpired(newStock, oldExpiredDate, selectedStock.getProduct().getBarcode());

                stockTableView.getItems().remove(stockTableView.getSelectionModel().getSelectedIndex());
                resetStock();
                content = "Data berhasil dihapus!";
                Helper.alert(Alert.AlertType.INFORMATION, content);

                if (stockTableView.getItems().isEmpty()) {
                    printButton.setDisable(true);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetStock();
    }

    @FXML
    private void stockTableViewClicked(MouseEvent mouseEvent) {
        selectFromTableView();
        stockTableView.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                selectFromTableView();
            }

            if (keyEvent.getCode() == KeyCode.ENTER) {
                productComboBox.requestFocus();
            }
        });
    }

    private void selectFromTableView() {
        selectedStock = stockTableView.getSelectionModel().getSelectedItem();
        if (selectedStock != null) {
            idTextField.setText(String.valueOf(selectedStock.getId()));
            productComboBox.setValue(selectedStock.getProduct());
            amountTextField.setText(String.valueOf(selectedStock.getQty()));
            if (selectedStock.getProduct().getExpiredDate().equals("-")) {
                expiredDateDatePicker.setValue(null);
            } else {
                expiredDateDatePicker.setValue(Helper.formatter(selectedStock.getProduct().getExpiredDate()));
            }
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void printButtonAction(ActionEvent actionEvent) {
        stocks = stockTableView.getItems();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id")));
        String employee = Common.user.getName();
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeNow = Helper.formattedTimeNow();

        new ReportGenerator().printAddReturnStock(stocks, date, employee, "MASUK", dateNow, timeNow);
    }

    private void resetError() {
        productComboBox.setStyle("-fx-border-color: #424242");
        amountTextField.setStyle("-fx-border-color: #424242");
        expiredDateDatePicker.setStyle("-fx-border-color: #424242");
    }

    private void resetStock() {
        idTextField.clear();
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
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(masterMenuButton, "Admin - Produk", "product-view.fxml");
            }
        } else {
            Helper.changePage(masterMenuButton, "Admin - Produk", "product-view.fxml");
        }
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(masterMenuButton, "Admin - Kategori", "category-view.fxml");
            }
        } else {
            Helper.changePage(masterMenuButton, "Admin - Kategori", "category-view.fxml");
        }
    }

    @FXML
    private void returnStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(stockMenuButton, "Admin - Return Stok", "return-stock-view.fxml");
            }
        } else {
            Helper.changePage(stockMenuButton, "Admin - Return Stok", "return-stock-view.fxml");
        }
    }

    @FXML
    private void userButtonAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
            }
        } else {
            Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
        }
    }

    @FXML
    private void supplierButtonAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
            }
        } else {
            Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
        }
    }

    @FXML
    private void stockReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(reportMenuButton, "Admin - Laporan Stok", "stock-report-view.fxml");
            }
        } else {
            Helper.changePage(reportMenuButton, "Admin - Laporan Stok", "stock-report-view.fxml");
        }
    }

    @FXML
    private void incomeReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(reportMenuButton, "Admin - Laporan Pendapatan", "income-report-view.fxml");
            }
        } else {
            Helper.changePage(reportMenuButton, "Admin - Laporan Pendapatan", "income-report-view.fxml");
        }
    }

    @FXML
    private void supplierReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(reportMenuButton, "Admin - Laporan Supplier", "supplier-report-view.fxml");
            }
        } else {
            Helper.changePage(reportMenuButton, "Admin - Laporan Supplier", "supplier-report-view.fxml");
        }
    }

    @FXML
    private void stockRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(recapMenuButton, "Admin - Rekap Stok", "stock-recap-view.fxml");
            }
        } else {
            Helper.changePage(recapMenuButton, "Admin - Rekap Stok", "stock-recap-view.fxml");
        }
    }

    @FXML
    private void incomeRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "income-recap-view.fxml");
            }
        } else {
            Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "income-recap-view.fxml");
        }
    }

    @FXML
    private void supplierRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        if (!stockTableView.getItems().isEmpty()) {
            content = "Data belum dicetak. Apakah akan dicetak?";
            ButtonType result = Helper.alert(Alert.AlertType.NONE, content);
            if (result == ButtonType.YES) {
                printButton.fire();
            } else if (result == ButtonType.NO) {
                Helper.changePage(recapMenuButton, "Admin - Rekap Supplier", "supplier-recap-view.fxml");
            }
        } else {
            Helper.changePage(recapMenuButton, "Admin - Rekap Supplier", "supplier-recap-view.fxml");
        }
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
