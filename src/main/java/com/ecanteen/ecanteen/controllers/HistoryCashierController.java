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
    private Button topUpMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label cashierNameLabel;
    @FXML
    private TableView<Income> incomeTableView;
    @FXML
    private TableColumn<Income, String> dateTableColumn;
    @FXML
    private TableColumn<Income, String> incomeTableColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        IncomeDaoImpl incomeDao = new IncomeDaoImpl();
        ObservableList<Income> incomes = FXCollections.observableArrayList();
        cashierNameLabel.setText(cashierNameLabel.getText() + Common.user.getName().toUpperCase());

        try {
            incomes.addAll(incomeDao.fetchIncomeCashier());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        incomeTableView.setPlaceholder(new Label("Tidak ada data."));
        incomeTableView.setItems(incomes);
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        incomeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIncome()));
    }

    @FXML
    private void containerPaneKeyReleased(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.TAB && keyEvent.isShortcutDown()) {
            Helper.changePage(transactionMenuButton, "Kasir - Transaksi", "transaction-cashier-view.fxml");
        }
    }

    @FXML
    private void transactionMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(transactionMenuButton, "Kasir - Transaksi", "transaction-cashier-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        String content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
