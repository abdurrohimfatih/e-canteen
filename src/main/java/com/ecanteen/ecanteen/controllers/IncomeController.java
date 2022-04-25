package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.IncomeDaoImpl;
import com.ecanteen.ecanteen.entities.Income;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class IncomeController implements Initializable {

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
    private TextField searchTextField;
    @FXML
    private Label cashierNameLabel;
    @FXML
    private TableView<Income> incomeTableView;
    @FXML
    private TableColumn<Income, String> dateTableColumn;
    @FXML
    private TableColumn<Income, String> incomeTableColumn;

    private ObservableList<Income> incomes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        IncomeDaoImpl incomeDao = new IncomeDaoImpl();
        incomes = FXCollections.observableArrayList();
        cashierNameLabel.setText(cashierNameLabel.getText() + Common.user.getName().toUpperCase());

        try {
            incomes.addAll(incomeDao.fetchIncomeCashier());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        profileButton.setText(Common.user.getName());
        incomeTableView.setItems(incomes);
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        incomeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIncome()));
    }

    @FXML
    private void transactionMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(transactionMenuButton, "Kasir - Transaksi", "transaction-view.fxml");
    }

    @FXML
    private void productMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(productMenuButton, "Kasir - Produk", "product-cashier-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                incomeTableView.setItems(incomes);
                return;
            }

            ObservableList<Income> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Income, ?>> columns = incomeTableView.getColumns();

            for (Income value : incomes) {
                for (int j = 0; j < 2; j++) {
                    TableColumn<Income, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            incomeTableView.setItems(tableItems);
        });
    }
}
