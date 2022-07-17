package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProductController implements Initializable {
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
    private TextField barcodeTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextField purchasePriceTextField;
    @FXML
    private TextField sellingPriceTextField;
    @FXML
    private ComboBox<Supplier> supplierComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button resetButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> noTableColumn;
    @FXML
    private TableColumn<Product, String> barcodeTableColumn;
    @FXML
    private TableColumn<Product, String> nameTableColumn;
    @FXML
    private TableColumn<Product, String> purchasePriceTableColumn;
    @FXML
    private TableColumn<Product, String> sellingPriceTableColumn;
    @FXML
    private TableColumn<Product, Integer> stockAmountTableColumn;
    @FXML
    private TableColumn<Product, Supplier> supplierTableColumn;
    @FXML
    private TableColumn<Product, String> expiredDateTableColumn;

    private ObservableList<Product> products;
    private ProductDaoImpl productDao;
    private Product selectedProduct;
    private String content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        CategoryDaoImpl categoryDao = new CategoryDaoImpl();
        SupplierDaoImpl supplierDao = new SupplierDaoImpl();
        products = FXCollections.observableArrayList();
        ObservableList<Category> categories = FXCollections.observableArrayList();
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.fetchAll());
            categories.addAll(categoryDao.fetchAll());
            suppliers.addAll(supplierDao.fetchActiveSupplier());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.toNumberField(barcodeTextField);
        Helper.toNumberField(sellingPriceTextField);
        Helper.addThousandSeparator(purchasePriceTextField);
        Helper.addThousandSeparator(sellingPriceTextField);
        Helper.addTextLimiter(barcodeTextField, 20);
        Helper.addTextLimiter(nameTextField, 25);
        Helper.addTextLimiter(purchasePriceTextField, 9);
        Helper.addTextLimiter(sellingPriceTextField, 9);
        categoryComboBox.setItems(categories);
        supplierComboBox.setItems(suppliers);
        productTableView.setPlaceholder(new Label("Tidak ada data."));
        productTableView.setItems(products);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(productTableView.getItems().indexOf(data.getValue()) + 1));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        purchasePriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurchasePrice()));
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSupplier()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (productDao.getBarcode(barcodeTextField.getText()) == 1) {
            resetError();
            barcodeTextField.setStyle("-fx-border-color: RED");
            content = "Produk dengan barcode tersebut sudah ada!";
            Helper.alert(Alert.AlertType.ERROR, content);
            barcodeTextField.requestFocus();
            return;
        }

        if (barcodeTextField.getText().trim().isEmpty()) {
            resetError();
            barcodeTextField.setStyle("-fx-border-color: RED");
            content = "Barcode wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            barcodeTextField.requestFocus();
            return;
        }

        if (validateForm()) return;

        resetError();
        Product product = new Product();
        product.setBarcode(barcodeTextField.getText().trim());
        product.setName(nameTextField.getText().trim());
        product.setCategory(categoryComboBox.getValue());
        product.setPurchasePrice(purchasePriceTextField.getText());
        product.setSellingPrice(sellingPriceTextField.getText());
        product.setStockAmount(0);
        product.setSupplier(supplierComboBox.getValue());
        product.setDateAdded(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        product.setExpiredDate("0001-01-01");

        try {
            if (productDao.addData(product) == 1) {
                products.clear();
                products.addAll(productDao.fetchAll());
                resetProduct();
                content = "Data berhasil ditambahkan!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (validateForm()) return;

        resetError();
        selectedProduct.setName(nameTextField.getText().trim());
        selectedProduct.setCategory(categoryComboBox.getValue());
        selectedProduct.setPurchasePrice(purchasePriceTextField.getText().trim());
        selectedProduct.setSellingPrice(sellingPriceTextField.getText().trim());
        selectedProduct.setSupplier(supplierComboBox.getValue());
        selectedProduct.setDateAdded(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        content = "Anda yakin ingin mengubah?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            try {
                if (productDao.updateData(selectedProduct) == 1) {
                    products.clear();
                    products.addAll(productDao.fetchAll());
                    resetProduct();
                    content = "Data berhasil diubah!";
                    Helper.alert(Alert.AlertType.INFORMATION, content);
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (new TransactionDaoImpl().getProductInSale(selectedProduct.getBarcode())) {
            content = "Produk ini pernah dijual, tidak dapat dihapus!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        content = "Anda yakin ingin menghapus?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (productDao.deleteData(selectedProduct) == 1) {
                products.clear();
                products.addAll(productDao.fetchAll());
                resetProduct();
                productTableView.requestFocus();
                content = "Data berhasil dihapus!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetProduct();
    }

    @FXML
    private void productTableViewClicked(MouseEvent mouseEvent) {
        selectFromTableView();
        productTableView.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                selectFromTableView();
            }

            if (keyEvent.getCode() == KeyCode.ENTER) {
                nameTextField.requestFocus();
            }
        });
    }

    private void selectFromTableView() {
        selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            barcodeTextField.setText(selectedProduct.getBarcode());
            nameTextField.setText(selectedProduct.getName());
            categoryComboBox.setValue(selectedProduct.getCategory());
            purchasePriceTextField.setText(String.valueOf(selectedProduct.getPurchasePrice()));
            sellingPriceTextField.setText(String.valueOf(selectedProduct.getSellingPrice()));
            supplierComboBox.setValue(selectedProduct.getSupplier());
            barcodeTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                productTableView.setItems(products);
                return;
            }

            ObservableList<Product> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Product, ?>> columns = productTableView.getColumns();

            for (Product value : products) {
                for (int j = 0; j < 2; j++) {
                    TableColumn<Product, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            productTableView.setItems(tableItems);
        });
    }

    private void resetProduct() {
        barcodeTextField.clear();
        nameTextField.clear();
        categoryComboBox.setValue(null);
        purchasePriceTextField.clear();
        sellingPriceTextField.clear();
        supplierComboBox.setValue(null);
        selectedProduct = null;
        productTableView.getSelectionModel().clearSelection();
        resetError();
        barcodeTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        barcodeTextField.requestFocus();
    }

    private void resetError() {
        barcodeTextField.setStyle("-fx-border-color: #424242");
        nameTextField.setStyle("-fx-border-color: #424242");
        categoryComboBox.setStyle("-fx-border-color: #424242");
        purchasePriceTextField.setStyle("-fx-border-color: #424242");
        sellingPriceTextField.setStyle("-fx-border-color: #424242");
        supplierComboBox.setStyle("-fx-border-color: #424242");
    }

    private boolean validateForm() {
        if (nameTextField.getText().trim().isEmpty()) {
            resetError();
            nameTextField.setStyle("-fx-border-color: RED");
            content = "Nama produk wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            nameTextField.requestFocus();
            return true;
        }

        if (categoryComboBox.getValue() == null) {
            resetError();
            categoryComboBox.setStyle("-fx-border-color: RED");
            content = "Kategori wajib dipilih!";
            Helper.alert(Alert.AlertType.ERROR, content);
            categoryComboBox.requestFocus();
            return true;
        }

        if (purchasePriceTextField.getText().trim().isEmpty()) {
            resetError();
            purchasePriceTextField.setStyle("-fx-border-color: RED");
            content = "Harga beli wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            purchasePriceTextField.requestFocus();
            return true;
        }

        if (sellingPriceTextField.getText().trim().isEmpty()) {
            resetError();
            sellingPriceTextField.setStyle("-fx-border-color: RED");
            content = "Harga jual wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            sellingPriceTextField.requestFocus();
            return true;
        }

        if (supplierComboBox.getValue() == null) {
            resetError();
            supplierComboBox.setStyle("-fx-border-color: RED");
            content = "Supplier wajib dipilih!";
            Helper.alert(Alert.AlertType.ERROR, content);
            supplierComboBox.requestFocus();
            return true;
        }

        return false;
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
        content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
