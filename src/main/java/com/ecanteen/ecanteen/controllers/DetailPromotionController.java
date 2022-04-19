package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.entities.Supplier;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DetailPromotionController implements Initializable {
    @FXML
    private Label promotionNameLabel;
    @FXML
    private TableView<Product> detailPromotionTableView;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Promotion selectedPromotion = PromotionController.selectedPromotion;
        ProductDaoImpl productDao = new ProductDaoImpl();
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.detailPromotion(selectedPromotion));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        detailPromotionTableView.setItems(products);
        promotionNameLabel.setText(selectedPromotion.getName());
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(detailPromotionTableView.getItems().indexOf(data.getValue()) + 1));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        purchasePriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurchasePrice()));
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        supplierTableColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSupplier()));
    }
}
