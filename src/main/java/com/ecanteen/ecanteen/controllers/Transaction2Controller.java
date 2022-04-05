package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.SaleDao;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Sale;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.EditingCell;
import com.ecanteen.ecanteen.utils.Helper;
import com.ecanteen.ecanteen.utils.ReportGenerator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Transaction2Controller implements Initializable {
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
    private TableColumn<Sale, Double> subtotalSaleTableColumn;
    @FXML
    private TextField totalAmountTextField;
    @FXML
    private Button printSaleButton;
    @FXML
    private Button resetSaleButton;

    private ProductDaoImpl productDao;
    private SaleDao saleDao;
    private ObservableList<Sale> saleData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        saleDao = new SaleDao();
        profileButton.setText(Common.user.getName());
        saleData = FXCollections.observableArrayList();

        barcodeSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        sellingPriceSaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSellingPrice()).asObject());
        quantitySaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        discountSaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDiscount()).asObject());
        subtotalSaleTableColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        Callback<TableColumn<Sale, Integer>, TableCell<Sale, Integer>> cellFactory = p -> new EditingCell();
        quantitySaleTableColumn.setCellFactory(cellFactory);
        quantitySaleTableColumn.setOnEditCommit(t -> {
            t.getTableView().getItems().get(t.getTablePosition().getRow()).setQuantity(t.getNewValue());
            double qty = t.getTableView().getItems().get(t.getTablePosition().getRow()).getQuantity();
            double discountPercent = t.getTableView().getItems().get(t.getTablePosition().getRow()).getDiscount();
            double sellingPrice = t.getRowValue().getSellingPrice();
            double subtotalStart = sellingPrice * qty;
            double subtotal;
            if (discountPercent != 0) {
                double discountAmount = subtotalStart * discountPercent / 100;
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
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() || !dialog.getResult().equals("0") || !dialog.getResult().trim().isEmpty()) {
            StringBuilder barcodes = new StringBuilder();
            StringBuilder qts = new StringBuilder();
            for (Sale saleDatum : saleData) {
                barcodes.append(saleDatum.getBarcode());
                barcodes.append(",");
                qts.append(saleDatum.getQuantity());
                qts.append(",");
            }

            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            try {
                Common.saleId = String.valueOf(saleDao.getNowSaleId());
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            Common.date = currentDate;
            Common.time = currentTime;
            Common.totalAmount = Double.parseDouble(totalAmountTextField.getText());
            Common.payAmount = Double.parseDouble(result.get());
            Common.change = Common.payAmount - Common.totalAmount;

            Sale sale = new Sale();
            sale.setId(Common.saleId);
            sale.setUsername(Common.user.getName());
            sale.setDate(Common.date);
            sale.setTime(Common.time);
            sale.setBarcodes(String.valueOf(barcodes));
            sale.setQts(String.valueOf(qts));
            sale.setTotalAmount(Common.totalAmount);

            try {
                if (saleDao.addSale(sale) == 1) {
                    String[] soldBarcode = barcodes.toString().split(",");
                    String[] soldQty = qts.toString().split(",");

                    for (int i = 0; i < soldBarcode.length; i++) {
                        int oldStock = productDao.getStockAmount(soldBarcode[i]);
                        int newStock = oldStock - Integer.parseInt(soldQty[i]);
                        productDao.updateStock(newStock, soldBarcode[i]);
                    }

                    try {
                        new ReportGenerator().generateInvoice(saleData);
                    } catch (JRException e) {
                        e.printStackTrace();
                    }

                    barcodeTextField.requestFocus();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
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
