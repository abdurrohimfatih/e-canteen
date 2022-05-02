package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.PromotionDaoImpl;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleIntegerProperty;
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

public class PromotionCashierController implements Initializable {
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
    private MenuButton stockMenuButton;
    @FXML
    private MenuItem productMenuItem;
    @FXML
    private MenuItem promotionMenuItem;
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
    private TableView<Promotion> promotionTableView;
    @FXML
    private TableColumn<Promotion, String> idTableColumn;
    @FXML
    private TableColumn<Promotion, String> nameTableColumn;
    @FXML
    private TableColumn<Promotion, Integer> percentageTableColumn;
    @FXML
    private TableColumn<Promotion, String> dateAddedTableColumn;
    @FXML
    private TableColumn<Promotion, String> expiredDateTableColumn;
    @FXML
    private TableColumn<Promotion, String> statusTableColumn;

    private ObservableList<Promotion> promotions;
    private PromotionDaoImpl promotionDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        promotionDao = new PromotionDaoImpl();
        promotions = FXCollections.observableArrayList();

        try {
            promotions.addAll(promotionDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        profileButton.setText(Common.user.getName());
        promotionTableView.setPlaceholder(new Label("Tidak ada data."));
        promotionTableView.setItems(promotions);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        percentageTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPercentage()).asObject());
        dateAddedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateAdded()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    @FXML
    private void transactionCashierButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(transactionMenuButton, "Kasir - Transaksi", "transaction-view.fxml");
    }

    @FXML
    private void productCashierMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Kasir - Produk", "product-cashier-view.fxml");
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
                promotionTableView.setItems(promotions);
                return;
            }

            ObservableList<Promotion> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Promotion, ?>> columns = promotionTableView.getColumns();

            for (Promotion value : promotions) {
                for (int j = 1; j < 2; j++) {
                    TableColumn<Promotion, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            promotionTableView.setItems(tableItems);
        });
    }
}
