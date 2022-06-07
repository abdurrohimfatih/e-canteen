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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionCashierController implements Initializable {
    @FXML
    private Button transactionMenuButton;
    @FXML
    private Button historyMenuButton;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button refreshProductButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Product> productTableView;
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
    @FXML
    private TableColumn<Sale, String> subtotalSaleTableColumn;
    @FXML
    private TextField totalAmountTextField;
    @FXML
    private Button printSaleButton;
    @FXML
    private Button resetSaleButton;

    private ProductDaoImpl productDao;
    private TransactionDaoImpl transactionDao;
    private ObservableList<Sale> saleData = FXCollections.observableArrayList();
    private String content;
    private ObservableList<Product> products;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        transactionDao = new TransactionDaoImpl();
        saleData = saleTableView.getItems();
        products = FXCollections.observableArrayList();

        try {
            products.addAll(productDao.fetchProductsReturnStock());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        productTableView.setPlaceholder(new Label("Tidak ada data."));
        saleTableView.setPlaceholder(new Label("Tidak ada data."));
        productTableView.setItems(products);
        barcodeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        sellingPriceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        stockAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockAmount()).asObject());
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
        barcodeSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
        nameSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        sellingPriceSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSellingPrice()));
        quantitySaleTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        subtotalSaleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSubtotal()));

        Callback<TableColumn<Sale, Integer>, TableCell<Sale, Integer>> cellFactory = p -> new EditingCell();
        quantitySaleTableColumn.setCellFactory(cellFactory);
        quantitySaleTableColumn.setOnEditCommit(t -> {
            String barcode = t.getTableView().getItems().get(t.getTablePosition().getRow()).getBarcode();

            int stockAmount;
            try {
                stockAmount = productDao.getStockAmount(barcode);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            String sellingPrice = t.getTableView().getItems().get(t.getTablePosition().getRow()).getSellingPrice();
            int qty = t.getTableView().getItems().get(t.getTablePosition().getRow()).getQuantity();

            if (qty > stockAmount) {
                t.getRowValue().setQuantity(1);
                content = "Kuantitas melebihi stok produk!";
                Helper.alert(Alert.AlertType.ERROR, content);
            } else {
                t.getRowValue().setQuantity(t.getNewValue());
            }

            qty = t.getTableView().getItems().get(t.getTablePosition().getRow()).getQuantity();

            int subtotalInt;
            String subtotalString;
            String[] selling = sellingPrice.split("\\.");
            StringBuilder price = new StringBuilder();
            for (String s : selling) {
                price.append(s);
            }
            int sellingInt = Integer.parseInt(String.valueOf(price));
            int subtotalStart = sellingInt * qty;

            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            formatter.setDecimalFormatSymbols(symbols);

            subtotalString = formatter.format(subtotalStart);

            t.getRowValue().setSubtotal(subtotalString);
            saleTableView.refresh();
            int totalAmount = 0;

            for (Sale i : saleTableView.getItems()) {
                String[] subtotalArray = i.getSubtotal().split("\\.");
                StringBuilder sub = new StringBuilder();
                for (String s : subtotalArray) {
                    sub.append(s);
                }
                subtotalInt = Integer.parseInt(String.valueOf(sub));
                totalAmount += subtotalInt;
            }

            String totalAmountString = formatter.format(totalAmount);

            totalAmountTextField.setText(totalAmountString);
            t.getTableView().getSelectionModel().clearSelection();
            barcodeTextField.requestFocus();
        });

        saleTableView.setRowFactory(saleTableView -> {
            final TableRow<Sale> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem removeMenuItem = new MenuItem("Hapus");
            removeMenuItem.setOnAction(actionEvent -> {
                saleTableView.getItems().remove(row.getItem());
                int totalAmount = 0;

                for (Sale i : saleTableView.getItems()) {
                    String[] subtotalArray = i.getSubtotal().split("\\.");
                    StringBuilder sub = new StringBuilder();
                    for (String s : subtotalArray) {
                        sub.append(s);
                    }
                    int subtotalInt = Integer.parseInt(String.valueOf(sub));
                    totalAmount += subtotalInt;
                }

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                String totalAmountString = formatter.format(totalAmount);

                if (totalAmountString.equals("0")) {
                    totalAmountTextField.setText("");
                } else {
                    totalAmountTextField.setText(totalAmountString);
                }
                saleTableView.getSelectionModel().clearSelection();
                barcodeTextField.requestFocus();
            });
            contextMenu.getItems().add(removeMenuItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu)
            );
            return row;
        });
    }

    @FXML
    private void addProductButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Product product = productDao.fetchProduct(barcodeTextField.getText().trim());

        if (barcodeTextField.getText().trim().isEmpty()) {
            content = "Masukkan barcode terlebih dahulu!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        if (product == null) {
            content = "Produk dengan barcode tersebut tidak ditemukan!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        if (productDao.getStockAmount(barcodeTextField.getText().trim()) <= 0) {
            content = "Produk tersebut stoknya habis!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate now = Helper.formatter(LocalDate.now().format(dateTimeFormatter));
//        LocalDate nowPlus1 = now.plusDays(1);
//        String expiredDate = productDao.getExpiredDate(barcodeTextField.getText().trim());
//
//        if (now.isEqual(Helper.formatter(expiredDate)) ||
//                now.isAfter(Helper.formatter(expiredDate))) {
//            content = "Produk tersebut sudah kedaluwarsa!";
//            Helper.alert(Alert.AlertType.ERROR, content);
//        } else if (nowPlus1.isEqual(Helper.formatter(expiredDate))) {
//            content = "Produk tersebut kedaluwarsa besok!";
//            Helper.alert(Alert.AlertType.ERROR, content);
//        }

        Sale sale = new Sale();
        sale.setBarcode(product.getBarcode());
        sale.setName(product.getName());
        sale.setSellingPrice(product.getSellingPrice());
        sale.setQuantity(1);

        int subtotalInt;
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

        saleTableView.getItems().add(sale);
        int totalAmount = 0;

        for (Sale i : saleTableView.getItems()) {
            String[] subtotalArray = i.getSubtotal().split("\\.");
            StringBuilder sub = new StringBuilder();
            for (String s : subtotalArray) {
                sub.append(s);
            }
            subtotalInt = Integer.parseInt(String.valueOf(sub));
            totalAmount += subtotalInt;
        }

        String totalAmountString = formatter.format(totalAmount);

        totalAmountTextField.setText(totalAmountString);

        resetProductButtonAction(actionEvent);
    }

    @FXML
    private void resetProductButtonAction(ActionEvent actionEvent) {
        productTableView.getSelectionModel().clearSelection();
        barcodeTextField.clear();
        barcodeTextField.requestFocus();
    }

    @FXML
    private void printSaleButtonAction(ActionEvent actionEvent) {
        if (saleData.isEmpty()) {
            content = "Isi barang terlebih dahulu ke dalam list!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tunai");
        dialog.setContentText("Jumlah bayar Rp");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        DialogPane pane = dialog.getDialogPane();
        pane.setPrefWidth(400);
        pane.getStylesheets().add(String.valueOf(Main.class.getResource("css/style.css")));
        pane.getStyleClass().add("myDialog");
        pane.getContent().setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: center;");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("image/logo.png"))));
        Optional<String> result;
        TextInputControl control = dialog.getEditor();
        control.setStyle("-fx-font-size: 20px; -fx-pref-width: 200px;");
        Helper.addThousandSeparator(control);

        Transaction transaction = new Transaction();
        try {
            transaction.setId(String.valueOf(transactionDao.getNowSaleId()));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        transaction.setUsername(Common.user.getUsername());
        transaction.setDate(Helper.formattedDateNow());
        transaction.setTime(Helper.formattedTimeNow());

        String[] totalArray = totalAmountTextField.getText().split("\\.");
        StringBuilder total = new StringBuilder();
        for (String t : totalArray) {
            total.append(t);
        }

        int totalAmountInt = Integer.parseInt(String.valueOf(total));

        transaction.setTotalAmount(String.valueOf(totalAmountInt));
        Common.totalAmountString = totalAmountTextField.getText();

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

                Common.change = changeString;

                dialog.setHeaderText("Jumlah bayar kurang dari total!");
            } else {
                return;
            }
        } while (totalAmountInt > payAmountInt);

        try {
            if (transactionDao.addData(transaction) == 1) {
                for (Sale item : saleData) {
                    int oldStock = productDao.getStockAmount(item.getBarcode());
                    int newStock = oldStock - item.getQuantity();
                    productDao.updateStock(newStock, item.getBarcode());
                }

                new ReportGenerator().generateInvoice(transactionDao, this, saleData, transaction);

                content = "Kembalian Rp";
                Helper.alert(Alert.AlertType.INFORMATION, content);

                resetProductButtonAction(actionEvent);
                products.clear();
                products.addAll(productDao.fetchAll());
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        barcodeTextField.requestFocus();
    }

    @FXML
    private void resetSaleButtonAction(ActionEvent actionEvent) {
        content = "Anda yakin ingin reset?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            resetProductButtonAction(actionEvent);
            resetSale();
        }
    }

    public void resetSale() {
        barcodeTextField.clear();
        saleTableView.getItems().clear();
        totalAmountTextField.setText("");
        barcodeTextField.requestFocus();
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

    @FXML
    private void productTableViewClicked(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (mouseEvent.getClickCount() > 1) {
            if (productDao.getStockAmount(selectedProduct.getBarcode()) <= 0) {
                content = "Produk tersebut stoknya habis!";
                Helper.alert(Alert.AlertType.ERROR, content);
            } else {
                addProduct(selectedProduct);
            }
        }

        productTableView.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || mouseEvent.getClickCount() > 1) {
                try {
                    if (productDao.getStockAmount(selectedProduct.getBarcode()) <= 0) {
                        content = "Produk tersebut stoknya habis!";
                        Helper.alert(Alert.AlertType.ERROR, content);
                    } else {
                        addProduct(selectedProduct);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void addProduct(Product selectedProduct) {
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate now = Helper.formatter(LocalDate.now().format(dateTimeFormatter));
//        LocalDate nowPlus1 = now.plusDays(1);
//        String expiredDate = selectedProduct.getExpiredDate();
//
//        if (now.isEqual(Helper.formatter(expiredDate)) ||
//                now.isAfter(Helper.formatter(expiredDate))) {
//            content = "Produk tersebut sudah kedaluwarsa!";
//            Helper.alert(Alert.AlertType.ERROR, content);
//        } else if (nowPlus1.isEqual(Helper.formatter(expiredDate))) {
//            content = "Produk tersebut kedaluwarsa besok!";
//            Helper.alert(Alert.AlertType.ERROR, content);
//        }

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

        saleTableView.getItems().add(sale);

        int subtotalInt;
        int totalAmount = 0;

        for (Sale i : saleTableView.getItems()) {
            String[] subtotalArray = i.getSubtotal().split("\\.");
            StringBuilder sub = new StringBuilder();
            for (String s : subtotalArray) {
                sub.append(s);
            }
            subtotalInt = Integer.parseInt(String.valueOf(sub));
            totalAmount += subtotalInt;
        }

        String totalAmountString = formatter.format(totalAmount);
        totalAmountTextField.setText(totalAmountString);

        productTableView.getSelectionModel().clearSelection();
        barcodeTextField.requestFocus();
    }

    @FXML
    private void saleTableViewClicked(MouseEvent mouseEvent) {
        Sale row = saleTableView.getSelectionModel().getSelectedItem();
        saleTableView.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                Common.productName = row.getName();
                content = "Tidak jadi membeli ini?";
                if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
                    return;
                }

                saleTableView.getItems().remove(row);

                int totalAmount = 0;

                for (Sale i : saleTableView.getItems()) {
                    String[] subtotalArray = i.getSubtotal().split("\\.");
                    StringBuilder sub = new StringBuilder();
                    for (String s : subtotalArray) {
                        sub.append(s);
                    }
                    int subtotalInt = Integer.parseInt(String.valueOf(sub));
                    totalAmount += subtotalInt;
                }

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                formatter.setDecimalFormatSymbols(symbols);
                String totalAmountString = formatter.format(totalAmount);

                if (totalAmountString.equals("0")) {
                    totalAmountTextField.setText("");
                } else {
                    totalAmountTextField.setText(totalAmountString);
                }
                saleTableView.getSelectionModel().clearSelection();
                barcodeTextField.requestFocus();
            }
        });
    }

    @FXML
    private void historyMenuButtonAction(ActionEvent actionEvent) throws IOException {
        if (!saleData.isEmpty()) {
            content = "Data transaksi akan di-reset.\nAnda yakin ingin pindah halaman?";
            ButtonType result = Helper.alert(Alert.AlertType.CONFIRMATION, content);
            if (result == ButtonType.OK) {
                Helper.changePage(historyMenuButton, "Kasir - Riwayat", "history-cashier-view.fxml");
            }
        } else {
            Helper.changePage(historyMenuButton, "Kasir - Riwayat", "history-cashier-view.fxml");
        }
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }

    @FXML
    private void refreshProductButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        products.clear();
        products.addAll(productDao.fetchAll());
        barcodeTextField.requestFocus();
    }
}
