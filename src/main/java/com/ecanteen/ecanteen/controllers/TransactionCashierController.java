package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CustomerDaoImpl;
import com.ecanteen.ecanteen.dao.ProductDaoImpl;
import com.ecanteen.ecanteen.dao.StockDaoImpl;
import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.*;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class TransactionCashierController implements Initializable {
    @FXML
    private BorderPane containerPane;
    @FXML
    private Button transactionMenuButton;
    @FXML
    private Button historyMenuButton;
    @FXML
    private Button recapMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField barcodeTextField;
    @FXML
    private Button searchProductButton;
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
    private TextField buyerTextField;
    @FXML
    private Button xButton;
    @FXML
    private TextField totalTextField;
    @FXML
    private Button printSaleButton;
    @FXML
    private Button resetSaleButton;

    private ProductDaoImpl productDao;
    private TransactionDaoImpl transactionDao;
    private ObservableList<Sale> saleData = FXCollections.observableArrayList();
    private String content;
    private StockDaoImpl stockDao;
    private CustomerDaoImpl customerDao;
    private Sale selectedItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDao = new ProductDaoImpl();
        transactionDao = new TransactionDaoImpl();
        stockDao = new StockDaoImpl();
        customerDao = new CustomerDaoImpl();
        saleData = saleTableView.getItems();

        Helper.toNumberField(barcodeTextField);
        Helper.addTextLimiter(barcodeTextField, 20);
        saleTableView.setPlaceholder(new Label("Tidak ada data."));
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

            String sellingPriceString = t.getTableView().getItems().get(t.getTablePosition().getRow()).getSellingPrice();
            int qty = t.getTableView().getItems().get(t.getTablePosition().getRow()).getQuantity();

            if (qty > stockAmount) {
                t.getRowValue().setQuantity(1);
                content = "Kuantitas melebihi stok produk!";
                Helper.alert(Alert.AlertType.ERROR, content);
            } else {
                t.getRowValue().setQuantity(t.getNewValue());
            }

            qty = t.getTableView().getItems().get(t.getTablePosition().getRow()).getQuantity();

            int sellingPriceInt = Helper.currencyToInt(sellingPriceString);
            int subtotalInt = sellingPriceInt * qty;
            String subtotalString = Helper.currencyToString(subtotalInt);

            t.getRowValue().setSubtotal(subtotalString);
            saleTableView.refresh();
            int totalInt = 0;

            for (Sale i : saleTableView.getItems()) {
                subtotalInt = Helper.currencyToInt(i.getSubtotal());
                totalInt += subtotalInt;
            }

            String totalString = Helper.currencyToString(totalInt);
            totalTextField.setText(totalString);
            t.getTableView().getSelectionModel().clearSelection();
            resetBarcodeTextField();
        });

        saleTableView.setRowFactory(saleTableView -> {
            final TableRow<Sale> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem removeMenuItem = new MenuItem("Hapus");
            removeMenuItem.setOnAction(actionEvent -> {
                saleTableView.getItems().remove(row.getItem());
                int totalInt = 0;

                for (Sale i : saleTableView.getItems()) {
                    int subtotalInt = Helper.currencyToInt(i.getSubtotal());
                    totalInt += subtotalInt;
                }

                String totalString = Helper.currencyToString(totalInt);
                if (totalString.equals("0")) {
                    totalTextField.clear();
                } else {
                    totalTextField.setText(totalString);
                }
                saleTableView.getSelectionModel().clearSelection();
                resetBarcodeTextField();
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
            resetBarcodeTextField();
            return;
        }

        if (productDao.getStockAmount(barcodeTextField.getText().trim()) <= 0) {
            content = "Produk tersebut stoknya habis!";
            Helper.alert(Alert.AlertType.ERROR, content);
            resetBarcodeTextField();
            return;
        }

        Sale sale = new Sale();
        sale.setBarcode(product.getBarcode());
        sale.setName(product.getName());
        sale.setPurchasePrice(product.getPurchasePrice());
        sale.setSellingPrice(product.getSellingPrice());
        sale.setQuantity(1);

        int sellingPriceInt = Helper.currencyToInt(sale.getSellingPrice());
        int subtotalInt = sellingPriceInt * sale.getQuantity();
        String subtotalString = Helper.currencyToString(subtotalInt);

        sale.setSubtotal(subtotalString);

        saleTableView.getItems().add(sale);
        int totalInt = 0;

        for (Sale i : saleTableView.getItems()) {
            subtotalInt = Helper.currencyToInt(i.getSubtotal());
            totalInt += subtotalInt;
        }

        String totalString = Helper.currencyToString(totalInt);
        totalTextField.setText(totalString);
        resetBarcodeTextField();
    }

    @FXML
    private void printSaleButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        resetBarcodeTextField();
        if (saleData.isEmpty()) {
            content = "Isi barang terlebih dahulu ke dalam list!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        Transaction transaction = new Transaction();
        try {
            transaction.setId(String.valueOf(transactionDao.getNowSaleId()));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        transaction.setUsername(Common.user.getUsername());
        transaction.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        transaction.setTime(Helper.formattedTimeNow());
        int totalInt = Helper.currencyToInt(totalTextField.getText());
        int balanceInt;
        transaction.setTotal(totalInt);
        Common.total = totalTextField.getText();

        if (Common.buyer != null) {
            transaction.setCustomer(Common.buyer);
            balanceInt = Helper.currencyToInt(Common.buyer.getBalance());
            int newBalance = balanceInt - totalInt;

            if (balanceInt < totalInt) {
                content = "Saldo\t: Rp " + Common.buyer.getBalance() +
                        "\nSaldo tidak mencukupi!";
                Helper.alert(Alert.AlertType.ERROR, content);
                return;
            }

            content = "Pembeli\t: " + Common.buyer.getName() + " - " + Common.buyer.getRole() +
                    "\nSaldo\t: Rp " + Common.buyer.getBalance() +
                    "\nLanjutkan transaksi?";
            if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
                return;
            }

            if (customerDao.updateBalance(newBalance, Common.buyer) == 1) {
                content = "Sisa saldo (Rp)";
                Common.pay = Common.buyer.getBalance();
                Common.change = Helper.currencyToString(newBalance);
                Common.payOrOld = "Saldo";
                Common.changeOrNew = "Sisa saldo";
            }
        } else {
            transaction.setCustomer(new Customer(0));

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
            pane.setOnKeyReleased(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    Stage stage = (Stage) pane.getScene().getWindow();
                    stage.close();
                }
            });
            ((Button) pane.lookupButton(ButtonType.OK)).setText("OK");
            ((Button) pane.lookupButton(ButtonType.CANCEL)).setText("Batal");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("image/logo.png"))));
            Optional<String> result;
            TextInputControl control = dialog.getEditor();
            control.setStyle("-fx-font-size: 20px; -fx-pref-width: 200px;");
            Helper.addThousandSeparator(control);
            int payInt;

            do {
                result = dialog.showAndWait();
                if (result.isPresent()) {
                    content = "Kembalian (Rp)";
                    Common.pay = dialog.getResult();
                    payInt = Helper.currencyToInt(Common.pay);
                    int changeInt = payInt - totalInt;
                    Common.change = Helper.currencyToString(changeInt);
                    Common.payOrOld = "Tunai";
                    Common.changeOrNew = "Kembalian";
                    dialog.setHeaderText("Jumlah bayar kurang dari total!");
                } else {
                    return;
                }
            } while (payInt < totalInt);
        }

        try {
            if (transactionDao.addData(transaction) == 1) {
                for (Sale item : saleData) {
                    int oldStock = productDao.getStockAmount(item.getBarcode());
                    int newStock = oldStock - item.getQuantity();
                    productDao.updateStock(newStock, item.getBarcode());

                    Stock stock = new Stock();
                    try {
                        stock.setId(stockDao.getNowId());
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    stock.setBarcode(item.getBarcode());
                    stock.setPreviousStock(oldStock);
                    stock.setQty(item.getQuantity());
                    stock.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    stock.setType("sale");

                    stockDao.addData(stock);
                }

                transaction.setDate(LocalDate.parse(transaction.getDate()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                new ReportGenerator().generateInvoice(transactionDao, this, saleData, transaction);

                xButton.setDisable(true);
                xButton.setVisible(false);
                Helper.alert(Alert.AlertType.INFORMATION, content);
                resetBarcodeTextField();
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
        resetBarcodeTextField();
    }

    @FXML
    private void saleTableViewKeyReleased(KeyEvent keyEvent) {
        selectedItem = saleTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            saleTableView.getSelectionModel().clearSelection();
            resetBarcodeTextField();
        }

        saleTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, t1) -> selectedItem = saleTableView.getSelectionModel().getSelectedItem());

        if (keyEvent.getCode() == KeyCode.PLUS || (keyEvent.getCode() == KeyCode.EQUALS && keyEvent.isShiftDown()) || keyEvent.getCode() == KeyCode.ADD) {
            TablePosition<Sale, ?> position = new TablePosition<>(saleTableView, saleTableView.getSelectionModel().getSelectedIndex(), quantitySaleTableColumn);
            saleTableView.getFocusModel().focus(position);
            saleTableView.edit(saleTableView.getSelectionModel().getSelectedIndex(), quantitySaleTableColumn);
            saleTableView.getFocusModel().focus(position);
        }

        if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.MINUS || keyEvent.getCode() == KeyCode.SUBTRACT) {
            Common.productName = selectedItem.getName();
            content = "Tidak jadi membeli ini?";
            if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
                return;
            }

            saleTableView.getItems().remove(selectedItem);
            int totalInt = 0;

            for (Sale i : saleTableView.getItems()) {
                int subtotalInt = Helper.currencyToInt(i.getSubtotal());
                totalInt += subtotalInt;
            }

            String totalString = Helper.currencyToString(totalInt);
            if (totalString.equals("0")) {
                totalTextField.clear();
            } else {
                totalTextField.setText(totalString);
            }
            saleTableView.getSelectionModel().clearSelection();
            resetBarcodeTextField();
        }
    }

    @FXML
    private void containerPaneKeyReleased(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.F12) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-product-cashier-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Kasir - Daftar Produk");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initOwner(containerPane.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            stage.setOnHiding(windowEvent -> {
                if (Common.sale != null) {
                    saleTableView.getItems().add(Common.sale);
                    Common.sale = null;
                    int totalInt = 0;

                    for (Sale i : saleTableView.getItems()) {
                        int subtotalInt = Helper.currencyToInt(i.getSubtotal());
                        totalInt += subtotalInt;
                    }

                    String totalString = Helper.currencyToString(totalInt);
                    totalTextField.setText(totalString);
                }
                resetBarcodeTextField();
            });
        }

        if (keyEvent.getCode() == KeyCode.F10) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-customer-cashier-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Kasir - Daftar Pelanggan");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initOwner(containerPane.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            stage.setOnHiding(windowEvent -> {
                if (Common.buyer != null) {
                    buyerTextField.setText(Common.buyer.getName());
                    xButton.setVisible(true);
                    xButton.setDisable(false);
                }
                resetBarcodeTextField();
            });
        }

        if (keyEvent.getCode() == KeyCode.INSERT) {
            saleTableView.getSelectionModel().clearSelection();
            resetBarcodeTextField();
        }

        if (!saleTableView.isFocused()) {
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                saleTableView.requestFocus();
                saleTableView.getSelectionModel().select(0);
                saleTableView.getFocusModel().focus(0);
            }
        }

        if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.isShortcutDown()) {
            printSaleButton.fire();
        }

        if (keyEvent.getCode() == KeyCode.F5) {
            resetSaleButton.fire();
        }

        if (keyEvent.getCode() != KeyCode.TAB || !keyEvent.isShortcutDown()) {
            return;
        }

        if (!saleData.isEmpty()) {
            content = "Data transaksi akan di-reset.\nAnda yakin ingin pindah halaman?";
            ButtonType result = Helper.alert(Alert.AlertType.CONFIRMATION, content);
            if (result == ButtonType.OK) {
                Helper.changePage(historyMenuButton, "Kasir - Riwayat", "history-cashier-view.fxml");
            }

            resetBarcodeTextField();
        } else {
            Helper.changePage(historyMenuButton, "Kasir - Riwayat", "history-cashier-view.fxml");
        }
    }

    @FXML
    private void searchProductButtonAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-product-cashier-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Kasir - Daftar Produk");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.initOwner(containerPane.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        stage.setOnHiding(windowEvent -> {
            if (Common.sale != null) {
                saleTableView.getItems().add(Common.sale);
                Common.sale = null;
                int totalInt = 0;

                for (Sale i : saleTableView.getItems()) {
                    int subtotalInt = Helper.currencyToInt(i.getSubtotal());
                    totalInt += subtotalInt;
                }

                String totalString = Helper.currencyToString(totalInt);
                totalTextField.setText(totalString);
            }
            resetBarcodeTextField();
        });
    }

    @FXML
    private void buyerTextFieldClicked(MouseEvent mouseEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-customer-cashier-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Kasir - Daftar Pelanggan");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.initOwner(containerPane.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        stage.setOnHiding(windowEvent -> {
            if (Common.buyer != null) {
                buyerTextField.setText(Common.buyer.getName());
                xButton.setVisible(true);
                xButton.setDisable(false);
            }
            resetBarcodeTextField();
        });
    }

    @FXML
    private void xButtonAction(ActionEvent actionEvent) {
        resetBuyer();
    }

    public void resetBarcodeTextField() {
        barcodeTextField.clear();
        barcodeTextField.requestFocus();
    }

    public void resetBuyer() {
        if (Common.buyer != null) {
            Common.buyer = null;
            buyerTextField.setText("Umum");
            xButton.setVisible(false);
            xButton.setDisable(true);
        }
        resetBarcodeTextField();
    }

    public void resetSale() {
        resetBuyer();
        saleTableView.getItems().clear();
        totalTextField.clear();
        resetBarcodeTextField();
    }

    @FXML
    private void historyMenuButtonAction(ActionEvent actionEvent) throws IOException {
        if (!saleData.isEmpty()) {
            content = "Data transaksi akan di-reset.\nAnda yakin ingin pindah halaman?";
            ButtonType result = Helper.alert(Alert.AlertType.CONFIRMATION, content);
            if (result == ButtonType.OK) {
                resetSale();
                Helper.changePage(historyMenuButton, "Kasir - Riwayat", "history-cashier-view.fxml");
            }
        } else {
            resetSale();
            Helper.changePage(historyMenuButton, "Kasir - Riwayat", "history-cashier-view.fxml");
        }

        resetBarcodeTextField();
    }

    @FXML
    private void recapMenuButtonAction(ActionEvent actionEvent) throws IOException {
        if (!saleData.isEmpty()) {
            content = "Data transaksi akan di-reset.\nAnda yakin ingin pindah halaman?";
            ButtonType result = Helper.alert(Alert.AlertType.CONFIRMATION, content);
            if (result == ButtonType.OK) {
                resetSale();
                Helper.changePage(recapMenuButton, "Kasir - Rekap", "recap-cashier-view.fxml");
            }
        } else {
            resetSale();
            Helper.changePage(recapMenuButton, "Kasir - Rekap", "recap-cashier-view.fxml");
        }

        resetBarcodeTextField();
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            resetSale();
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }

        resetBarcodeTextField();
    }
}
