package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.entities.Transaction;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.EditingCell;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.ReportGenerator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField barcodeTextField;
    @FXML
    private Button addProductButton;
    @FXML
    private Button resetProductButton;
    @FXML
    private TableView<Sale> saleTableView;
    @FXML
    private TableColumn<Sale, String> barcodeSaleTableColumn;
    @FXML
    private TableColumn<Sale, String> nameSaleTableColumn;
    @FXML
    private TableColumn<Sale, Integer> sellingPriceSaleTableColumn;
    @FXML
    private TableColumn<Sale, Integer> quantitySaleTableColumn;
    @FXML
    private TableColumn<Sale, Integer> discountSaleTableColumn;
    @FXML
    private TableColumn<Sale, Integer> subtotalSaleTableColumn;
    @FXML
    private TextField totalAmountTextField;
    @FXML
    private Button printSaleButton;
    @FXML
    private Button resetSaleButton;

    private ProductDaoImpl productDao;
    private TransactionDaoImpl transactionDao;
    private ObservableList<Sale> saleData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        transactionDao = new TransactionDaoImpl();
        profileButton.setText(Common.user.getName());
        saleData = FXCollections.observableArrayList();

        barcodeSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        sellingPriceSaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSellingPrice()).asObject());
        quantitySaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        discountSaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDiscount()).asObject());
        subtotalSaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSubtotal()).asObject());

        Callback<TableColumn<Sale, Integer>, TableCell<Sale, Integer>> cellFactory = p -> new EditingCell();
        quantitySaleTableColumn.setCellFactory(cellFactory);
        quantitySaleTableColumn.setOnEditCommit(t -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setQuantity(t.getNewValue());
            int qty = t.getTableView().getItems().get(t.getTablePosition().getRow()).getQuantity();
            int discountPercent = t.getTableView().getItems().get(t.getTablePosition().getRow()).getDiscount();
            int sellingPrice = t.getRowValue().getSellingPrice();
            int subtotalStart = sellingPrice * qty;
            int subtotal;
            if (discountPercent != 0) {
                int discountAmount = subtotalStart * discountPercent / 100;
                subtotal = subtotalStart - discountAmount;
                t.getRowValue().setSubtotal(subtotal);
                saleTableView.refresh();
                int totalAmount = 0;
                for (Sale i : saleTableView.getItems()) {
                    totalAmount += i.getSubtotal();
                }
                totalAmountTextField.setText(String.valueOf(totalAmount));
            }
            t.getTableView().getSelectionModel().clearSelection();
        });

        saleTableView.setRowFactory(saleTableView -> {
            final TableRow<Sale> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem removeMenuItem = new MenuItem("Hapus");
            removeMenuItem.setOnAction(actionEvent -> {
                saleTableView.getItems().remove(row.getItem());
                int totalAmount = 0;
                for (Sale i : saleTableView.getItems()) {
                    totalAmount += i.getSubtotal();
                }
                totalAmountTextField.setText(String.valueOf(totalAmount));
            });
            contextMenu.getItems().add(removeMenuItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu)
            );
            return row;
        });
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

    @FXML
    private void addProductButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Product product = productDao.fetchProduct(barcodeTextField.getText().trim());
        if (product == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Tidak ditemukan");
            alert.setContentText("Produk dengan barcode tersebut tidak ditemukan");
            alert.showAndWait();
        } else {
            Sale sale = new Sale();
            sale.setBarcode(product.getBarcode());
            sale.setName(product.getName());
            sale.setSellingPrice(product.getSellingPrice());
            sale.setQuantity(1);
            sale.setDiscount(productDao.getDiscount(barcodeTextField.getText()));
            int subtotalStart = sale.getSellingPrice() * sale.getQuantity() * sale.getDiscount() / 100;
            int subtotal = sale.getSellingPrice() * sale.getQuantity() - subtotalStart;
            sale.setSubtotal(subtotal);
            saleTableView.getItems().add(sale);
            int totalAmount = 0;
            for (Sale i : saleTableView.getItems()) {
                totalAmount += i.getSubtotal();
            }
            totalAmountTextField.setText(String.valueOf(totalAmount));
            barcodeTextField.clear();
        }
    }

    @FXML
    private void resetProductButtonAction(ActionEvent actionEvent) {
        barcodeTextField.clear();
    }

    @FXML
    private void saleTableViewClicked(MouseEvent mouseEvent) {
    }

    @FXML
    private void printSaleButtonAction(ActionEvent actionEvent) {
        saleData = saleTableView.getItems();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Jumlah uang");
        dialog.setHeaderText("Jumlah uang yang dibayarkan");
        Optional<String> result;

        StringBuilder barcodes = new StringBuilder();
        StringBuilder qts = new StringBuilder();
        for (Sale item : saleData) {
            barcodes.append(item.getBarcode());
            barcodes.append(",");
            qts.append(item.getQuantity());
            qts.append(",");
        }

        Transaction transaction = new Transaction();
        try {
            transaction.setId(String.valueOf(transactionDao.getNowSaleId()));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        transaction.setUsername(Common.user.getName());
        transaction.setDate(Helper.formattedDateNow());
        transaction.setTime(Helper.formattedTimeNow());
        transaction.setBarcodes(String.valueOf(barcodes));
        transaction.setQts(String.valueOf(qts));
        transaction.setTotalAmount(Integer.parseInt(totalAmountTextField.getText()));

        do {
            result = dialog.showAndWait();
            transaction.setPayAmount(Integer.parseInt(result.get()));
            transaction.setChange(transaction.getPayAmount() - transaction.getTotalAmount());
        } while (transaction.getTotalAmount() > transaction.getPayAmount() || result.get().trim().isEmpty());

        try {
            if (transactionDao.addData(transaction) == 1) {
                String[] soldBarcode = barcodes.toString().split(",");
                String[] soldQty = qts.toString().split(",");

                for (int i = 0; i < soldBarcode.length; i++) {
                    int oldStock = productDao.getStockAmount(soldBarcode[i]);
                    int newStock = oldStock - Integer.parseInt(soldQty[i]);
                    productDao.updateStock(newStock, soldBarcode[i]);
                }

                new ReportGenerator().generateInvoice(saleData, transaction);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetSaleButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Konfirmasi");
        alert.setContentText("Anda yakin ingin reset?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            resetSale();
        }
    }

    private void resetSale() {
        barcodeTextField.clear();
        saleTableView.getItems().clear();
        totalAmountTextField.setText("");
    }
}
