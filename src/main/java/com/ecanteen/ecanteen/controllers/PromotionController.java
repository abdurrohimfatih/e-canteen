package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.PromotionDaoImpl;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PromotionController implements Initializable {
    @FXML
    private Button userMenuButton;
    @FXML
    private Button productMenuButton;
    @FXML
    private Button categoryMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private Button promotionMenuButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField percentageTextField;
    @FXML
    private DatePicker expiredDateDatePicker;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label infoLabel;
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

    private ObservableList<Promotion> promotions;
    private PromotionDaoImpl promotionDao;
    private Promotion selectedPromotion;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        promotionDao = new PromotionDaoImpl();
        promotions = FXCollections.observableArrayList();

        try {
            promotions.addAll(promotionDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.addTextLimiter(idTextField, 10);
        Helper.addTextLimiter(percentageTextField, 3);
        promotionTableView.setItems(promotions);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        percentageTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPercentage()).asObject());
        dateAddedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateAdded()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (idTextField.getText().trim().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                percentageTextField.getText().trim().isEmpty() ||
                expiredDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            Promotion promotion = new Promotion();
            promotion.setId(idTextField.getText().trim());
            promotion.setName(nameTextField.getText().trim());
            promotion.setPercentage(Integer.parseInt(percentageTextField.getText().trim()));
            promotion.setDateAdded(String.valueOf(LocalDate.now()));
            promotion.setExpiredDate(String.valueOf(expiredDateDatePicker.getValue()));

            try {
                if (promotionDao.addData(promotion) == 1) {
                    promotions.clear();
                    promotions.addAll(promotionDao.fetchAll());
                    resetPromotion();
                    infoLabel.setText("Data berhasil ditambahkan!");
                    infoLabel.setStyle("-fx-text-fill: green");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty() ||
                percentageTextField.getText().trim().isEmpty() ||
                expiredDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.setHeaderText("Error");
            alert.showAndWait();
        } else {
            selectedPromotion.setName(nameTextField.getText().trim());
            selectedPromotion.setPercentage(Integer.parseInt(percentageTextField.getText().trim()));
            selectedPromotion.setExpiredDate(String.valueOf(expiredDateDatePicker.getValue()));

            try {
                if (promotionDao.updateData(selectedPromotion) == 1) {
                    promotions.clear();
                    promotions.addAll(promotionDao.fetchAll());
                    resetPromotion();
                    infoLabel.setText("Data berhasil diubah!");
                    infoLabel.setStyle("-fx-text-fill: green");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Konfirmasi");
        alert.setContentText("Anda yakin ingin menghapus?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            try {
                if (promotionDao.deleteData(selectedPromotion) == 1) {
                    promotions.clear();
                    promotions.addAll(promotionDao.fetchAll());
                    resetPromotion();
                    promotionTableView.requestFocus();
                    infoLabel.setText("Data berhasil dihapus!");
                    infoLabel.setStyle("-fx-text-fill: green");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetPromotion();
    }

    @FXML
    private void promotionTableViewClicked(MouseEvent mouseEvent) {
        selectedPromotion = promotionTableView.getSelectionModel().getSelectedItem();
        if (selectedPromotion != null) {
            idTextField.setText(selectedPromotion.getId());
            nameTextField.setText(selectedPromotion.getName());
            percentageTextField.setText(String.valueOf(selectedPromotion.getPercentage()));
            expiredDateDatePicker.setValue(LocalDate.parse(selectedPromotion.getExpiredDate()));
            idTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        FilteredList<Promotion> filteredList = new FilteredList<>(promotions, b -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(promotion -> {
            if (newValue.isEmpty()) {
                return true;
            }

            String searchKeyword = newValue.toLowerCase().trim();

            return promotion.getName().toLowerCase().contains(searchKeyword);
        }));

        SortedList<Promotion> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(promotionTableView.comparatorProperty());
        promotionTableView.setItems(sortedList);
    }

    private void resetPromotion() {
        idTextField.clear();
        nameTextField.clear();
        percentageTextField.clear();
        expiredDateDatePicker.setValue(null);
        selectedPromotion = null;
        promotionTableView.getSelectionModel().clearSelection();
        idTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        idTextField.requestFocus();
        infoLabel.setText("");
    }

    @FXML
    private void userMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
    }

    @FXML
    private void productMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(productMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void categoryMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(categoryMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void supplierMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
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
}
