package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.PromotionDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
    private Button deleteButton;
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
    private TableColumn<Product, Integer> purchasePriceTableColumn;
    @FXML
    private TableColumn<Product, Integer> sellingPriceTableColumn;
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
    static String button;

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

        categoryComboBox.setItems(categories);
        supplierComboBox.setItems(suppliers);
        promotionComboBox.setItems(promotions);
        promotionComboBox.getItems().add(0, null);
        productTableView.setItems(products);
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        purchasePriceTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPurchasePrice()).asObject());
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSellingPrice()).asObject());
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSupplier()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
        promotionTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPromotion()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (barcodeTextField.getText().trim().isEmpty() ||
                nameTextField.getText().isEmpty() ||
                categoryComboBox.getValue() == null ||
                purchasePriceTextField.getText().trim().isEmpty() ||
                sellingPriceTextField.getText().trim().isEmpty() ||
                stockAmountTextField.getText().trim().isEmpty() ||
                supplierComboBox.getValue() == null ||
                expiredDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            Product product = new Product();
            product.setBarcode(barcodeTextField.getText().trim());
            product.setName(nameTextField.getText().trim());
            product.setCategory(categoryComboBox.getValue());
            product.setPurchasePrice(Integer.parseInt(purchasePriceTextField.getText()));
            product.setSellingPrice(Integer.parseInt(sellingPriceTextField.getText()));
            product.setStockAmount(Integer.parseInt(stockAmountTextField.getText().trim()));
            product.setSupplier(supplierComboBox.getValue());
            product.setDateAdded(String.valueOf(LocalDate.now()));
            product.setExpiredDate(String.valueOf(expiredDateDatePicker.getValue()));
            product.setPromotion(promotionComboBox.getValue());

            try {
                if (productDao.addData(product) == 1) {
                    products.clear();
                    products.addAll(productDao.fetchAll());
                    resetProduct();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sukses");
                    alert.setContentText("Data berhasil ditambahkan!");
                    alert.showAndWait();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (barcodeTextField.getText().trim().isEmpty() ||
                nameTextField.getText().isEmpty() ||
                categoryComboBox.getValue() == null ||
                purchasePriceTextField.getText().trim().isEmpty() ||
                sellingPriceTableColumn.getText().trim().isEmpty() ||
                stockAmountTextField.getText().trim().isEmpty() ||
                supplierComboBox.getValue() == null ||
                expiredDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            selectedProduct.setName(nameTextField.getText().trim());
            selectedProduct.setCategory(categoryComboBox.getValue());
            selectedProduct.setPurchasePrice(Integer.parseInt(purchasePriceTextField.getText().trim()));
            selectedProduct.setSellingPrice(Integer.parseInt(sellingPriceTextField.getText().trim()));
            selectedProduct.setStockAmount(Integer.parseInt(stockAmountTextField.getText().trim()));
            selectedProduct.setSupplier(supplierComboBox.getValue());
            selectedProduct.setDateAdded(String.valueOf(LocalDate.now()));
            selectedProduct.setExpiredDate(String.valueOf(expiredDateDatePicker.getValue()));
            selectedProduct.setPromotion(promotionComboBox.getValue());

            try {
                if (productDao.updateData(selectedProduct) == 1) {
                    products.clear();
                    products.addAll(productDao.fetchAll());
                    resetProduct();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Sukses");
                    alert.setContentText("Data berhasil diubah!");
                    alert.showAndWait();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Konfirmasi");
        alert.setContentText("Anda yakin ingin menghapus?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            try {
                if (productDao.deleteData(selectedProduct) == 1) {
                    products.clear();
                    products.addAll(productDao.fetchAll());
                    resetProduct();
                    productTableView.requestFocus();
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setHeaderText("Sukses");
                    alert2.setContentText("Data berhasil dihapus!");
                    alert2.showAndWait();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
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
            expiredDateDatePicker.setValue(LocalDate.parse(selectedProduct.getExpiredDate()));
            if (!selectedProduct.getPromotion().getId().equals("-1")) {
                promotionComboBox.setValue(selectedProduct.getPromotion());
            } else {
                promotionComboBox.setValue(null);
            }
            barcodeTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        FilteredList<Product> filteredList = new FilteredList<>(products, b -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(product -> {
            if (newValue.isEmpty()) {
                return true;
            }

            String searchKeyword = newValue.toLowerCase().trim();

            if (product.getBarcode().toLowerCase().contains(searchKeyword)) {
                return true;
            } else return product.getName().toLowerCase().contains(searchKeyword);
        }));

        SortedList<Product> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(productTableView.comparatorProperty());
        productTableView.setItems(sortedList);
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
        barcodeTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        barcodeTextField.requestFocus();
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Konfirmasi");
        alert.setContentText("Anda yakin ingin keluar?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
