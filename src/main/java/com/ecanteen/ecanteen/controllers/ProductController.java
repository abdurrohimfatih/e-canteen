package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supplier;
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
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProductController implements Initializable {
    @FXML
    private Button productMenuButton;
    @FXML
    private Button categoryMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField barcodeTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextField priceTextField;
    @FXML
    private TextField stockAmountTextField;
    @FXML
    private ComboBox<Supplier> supplierComboBox;
    @FXML
    private DatePicker dateAddedDatePicker;
    @FXML
    private DatePicker expiredDateDatePicker;
    @FXML
    private TextField countTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label infoLabel;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, String> barcodeTableColumn;
    @FXML
    private TableColumn<Product, String> nameTableColumn;
    @FXML
    private TableColumn<Product, Category> categoryTableColumn;
    @FXML
    private TableColumn<Product, Integer> priceTableColumn;
    @FXML
    private TableColumn<Product, Integer> stockAmountTableColumn;
    @FXML
    private TableColumn<Product, Supplier> supplierTableColumn;
    @FXML
    private TableColumn<Product, String> dateAddedTableColumn;
    @FXML
    private TableColumn<Product, String> expiredDateTableColumn;
    @FXML
    private TableColumn<Product, Integer> countTableColumn;

    private ObservableList<Product> products;
    private ProductDaoImpl productDao;
    private Product selectedProduct;

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
            suppliers.addAll(supplierDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        categoryComboBox.setItems(categories);
        supplierComboBox.setItems(suppliers);
        dateAddedDatePicker.setValue(LocalDate.now());

        productTableView.setItems(products);
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        categoryTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCategory()));
        priceTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPrice()).asObject());
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSupplier()));
        dateAddedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateAdded()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
        countTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCount()).asObject());
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (barcodeTextField.getText().trim().isEmpty() ||
                nameTextField.getText().isEmpty() ||
                categoryComboBox.getValue() == null ||
                priceTextField.getText().trim().isEmpty() ||
                stockAmountTextField.getText().trim().isEmpty() ||
                supplierComboBox.getValue() == null ||
                expiredDateDatePicker.getValue() == null ||
                countTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            Product product = new Product();
            product.setBarcode(barcodeTextField.getText().trim());
            product.setName(nameTextField.getText().trim());
            product.setCategory(categoryComboBox.getValue());
            product.setPrice(Integer.parseInt(priceTextField.getText()));
            product.setStockAmount(Integer.parseInt(stockAmountTextField.getText().trim()));
            product.setSupplier(supplierComboBox.getValue());
            product.setDateAdded(String.valueOf(dateAddedDatePicker.getValue()));
            product.setExpiredDate(String.valueOf(expiredDateDatePicker.getValue()));
            product.setCount(Integer.parseInt(countTextField.getText().trim()));

            try {
                if (productDao.addData(product) == 1) {
                    products.clear();
                    products.addAll(productDao.fetchAll());
                    resetProduct();
                    infoLabel.setText("Data berhasil ditambahkan!");
                    infoLabel.setStyle("-fx-text-fill: green");
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
                priceTextField.getText().trim().isEmpty() ||
                stockAmountTextField.getText().trim().isEmpty() ||
                supplierComboBox.getValue() ==  null ||
                expiredDateDatePicker.getValue() == null ||
                countTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            selectedProduct.setName(nameTextField.getText().trim());
            selectedProduct.setCategory(categoryComboBox.getValue());
            selectedProduct.setPrice(Integer.parseInt(priceTextField.getText().trim()));
            selectedProduct.setStockAmount(Integer.parseInt(stockAmountTextField.getText().trim()));
            selectedProduct.setSupplier(supplierComboBox.getValue());
            selectedProduct.setDateAdded(String.valueOf(dateAddedDatePicker.getValue()));
            selectedProduct.setExpiredDate(String.valueOf(expiredDateDatePicker.getValue()));
            selectedProduct.setCount(Integer.parseInt(countTextField.getText().trim()));

            try {
                if (productDao.updateData(selectedProduct) == 1) {
                    products.clear();
                    products.addAll(productDao.fetchAll());
                    resetProduct();
                    infoLabel.setText("Data berhasil diubah!");
                    infoLabel.setStyle("-fx-text-fill: green");
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
                    infoLabel.setText("Data berhasil dihapus!");
                    infoLabel.setStyle("-fx-text-fill: green");
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
            priceTextField.setText(String.valueOf(selectedProduct.getPrice()));
            stockAmountTextField.setText(String.valueOf(selectedProduct.getStockAmount()));
            supplierComboBox.setValue(selectedProduct.getSupplier());
            dateAddedDatePicker.setValue(LocalDate.parse(selectedProduct.getDateAdded()));
            expiredDateDatePicker.setValue(LocalDate.parse(selectedProduct.getExpiredDate()));
            countTextField.setText(String.valueOf(selectedProduct.getCount()));

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
        priceTextField.clear();
        stockAmountTextField.clear();
        supplierComboBox.setValue(null);
        dateAddedDatePicker.setValue(LocalDate.now());
        expiredDateDatePicker.setValue(null);
        countTextField.clear();
        selectedProduct = null;
        productTableView.getSelectionModel().clearSelection();
        barcodeTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        barcodeTextField.requestFocus();
        infoLabel.setText("");
    }

    @FXML
    private void categoryMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Stage categoryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("category-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        categoryStage.setTitle("Kategori | e-Canteen");
        categoryStage.setMaximized(true);
        categoryStage.setScene(scene);
        categoryStage.show();

        Stage stage = (Stage) categoryMenuButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void supplierMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Stage supplierStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("supplier-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        supplierStage.setTitle("Supplier | e-Canteen");
        supplierStage.setMaximized(true);
        supplierStage.setScene(scene);
        supplierStage.show();

        Stage stage = (Stage) supplierMenuButton.getScene().getWindow();
        stage.close();
    }
}
