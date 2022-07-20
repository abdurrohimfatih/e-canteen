package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.utils.Common;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class DetailProductCashierController implements Initializable {
    @FXML
    private AnchorPane containerPane;
    @FXML
    private Button backButton;
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
    private TableColumn<Product, String> sellingPriceTableColumn;
    @FXML
    private TableColumn<Product, Integer> stockAmountTableColumn;
    @FXML
    private TableColumn<Product, String> expiredDateTableColumn;

    private Product selectedProduct;
    private ObservableList<Product> products;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ProductDaoImpl productDao = new ProductDaoImpl();
        products = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.fetchProductsReturnStock());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        productTableView.setPlaceholder(new Label("Tidak ada data."));
        productTableView.setItems(products);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(productTableView.getItems().indexOf(data.getValue()) + 1));
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
    }

    @FXML
    private void productTableViewClicked(MouseEvent mouseEvent) {
        selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            if (mouseEvent.getClickCount() > 1) {
                Common.sale = addProduct(selectedProduct);
                ((Stage) containerPane.getScene().getWindow()).close();
            }
        }
    }

    @FXML
    private void productTableViewKeyReleased(KeyEvent keyEvent) {
        selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                selectedProduct = productTableView.getSelectionModel().getSelectedItem();
            }

            if (keyEvent.getCode() == KeyCode.ENTER) {
                Common.sale = addProduct(selectedProduct);
                ((Stage) containerPane.getScene().getWindow()).close();
            }
        }
    }

    private Sale addProduct(Product selectedProduct) {
        Sale sale = new Sale();
        sale.setBarcode(selectedProduct.getBarcode());
        sale.setName(selectedProduct.getName());
        sale.setSellingPrice(selectedProduct.getSellingPrice());
        sale.setQuantity(1);

        String subtotalString;
        String[] selling = sale.getSellingPrice().split("\\.");
        StringBuilder price = new StringBuilder();
        for (String s : selling) {
            price.append(s);
        }

        int sellingInt = Integer.parseInt(String.valueOf(price));
        int subtotalStart = sellingInt * sale.getQuantity();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        subtotalString = formatter.format(subtotalStart);

        sale.setSubtotal(subtotalString);

        return sale;
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
                for (int j = 1; j < 3; j++) {
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

    @FXML
    private void containerPaneKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            Stage stage = (Stage) containerPane.getScene().getWindow();
            stage.close();
        }

        if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
            productTableView.requestFocus();
        }
    }

    @FXML
    private void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
