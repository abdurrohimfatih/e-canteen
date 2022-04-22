package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    @FXML
    private Button transactionMenuButton;
    @FXML
    private MenuButton reportMenuButton;
    @FXML
    private MenuItem incomeMenuItem;
    @FXML
    private MenuItem soldProductMenuItem;
    @FXML
    private MenuItem favoriteProductMenuItem;
//    @FXML
//    private MenuButton stockMenuButton;
//    @FXML
//    private MenuItem productMenuItem;
//    @FXML
//    private MenuItem promotionMenuItem;
    @FXML
    private Button productMenuButton;
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
    private TableColumn<Sale, String> sellingPriceSaleTableColumn;
    @FXML
    private TableColumn<Sale, Integer> quantitySaleTableColumn;
//    @FXML
//    private TableColumn<Sale, String> discountSaleTableColumn;
    @FXML
    private TableColumn<Sale, String> subtotalSaleTableColumn;
//    @FXML
//    private TextField totalAllTextField;
//    @FXML
//    private TextField totalDiscountTextField;
    @FXML
    private TextField totalAmountTextField;
    @FXML
    private Button printSaleButton;
    @FXML
    private Button resetSaleButton;

    private ProductDaoImpl productDao;
    private TransactionDaoImpl transactionDao;
    private ObservableList<Sale> saleData;
    private String content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        transactionDao = new TransactionDaoImpl();
        profileButton.setText(Common.user.getName());
        saleData = FXCollections.observableArrayList();

        barcodeSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        sellingPriceSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        quantitySaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
//        discountSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDiscountAmount()));
        subtotalSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubtotal()));

        Callback<TableColumn<Sale, Integer>, TableCell<Sale, Integer>> cellFactory = p -> new EditingCell();
        quantitySaleTableColumn.setCellFactory(cellFactory);
        quantitySaleTableColumn.setOnEditCommit(t -> {
            t.getRowValue().setQuantity(t.getNewValue());

            String sellingPrice = t.getRowValue().getSellingPrice();
            int qty = t.getRowValue().getQuantity();
//            int discount = t.getRowValue().getDiscount();

//            int discountAmountInt;
//            String discountAmountString;
            int subtotalInt;
            String subtotalString;
            String[] selling = sellingPrice.split("\\.");
            StringBuilder price = new StringBuilder();
            for (String s : selling) {
                price.append(s);
            }
            int sellingInt = Integer.parseInt(String.valueOf(price));
            int subtotalStart = sellingInt * qty;
//            if (discount != 0) {
//                discountAmountInt = subtotalStart * discount / 100;
//            } else {
//                discountAmountInt = 0;
//            }

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);

//            discountAmountString = formatter.format(discountAmountInt);
            subtotalString = formatter.format(subtotalStart);

//            t.getRowValue().setDiscountAmount(discountAmountString);
            t.getRowValue().setSubtotal(subtotalString);

            saleTableView.refresh();
//            int totalAll = 0;
//            int totalDiscountInt;
//            int totalDiscountAmount = 0;
            int totalAmount = 0;

            for (Sale i : saleTableView.getItems()) {
                String[] subtotalArray = i.getSubtotal().split("\\.");
                StringBuilder sub = new StringBuilder();
                for (String s : subtotalArray) {
                    sub.append(s);
                }
                subtotalInt = Integer.parseInt(String.valueOf(sub));
                totalAmount += subtotalInt;

//                String[] discountAmountArray = i.getDiscountAmount().split("\\.");
//                StringBuilder disc = new StringBuilder();
//                for (String d : discountAmountArray) {
//                    disc.append(d);
//                }
//                totalDiscountInt = Integer.parseInt(String.valueOf(disc));
//                totalDiscountAmount += totalDiscountInt;
            }

//            totalAmount = totalAll - totalDiscountAmount;
//            String totalAllString = formatter.format(totalAll);
//            String totalDiscountString = formatter.format(totalDiscountAmount);
            String totalAmountString = formatter.format(totalAmount);

//            totalAllTextField.setText(totalAllString);
//            totalDiscountTextField.setText(totalDiscountString);
            totalAmountTextField.setText(totalAmountString);
            t.getTableView().getSelectionModel().clearSelection();
        });

        saleTableView.setRowFactory(saleTableView -> {
            final TableRow<Sale> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem removeMenuItem = new MenuItem("Hapus");
            removeMenuItem.setOnAction(actionEvent -> {
                saleTableView.getItems().remove(row.getItem());
//                int totalAll = 0;
//                int totalDiscountInt;
//                int totalDiscountAmount = 0;
                int totalAmount = 0;

                for (Sale i : saleTableView.getItems()) {
                    String[] subtotalArray = i.getSubtotal().split("\\.");
                    StringBuilder sub = new StringBuilder();
                    for (String s : subtotalArray) {
                        sub.append(s);
                    }
                    int subtotalInt = Integer.parseInt(String.valueOf(sub));
                    totalAmount += subtotalInt;

//                    String[] discountAmountArray = i.getDiscountAmount().split("\\.");
//                    StringBuilder disc = new StringBuilder();
//                    for (String d : discountAmountArray) {
//                        disc.append(d);
//                    }
//                    totalDiscountInt = Integer.parseInt(String.valueOf(disc));
//                    totalDiscountAmount += totalDiscountInt;
                }

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
//                totalAmount = totalAll - totalDiscountAmount;
//                String totalAllString = formatter.format(totalAll);
//                String totalDiscountString = formatter.format(totalDiscountAmount);
                String totalAmountString = formatter.format(totalAmount);

//                totalAllTextField.setText(totalAllString);
//                totalDiscountTextField.setText(totalDiscountString);
                totalAmountTextField.setText(totalAmountString);
                saleTableView.getSelectionModel().clearSelection();
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
        content = "Anda yakin ingin keluar?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }

    @FXML
    private void addProductButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Product product = productDao.fetchProduct(barcodeTextField.getText().trim());
        if (product == null) {
            content = "Produk dengan barcode tersebut tidak ditemukan!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            Sale sale = new Sale();
            sale.setBarcode(product.getBarcode());
            sale.setName(product.getName());
            sale.setSellingPrice(product.getSellingPrice());
            sale.setQuantity(1);
//            sale.setDiscount(productDao.getDiscount(barcodeTextField.getText()));

//            int discountAmountInt;
//            String discountAmountString;
            int subtotalInt;
            String subtotalString;

            String[] selling = sale.getSellingPrice().split("\\.");
            StringBuilder price = new StringBuilder();
            for (String s : selling) {
                price.append(s);
            }
            int sellingInt = Integer.parseInt(String.valueOf(price));
            int subtotalStart = sellingInt * sale.getQuantity();
//            if (sale.getDiscount() != 0) {
//                discountAmountInt = subtotalStart * sale.getDiscount() / 100;
//            } else {
//                discountAmountInt = 0;
//            }

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);

//            discountAmountString = formatter.format(discountAmountInt);
            subtotalString = formatter.format(subtotalStart);

//            sale.setDiscountAmount(discountAmountString);
            sale.setSubtotal(subtotalString);

            saleTableView.getItems().add(sale);
//            int totalAll = 0;
//            int totalDiscountInt;
//            int totalDiscountAmount = 0;
            int totalAmount = 0;

            for (Sale i : saleTableView.getItems()) {
                String[] subtotalArray = i.getSubtotal().split("\\.");
                StringBuilder sub = new StringBuilder();
                for (String s : subtotalArray) {
                    sub.append(s);
                }
                subtotalInt = Integer.parseInt(String.valueOf(sub));
                totalAmount += subtotalInt;

//                String[] discountAmountArray = i.getDiscountAmount().split("\\.");
//                StringBuilder disc = new StringBuilder();
//                for (String d : discountAmountArray) {
//                    disc.append(d);
//                }
//                totalDiscountInt = Integer.parseInt(String.valueOf(disc));
//                totalDiscountAmount += totalDiscountInt;
            }

//            totalAmount = totalAll - totalDiscountAmount;
//            String totalAllString = formatter.format(totalAll);
//            String totalDiscountString = formatter.format(totalDiscountAmount);
            String totalAmountString = formatter.format(totalAmount);

//            totalAllTextField.setText(totalAllString);
//            totalDiscountTextField.setText(totalDiscountString);
            totalAmountTextField.setText(totalAmountString);
            barcodeTextField.clear();
        }
    }

    @FXML
    private void resetProductButtonAction(ActionEvent actionEvent) {
        barcodeTextField.clear();
    }

    @FXML
    private void printSaleButtonAction(ActionEvent actionEvent) {
        saleData = saleTableView.getItems();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tunai");
        dialog.setContentText("Jumlah bayar");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(String.valueOf(Main.class.getResource("css/style.css")));
        pane.getStyleClass().add("myDialog");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("image/logo.png"))));
        Optional<String> result;
        TextInputControl control = dialog.getEditor();
        control.setPrefWidth(200);
        control.setStyle("-fx-font-size: 16px");
        Helper.addThousandSeparator(control);

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
//        transaction.setTotalAll(totalAllTextField.getText());
//        transaction.setTotalDiscount(totalDiscountTextField.getText());
        transaction.setTotalAmount(totalAmountTextField.getText());

        String[] totalArray = transaction.getTotalAmount().split("\\.");
        StringBuilder total = new StringBuilder();
        for (String t : totalArray) {
            total.append(t);
        }
        int totalAmountInt = Integer.parseInt(String.valueOf(total));
        int payAmountInt;

        do {
            result = dialog.showAndWait();
            if (result.isPresent()) {
                transaction.setPayAmount(dialog.getResult());

                String[] payArray = transaction.getPayAmount().split("\\.");
                StringBuilder pay = new StringBuilder();
                for (String p : payArray) {
                    pay.append(p);
                }
                payAmountInt = Integer.parseInt(String.valueOf(pay));

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                int changeInt = payAmountInt - totalAmountInt;
                String changeString = formatter.format(changeInt);

                transaction.setChange(changeString);
            } else {
                return;
            }
        } while (totalAmountInt > payAmountInt);

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
        content = "Anda yakin ingin reset?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            resetSale();
        }
    }

    private void resetSale() {
        barcodeTextField.clear();
        saleTableView.getItems().clear();
        totalAmountTextField.setText("");
    }

//    @FXML
//    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
//        Helper.changePage(stockMenuButton, "Kasir - Produk", "product-cashier-view.fxml");
//    }
//
//    @FXML
//    private void promotionMenuItemAction(ActionEvent actionEvent) throws IOException {
//        Helper.changePage(stockMenuButton, "Kasir - Promosi", "promotion-cashier-view.fxml");
//    }
}
