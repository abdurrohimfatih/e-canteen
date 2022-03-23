package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Supplier;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SupplierController implements Initializable {
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
    private TextArea addressTextArea;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField bankAccountTextField;
    @FXML
    private TextField accountNumberTextField;
    @FXML
    private ComboBox<String> statusComboBox;
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
    private TableView<Supplier> supplierTableView;
    @FXML
    private TableColumn<Supplier, String> idTableColumn;
    @FXML
    private TableColumn<Supplier, String> nameTableColumn;
    @FXML
    private TableColumn<Supplier, String> addressTableColumn;
    @FXML
    private TableColumn<Supplier, String> phoneTableColumn;
    @FXML
    private TableColumn<Supplier, Integer> productAmountTableColumn;
    @FXML
    private TableColumn<Supplier, String> statusTableColumn;

    private ObservableList<Supplier> suppliers;
    private SupplierDaoImpl supplierDao;
    static Supplier selectedSupplier;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        supplierDao = new SupplierDaoImpl();
        suppliers = FXCollections.observableArrayList();

        try {
            suppliers.addAll(supplierDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.addTextLimiter(idTextField, 16);
        Helper.addTextLimiter(nameTextField, 30);
        Helper.addTextLimiterTextArea(addressTextArea, 15);
        Helper.addTextLimiter(phoneTextField, 14);
        Helper.addTextLimiter(bankAccountTextField, 30);
        Helper.addTextLimiter(accountNumberTextField, 25);
        genderComboBox.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
        statusComboBox.setItems(FXCollections.observableArrayList("Aktif", "Tidak Aktif"));
        supplierTableView.setItems(suppliers);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        addressTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        phoneTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        productAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getProductAmount()).asObject());
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (idTextField.getText().trim().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextArea.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                phoneTextField.getText().trim().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.showAndWait();
        } else {
            Supplier supplier = new Supplier();
            supplier.setId(idTextField.getText().trim());
            supplier.setName(nameTextField.getText().trim());
            supplier.setAddress(addressTextArea.getText().trim());
            supplier.setGender(genderComboBox.getValue());
            supplier.setPhone(phoneTextField.getText().trim());
            if (emailTextField.getText().trim().isEmpty()) {
                supplier.setEmail("-");
            } else {
                supplier.setEmail(emailTextField.getText().trim());
            }

            if (bankAccountTextField.getText().trim().isEmpty()) {
                supplier.setBankAccount("-");
            } else {
                supplier.setBankAccount(bankAccountTextField.getText().trim());
            }

            if (accountNumberTextField.getText().trim().isEmpty()) {
                supplier.setAccountNumber("-");
            } else {
                supplier.setAccountNumber(accountNumberTextField.getText().trim());
            }

            if (statusComboBox.getValue().equals("Aktif")) {
                supplier.setStatus("1");
            } else {
                supplier.setStatus("0");
            }

            try {
                if (supplierDao.addData(supplier) == 1) {
                    suppliers.clear();
                    suppliers.addAll(supplierDao.fetchAll());
                    resetSupplier();
                    idTextField.requestFocus();
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
                addressTextArea.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                phoneTextField.getText().trim().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.showAndWait();
        } else {
            selectedSupplier.setName(nameTextField.getText().trim());
            selectedSupplier.setAddress(addressTextArea.getText().trim());
            selectedSupplier.setGender(genderComboBox.getValue());
            selectedSupplier.setPhone(phoneTextField.getText().trim());
            if (emailTextField.getText().trim().isEmpty()) {
                selectedSupplier.setEmail("-");
            } else {
                selectedSupplier.setEmail(emailTextField.getText().trim());
            }

            if (bankAccountTextField.getText().trim().isEmpty()) {
                selectedSupplier.setBankAccount("-");
            } else {
                selectedSupplier.setBankAccount(bankAccountTextField.getText().trim());
            }

            if (accountNumberTextField.getText().trim().isEmpty()) {
                selectedSupplier.setAccountNumber("-");
            } else {
                selectedSupplier.setAccountNumber(accountNumberTextField.getText().trim());
            }

            if (statusComboBox.getValue().equals("Aktif")) {
                selectedSupplier.setStatus("1");
            } else {
                selectedSupplier.setStatus("0");
            }

            try {
                if (supplierDao.updateData(selectedSupplier) == 1) {
                    suppliers.clear();
                    suppliers.addAll(supplierDao.fetchAll());
                    resetSupplier();
                    supplierTableView.requestFocus();
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
                if (supplierDao.deleteData(selectedSupplier) == 1) {
                    suppliers.clear();
                    suppliers.addAll(supplierDao.fetchAll());
                    resetSupplier();
                    supplierTableView.requestFocus();
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
        resetSupplier();
    }

    @FXML
    private void supplierTableViewClicked(MouseEvent mouseEvent) {
        selectedSupplier = supplierTableView.getSelectionModel().getSelectedItem();
        if (selectedSupplier != null) {
            idTextField.setText(selectedSupplier.getId());
            nameTextField.setText(selectedSupplier.getName());
            addressTextArea.setText(selectedSupplier.getAddress());
            genderComboBox.setValue(selectedSupplier.getGender());
            phoneTextField.setText(selectedSupplier.getPhone());
            emailTextField.setText(selectedSupplier.getEmail());
            bankAccountTextField.setText(selectedSupplier.getBankAccount());
            accountNumberTextField.setText(selectedSupplier.getAccountNumber());
            statusComboBox.setValue(selectedSupplier.getStatus());
            idTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);

            if (mouseEvent.getClickCount() > 1) {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-supplier-view.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setTitle("Detail Supplier");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

                Stage supplierStage = (Stage) supplierMenuButton.getScene().getWindow();
                supplierStage.setOnCloseRequest(event -> {
                    stage.close();
                });
            }
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        FilteredList<Supplier> filteredList = new FilteredList<>(suppliers, b -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(supplier -> {
            if (newValue.isEmpty()) {
                return true;
            }

            String searchKeyword = newValue.toLowerCase().trim();

            if (supplier.getId().toLowerCase().contains(searchKeyword)) {
                return true;
            } else return supplier.getName().toLowerCase().contains(searchKeyword);
        }));

        SortedList<Supplier> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(supplierTableView.comparatorProperty());
        supplierTableView.setItems(sortedList);
    }

    private void resetSupplier() {
        idTextField.clear();
        nameTextField.clear();
        addressTextArea.clear();
        genderComboBox.setValue(null);
        phoneTextField.clear();
        emailTextField.clear();
        bankAccountTextField.clear();
        accountNumberTextField.clear();
        statusComboBox.setValue(null);
        selectedSupplier = null;
        supplierTableView.getSelectionModel().clearSelection();
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
    private void promotionMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(promotionMenuButton, "Admin - Promosi", "promotion-view.fxml");
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
