package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supplier;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DetailCategoryController implements Initializable {

    @FXML
    private Label categoryNameLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private TableView<Product> detailCategoryTableView;
    @FXML
    private TableColumn<Product, Integer> noTableColumn;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Category selectedCategory = CategoryController.selectedCategory;
        ProductDaoImpl productDao = new ProductDaoImpl();
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.detailCategory(selectedCategory));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        detailCategoryTableView.setItems(products);
        categoryNameLabel.setText(selectedCategory.getName());
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(detailCategoryTableView.getItems().indexOf(data.getValue()) + 1));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        purchasePriceTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPurchasePrice()).asObject());
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSellingPrice()).asObject());
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSupplier()));
    }

    @FXML
    private void addProductAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
        Stage mainStage = (Stage) stage.getOwner();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("product-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setTitle("Produk | IDC");
        mainStage.setScene(scene);
        mainStage.show();
    }

    @FXML
    private void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
