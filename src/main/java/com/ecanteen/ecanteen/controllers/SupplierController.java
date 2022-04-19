package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Supplier;
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
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {
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
    private TextField addressTextField;
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
    private Label warningLabel;
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
    private String content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        supplierDao = new SupplierDaoImpl();
        suppliers = FXCollections.observableArrayList();

        try {
            suppliers.addAll(supplierDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        profileButton.setText(Common.user.getName());
        Helper.toNumberField(phoneTextField);
        Helper.toNumberField(accountNumberTextField);
        Helper.addTextLimiter(idTextField, 16);
        Helper.addTextLimiter(nameTextField, 30);
        Helper.addTextLimiter(addressTextField, 15);
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
    private void addButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (idTextField.getText().trim().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                phoneTextField.getText().trim().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            if (!EmailValidator.getInstance().isValid(emailTextField.getText())) {
                warningLabel.setText("Email tidak valid");
            } else {
                if (supplierDao.getId(idTextField.getText()) == 1) {
                    content = "ID supplier tersebut sudah digunakan!";
                    Helper.alert(Alert.AlertType.ERROR, content);
                } else {
                    Supplier supplier = new Supplier();
                    supplier.setId(idTextField.getText().trim());
                    supplier.setName(nameTextField.getText().trim());
                    supplier.setAddress(addressTextField.getText().trim());
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
                            content = "Data berhasil ditambahkan!";
                            Helper.alert(Alert.AlertType.INFORMATION, content);
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty() ||
                addressTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                phoneTextField.getText().trim().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            if (!EmailValidator.getInstance().isValid(emailTextField.getText())) {
                warningLabel.setText("Email tidak valid");
            } else {
                selectedSupplier.setName(nameTextField.getText().trim());
                selectedSupplier.setAddress(addressTextField.getText().trim());
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

                content = "Anda yakin ingin mengubah?";
                if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
                    try {
                        if (supplierDao.updateData(selectedSupplier) == 1) {
                            suppliers.clear();
                            suppliers.addAll(supplierDao.fetchAll());
                            resetSupplier();
                            supplierTableView.requestFocus();
                            content = "Data berhasil diubah!";
                            Helper.alert(Alert.AlertType.INFORMATION, content);
                        }
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        if (selectedSupplier.getProductAmount() > 0 && selectedSupplier.getStatus().equals("Aktif")) {
            content = "Supplier ini memiliki produk dan berstatus aktif, tidak dapat dihapus!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            content = "Anda yakin ingin menghapus?";
            if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
                try {
                    if (supplierDao.deleteData(selectedSupplier) == 1) {
                        suppliers.clear();
                        suppliers.addAll(supplierDao.fetchAll());
                        resetSupplier();
                        supplierTableView.requestFocus();
                        content = "Data berhasil dihapus!";
                        Helper.alert(Alert.AlertType.INFORMATION, content);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
            addressTextField.setText(selectedSupplier.getAddress());
            genderComboBox.setValue(selectedSupplier.getGender());
            phoneTextField.setText(selectedSupplier.getPhone());
            emailTextField.setText(selectedSupplier.getEmail());
            bankAccountTextField.setText(selectedSupplier.getBankAccount());
            accountNumberTextField.setText(selectedSupplier.getAccountNumber());
            statusComboBox.setValue(selectedSupplier.getStatus());
            idTextField.setDisable(true);
            warningLabel.setText("");
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
                stage.initOwner(supplierTableView.getScene().getWindow());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            }
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                supplierTableView.setItems(suppliers);
                return;
            }

            ObservableList<Supplier> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Supplier, ?>> columns = supplierTableView.getColumns();

            for (Supplier value : suppliers) {
                for (int j = 0; j < 2; j++) {
                    TableColumn<Supplier, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            supplierTableView.setItems(tableItems);
        });
    }

    private void resetSupplier() {
        idTextField.clear();
        nameTextField.clear();
        addressTextField.clear();
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
    private void promotionMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Promosi", "promotion-view.fxml");
    }

    @FXML
    private void userButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - User", "user-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
