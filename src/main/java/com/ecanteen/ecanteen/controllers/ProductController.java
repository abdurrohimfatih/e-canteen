package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.PromotionDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProductController implements Initializable {
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
    private MenuItem promotionMenuItem;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button customerMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private Button historyMenuButton;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button profileButton;
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
    private TextField stockAmountTextField;
    @FXML
    private ComboBox<Supplier> supplierComboBox;
    @FXML
    private DatePicker expiredDateDatePicker;
    @FXML
    private ComboBox<Promotion> promotionComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button resetButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Product> productTableView;
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
    @FXML
    private TableColumn<Product, Promotion> promotionTableColumn;

    private ObservableList<Product> products;
    private ProductDaoImpl productDao;
    private Product selectedProduct;
    private String content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        CategoryDaoImpl categoryDao = new CategoryDaoImpl();
        SupplierDaoImpl supplierDao = new SupplierDaoImpl();
        PromotionDaoImpl promotionDao = new PromotionDaoImpl();
        products = FXCollections.observableArrayList();
        ObservableList<Category> categories = FXCollections.observableArrayList();
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        ObservableList<Promotion> promotions = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.fetchAll());
            categories.addAll(categoryDao.fetchAll());
            suppliers.addAll(supplierDao.fetchAll());
            promotions.addAll(promotionDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        profileButton.setText(Common.user.getName());
        Helper.toNumberField(barcodeTextField);
        Helper.toNumberField(sellingPriceTextField);
        Helper.toNumberField(stockAmountTextField);
        Helper.addThousandSeparator(purchasePriceTextField);
        Helper.addThousandSeparator(sellingPriceTextField);
        Helper.addTextLimiter(barcodeTextField, 20);
        Helper.addTextLimiter(nameTextField, 100);
        Helper.addTextLimiter(purchasePriceTextField, 9);
        Helper.addTextLimiter(sellingPriceTextField, 9);
        Helper.addTextLimiter(stockAmountTextField, 11);
        Helper.formatDatePicker(expiredDateDatePicker);
        categoryComboBox.setItems(categories);
        supplierComboBox.setItems(suppliers);
        promotionComboBox.setItems(promotions);
        promotionComboBox.getItems().add(0, null);
        productTableView.setItems(products);
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        purchasePriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurchasePrice()));
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSupplier()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
        promotionTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPromotion()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (barcodeTextField.getText().trim().isEmpty()) {
            resetError();
            barcodeTextField.setStyle("-fx-border-color: RED");
            content = "Barcode wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            barcodeTextField.requestFocus();
        } else if (nameTextField.getText().trim().isEmpty()) {
            resetError();
            nameTextField.setStyle("-fx-border-color: RED");
            content = "Nama produk wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            nameTextField.requestFocus();
        } else if (categoryComboBox.getValue() == null) {
            resetError();
            categoryComboBox.setStyle("-fx-border-color: RED");
            content = "Kategori wajib dipilih!";
            Helper.alert(Alert.AlertType.ERROR, content);
            categoryComboBox.requestFocus();
        } else if (purchasePriceTextField.getText().trim().isEmpty()) {
            resetError();
            purchasePriceTextField.setStyle("-fx-border-color: RED");
            content = "Harga beli wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            purchasePriceTextField.requestFocus();
        } else if (sellingPriceTextField.getText().trim().isEmpty()) {
            resetError();
            sellingPriceTextField.setStyle("-fx-border-color: RED");
            content = "Harga jual wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            sellingPriceTextField.requestFocus();
        } else if (stockAmountTextField.getText().trim().isEmpty()) {
            resetError();
            stockAmountTextField.setStyle("-fx-border-color: RED");
            content = "Jumlah stok wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            stockAmountTextField.requestFocus();
        } else if (supplierComboBox.getValue() == null) {
            resetError();
            supplierComboBox.setStyle("-fx-border-color: RED");
            content = "Supplier wajib dipilih!";
            Helper.alert(Alert.AlertType.ERROR, content);
            supplierComboBox.requestFocus();
        } else if (expiredDateDatePicker.getValue() == null) {
            resetError();
            expiredDateDatePicker.setStyle("-fx-border-color: RED");
            content = "Tanggal kedaluwarsa wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            expiredDateDatePicker.requestFocus();
        } else if (productDao.getBarcode(barcodeTextField.getText()) == 1) {
            resetError();
            barcodeTextField.setStyle("-fx-border-color: RED");
            content = "Produk dengan barcode tersebut sudah ada!";
            Helper.alert(Alert.AlertType.ERROR, content);
            barcodeTextField.requestFocus();
        } else {
            resetError();
            Product product = new Product();
            product.setBarcode(barcodeTextField.getText().trim());
            product.setName(nameTextField.getText().trim());
            product.setCategory(categoryComboBox.getValue());
            product.setPurchasePrice(purchasePriceTextField.getText());
            product.setSellingPrice(sellingPriceTextField.getText());
            product.setStockAmount(Integer.parseInt(stockAmountTextField.getText().trim()));
            product.setSupplier(supplierComboBox.getValue());
            product.setDateAdded(Helper.formattedDateNow());
            product.setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            product.setPromotion(promotionComboBox.getValue());

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
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty()) {
            resetError();
            nameTextField.setStyle("-fx-border-color: RED");
            content = "Nama produk wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            nameTextField.requestFocus();
        } else if (categoryComboBox.getValue() == null) {
            resetError();
            categoryComboBox.setStyle("-fx-border-color: RED");
            content = "Kategori wajib dipilih!";
            Helper.alert(Alert.AlertType.ERROR, content);
            categoryComboBox.requestFocus();
        } else if (purchasePriceTextField.getText().trim().isEmpty()) {
            resetError();
            purchasePriceTextField.setStyle("-fx-border-color: RED");
            content = "Harga beli wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            purchasePriceTextField.requestFocus();
        } else if (sellingPriceTextField.getText().trim().isEmpty()) {
            resetError();
            sellingPriceTextField.setStyle("-fx-border-color: RED");
            content = "Harga jual wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            sellingPriceTextField.requestFocus();
        } else if (stockAmountTextField.getText().trim().isEmpty()) {
            resetError();
            stockAmountTextField.setStyle("-fx-border-color: RED");
            content = "Jumlah stok wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            stockAmountTextField.requestFocus();
        } else if (supplierComboBox.getValue() == null) {
            resetError();
            supplierComboBox.setStyle("-fx-border-color: RED");
            content = "Supplier wajib dipilih!";
            Helper.alert(Alert.AlertType.ERROR, content);
            supplierComboBox.requestFocus();
        } else if (expiredDateDatePicker.getValue() == null) {
            resetError();
            expiredDateDatePicker.setStyle("-fx-border-color: RED");
            content = "Tanggal kedaluwarsa wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
            expiredDateDatePicker.requestFocus();
        } else {
            resetError();
            selectedProduct.setName(nameTextField.getText().trim());
            selectedProduct.setCategory(categoryComboBox.getValue());
            selectedProduct.setPurchasePrice(purchasePriceTextField.getText().trim());
            selectedProduct.setSellingPrice(sellingPriceTextField.getText().trim());
            selectedProduct.setStockAmount(Integer.parseInt(stockAmountTextField.getText().trim()));
            selectedProduct.setSupplier(supplierComboBox.getValue());
            selectedProduct.setDateAdded(Helper.formattedDateNow());
            selectedProduct.setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            selectedProduct.setPromotion(promotionComboBox.getValue());

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
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetProduct();
    }

    @FXML
    private void productTableViewClicked(MouseEvent mouseEvent) {
        selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            barcodeTextField.setText(selectedProduct.getBarcode());
            nameTextField.setText(selectedProduct.getName());
            categoryComboBox.setValue(selectedProduct.getCategory());
            purchasePriceTextField.setText(String.valueOf(selectedProduct.getPurchasePrice()));
            sellingPriceTextField.setText(String.valueOf(selectedProduct.getSellingPrice()));
            stockAmountTextField.setText(String.valueOf(selectedProduct.getStockAmount()));
            supplierComboBox.setValue(selectedProduct.getSupplier());
            expiredDateDatePicker.setValue(Helper.formatter(selectedProduct.getExpiredDate()));
            if (!selectedProduct.getPromotion().getId().equals("-1")) {
                promotionComboBox.setValue(selectedProduct.getPromotion());
            } else {
                promotionComboBox.setValue(null);
            }
            barcodeTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
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
        stockAmountTextField.clear();
        supplierComboBox.setValue(null);
        expiredDateDatePicker.setValue(null);
        promotionComboBox.setValue(null);
        selectedProduct = null;
        productTableView.getSelectionModel().clearSelection();
        resetError();
        barcodeTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        resetButton.setDisable(true);
        barcodeTextField.requestFocus();
    }

    private void resetError() {
        barcodeTextField.setStyle("-fx-border-color: #424242");
        nameTextField.setStyle("-fx-border-color: #424242");
        categoryComboBox.setStyle("-fx-border-color: #424242");
        purchasePriceTextField.setStyle("-fx-border-color: #424242");
        sellingPriceTextField.setStyle("-fx-border-color: #424242");
        stockAmountTextField.setStyle("-fx-border-color: #424242");
        supplierComboBox.setStyle("-fx-border-color: #424242");
        expiredDateDatePicker.setStyle("-fx-border-color: #424242");
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void promotionMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Promosi", "promotion-view.fxml");
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
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
