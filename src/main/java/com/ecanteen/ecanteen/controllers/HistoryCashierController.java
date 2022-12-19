package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.TransactionDaoImpl;
import com.ecanteen.ecanteen.entities.Transaction;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HistoryCashierController implements Initializable {
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
    private TableView<Transaction> historyTableView;
    @FXML
    private TableColumn<Transaction, String> idTableColumn;
    @FXML
    private TableColumn<Transaction, String> customerTableColumn;
    @FXML
    private TableColumn<Transaction, String> totalTableColumn;
    @FXML
    private TableColumn<Transaction, String> timeTableColumn;
    @FXML
    private TextField searchTextField;

    private ObservableList<Transaction> transactions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TransactionDaoImpl transactionDao = new TransactionDaoImpl();
        transactions = FXCollections.observableArrayList();

        try {
            transactions.addAll(transactionDao.fetchTransactionCashier());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        historyTableView.setPlaceholder(new Label("Tidak ada data."));
        historyTableView.setItems(transactions);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        customerTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomer().getName()));
        totalTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Helper.currencyToString(data.getValue().getTotal())));
        timeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));

        historyTableView.setRowFactory(historyTableView -> {
            final TableRow<Transaction> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem cancelMenuItem = new MenuItem("Batalkan Transaksi");
            cancelMenuItem.setOnAction(actionEvent -> {
                String content = "Anda yakin ingin membatalkan transaksi ini?" +
                        "\n\nID Transaksi\t: " + row.getItem().getId() +
                        "\nPembeli\t\t: " + row.getItem().getCustomer() +
                        "\nTotal\t\t: " + Helper.currencyToString(row.getItem().getTotal()) +
                        "\nWaktu\t\t: " + row.getItem().getTime();
                ButtonType result = Helper.alert(Alert.AlertType.CONFIRMATION, content);
                try {
                    if (result == ButtonType.OK && transactionDao.deleteData(row.getItem()) == 1) {
                        content = "Transaksi telah dibatalkan!" +
                                "\nID Transaksi : " + row.getItem().getId();
                        Helper.alert(Alert.AlertType.INFORMATION, content);
                        historyTableView.getItems().remove(row.getItem());
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                historyTableView.getSelectionModel().clearSelection();
            });
            contextMenu.getItems().add(cancelMenuItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu)
            );
            return row;
        });
    }

    @FXML
    private void containerPaneKeyReleased(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.TAB && keyEvent.isShortcutDown()) {
            Helper.changePage(recapMenuButton, "Kasir - Rekap", "recap-cashier-view.fxml");
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                historyTableView.setItems(transactions);
                return;
            }

            ObservableList<Transaction> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Transaction, ?>> columns = historyTableView.getColumns();

            for (Transaction value : transactions) {
                for (int j = 0; j < 4; j++) {
                    TableColumn<Transaction, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            historyTableView.setItems(tableItems);
        });
    }

    @FXML
    private void transactionMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(transactionMenuButton, "Kasir - Transaksi", "transaction-cashier-view.fxml");
    }

    @FXML
    private void recapMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Kasir - Rekap", "recap-cashier-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
