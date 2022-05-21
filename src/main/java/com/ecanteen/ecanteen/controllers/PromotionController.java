package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.PromotionDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PromotionController implements Initializable {
    @FXML
    private MenuButton reportMenuButton;
    @FXML
    private MenuItem incomeMenuItem;
    @FXML
    private MenuItem soldProductMenuItem;
    @FXML
    private MenuItem favoriteProductMenuItem;
    @FXML
    private MenuItem supplierMenuItem;
    @FXML
    private MenuItem benefitMenuItem;
    @FXML
    private MenuButton stockMenuButton;
    @FXML
    private MenuItem productMenuItem;
    @FXML
    private MenuItem categoryMenuItem;
    @FXML
    private MenuItem promotionMenuItem;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button customerMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private Button historyMenuButton;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button profileButton;
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
    private TableColumn<Promotion, Integer> productAmountTableColumn;
    @FXML
    private TableColumn<Promotion, String> dateAddedTableColumn;
    @FXML
    private TableColumn<Promotion, String> expiredDateTableColumn;
    @FXML
    private TableColumn<Promotion, String> statusTableColumn;

    private ObservableList<Promotion> promotions;
    private PromotionDaoImpl promotionDao;
    static Promotion selectedPromotion;
    private String content;

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
        Helper.toNumberField(percentageTextField);
        Helper.addTextLimiter(idTextField, 10);
        Helper.addTextLimiter(nameTextField, 100);
        Helper.addTextLimiter(percentageTextField, 3);
        Helper.formatDatePicker(expiredDateDatePicker);
        expiredDateDatePicker.getEditor().setDisable(true);
        expiredDateDatePicker.getEditor().setOpacity(1);
        promotionTableView.setPlaceholder(new Label("Tidak ada data."));
        promotionTableView.setItems(promotions);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        percentageTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPercentage()).asObject());
        productAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getProductAmount()).asObject());
        dateAddedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateAdded()));
        expiredDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiredDate()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (idTextField.getText().trim().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                percentageTextField.getText().trim().isEmpty() ||
                expiredDateDatePicker.getValue() == null) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);

            resetError();

            if (idTextField.getText().trim().isEmpty()) idTextField.setStyle("-fx-border-color: RED");
            if (nameTextField.getText().trim().isEmpty()) nameTextField.setStyle("-fx-border-color: RED");
            if (percentageTextField.getText().trim().isEmpty()) percentageTextField.setStyle("-fx-border-color: RED");
            if (expiredDateDatePicker.getValue() == null) expiredDateDatePicker.setStyle("-fx-border-color: RED");

            return;
        }

        resetError();

        if (promotionDao.getId(idTextField.getText()) == 1) {
            content = "ID promosi tersebut sudah digunakan!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        Promotion promotion = new Promotion();
        promotion.setId(idTextField.getText().trim());
        promotion.setName(nameTextField.getText().trim());
        promotion.setPercentage(Integer.parseInt(percentageTextField.getText().trim()));
        promotion.setDateAdded(Helper.formattedDateNow());
        promotion.setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        try {
            if (promotionDao.addData(promotion) == 1) {
                promotions.clear();
                promotions.addAll(promotionDao.fetchAll());
                resetPromotion();
                idTextField.requestFocus();
                content = "Data berhasil ditambahkan!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty() ||
                percentageTextField.getText().trim().isEmpty() ||
                expiredDateDatePicker.getValue() == null) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);

            resetError();

            if (nameTextField.getText().trim().isEmpty()) nameTextField.setStyle("-fx-border-color: RED");
            if (percentageTextField.getText().trim().isEmpty()) percentageTextField.setStyle("-fx-border-color: RED");
            if (expiredDateDatePicker.getValue() == null) expiredDateDatePicker.setStyle("-fx-border-color: RED");

            return;
        }

        resetError();

        selectedPromotion.setName(nameTextField.getText().trim());
        selectedPromotion.setPercentage(Integer.parseInt(percentageTextField.getText().trim()));
        selectedPromotion.setExpiredDate(expiredDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        content = "Anda yakin ingin mengubah?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (promotionDao.updateData(selectedPromotion) == 1) {
                promotions.clear();
                promotions.addAll(promotionDao.fetchAll());
                resetPromotion();
                promotionTableView.requestFocus();
                content = "Data berhasil diubah!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        content = "Anda yakin ingin menghapus?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (promotionDao.deleteData(selectedPromotion) == 1) {
                promotions.clear();
                promotions.addAll(promotionDao.fetchAll());
                resetPromotion();
                promotionTableView.requestFocus();
                content = "Data berhasil dihapus!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetPromotion();
    }

    @FXML
    private void promotionTableViewClicked(MouseEvent mouseEvent) {
        selectedPromotion = promotionTableView.getSelectionModel().getSelectedItem();
        if (selectedPromotion == null) {
            return;
        }

        idTextField.setText(selectedPromotion.getId());
        nameTextField.setText(selectedPromotion.getName());
        percentageTextField.setText(String.valueOf(selectedPromotion.getPercentage()));
        expiredDateDatePicker.setValue(Helper.formatter(selectedPromotion.getExpiredDate()));
        idTextField.setDisable(true);
        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        resetButton.setDisable(false);

        if (mouseEvent.getClickCount() > 1) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-promotion-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setTitle("Detail Promosi");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initOwner(promotionTableView.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
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

    private void resetPromotion() {
        idTextField.clear();
        nameTextField.clear();
        percentageTextField.clear();
        expiredDateDatePicker.setValue(null);
        selectedPromotion = null;
        promotionTableView.getSelectionModel().clearSelection();
        resetError();
        idTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        idTextField.requestFocus();
    }

    private void resetError() {
        idTextField.setStyle("-fx-border-color: #424242");
        nameTextField.setStyle("-fx-border-color: #424242");
        percentageTextField.setStyle("-fx-border-color: #424242");
        expiredDateDatePicker.setStyle("-fx-border-color: #424242");
    }

    @FXML
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void userButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
    }

    @FXML
    private void supplierButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
