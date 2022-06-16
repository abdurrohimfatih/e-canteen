package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.UserDaoImpl;
import com.ecanteen.ecanteen.entities.User;
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
import javafx.scene.input.MouseEvent;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private MenuButton masterMenuButton;
    @FXML
    private MenuItem productMenuItem;
    @FXML
    private MenuItem categoryMenuItem;
    @FXML
    private MenuButton stockMenuButton;
    @FXML
    private MenuItem addStockMenuItem;
    @FXML
    private MenuItem returnStockMenuItem;
    @FXML
    private Button userMenuButton;
    @FXML
    private Button customerMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private MenuButton reportMenuButton;
    @FXML
    private MenuItem stockReportMenuItem;
    @FXML
    private MenuItem incomeReportMenuItem;
    @FXML
    private MenuItem supplierReportMenuItem;
    @FXML
    private MenuButton recapMenuButton;
    @FXML
    private MenuItem incomeRecapMenuItem;
    @FXML
    private MenuItem stockRecapMenuItem;
    @FXML
    private MenuItem supplierRecapMenuItem;
    @FXML
    private Button topUpMenuButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordTextField;
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
    private ComboBox<String> levelComboBox;
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
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, String> usernameTableColumn;
    @FXML
    private TableColumn<User, String> nameTableColumn;
    @FXML
    private TableColumn<User, String> addressTableColumn;
    @FXML
    private TableColumn<User, String> phoneTableColumn;
    @FXML
    private TableColumn<User, String> emailTableColumn;
    @FXML
    private TableColumn<User, String> levelTableColumn;
    @FXML
    private TableColumn<User, String> statusTableColumn;

    private ObservableList<User> users;
    private UserDaoImpl userDao;
    private User selectedUser;
    private String content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userDao = new UserDaoImpl();
        users = FXCollections.observableArrayList();

        try {
            users.addAll(userDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.toNumberField(phoneTextField);
        Helper.addTextLimiter(usernameTextField, 20);
        Helper.addTextLimiter(nameTextField, 30);
        Helper.addTextLimiter(addressTextField, 15);
        Helper.addTextLimiter(phoneTextField, 13);
        Helper.addTextLimiter(emailTextField, 50);
        genderComboBox.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
        levelComboBox.setItems(FXCollections.observableArrayList("Admin", "Kasir"));
        statusComboBox.setItems(FXCollections.observableArrayList("Aktif", "Tidak Aktif"));
        userTableView.setPlaceholder(new Label("Tidak ada data."));
        userTableView.setItems(users);
        usernameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        addressTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        phoneTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        emailTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        levelTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLevel()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (usernameTextField.getText().trim().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue() == null ||
                phoneTextField.getText().trim().isEmpty() ||
                levelComboBox.getValue() == null ||
                statusComboBox.getValue() == null) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);

            resetError();

            if (usernameTextField.getText().trim().isEmpty()) usernameTextField.setStyle("-fx-border-color: RED");
            if (passwordTextField.getText().isEmpty()) passwordTextField.setStyle("-fx-border-color: RED");
            if (nameTextField.getText().trim().isEmpty()) nameTextField.setStyle("-fx-border-color: RED");
            if (addressTextField.getText().trim().isEmpty()) addressTextField.setStyle("-fx-border-color: RED");
            if (genderComboBox.getValue() == null) genderComboBox.setStyle("-fx-border-color: RED");
            if (phoneTextField.getText().trim().isEmpty()) phoneTextField.setStyle("-fx-border-color: RED");
            if (levelComboBox.getValue() == null) levelComboBox.setStyle("-fx-border-color: RED");
            if (statusComboBox.getValue() == null) statusComboBox.setStyle("-fx-border-color: RED");

            return;
        }

        if (Helper.validateNumberPhone(phoneTextField)) {
            resetError();
            warningLabel.setText("No telp tidak valid");
            phoneTextField.setStyle("-fx-border-color: RED");
            phoneTextField.requestFocus();
            return;
        }

        if (!emailTextField.getText().trim().equals("") &&
                !EmailValidator.getInstance().isValid(emailTextField.getText())) {
            resetError();
            warningLabel.setText("Email tidak valid");
            emailTextField.setStyle("-fx-border-color: RED");
            emailTextField.requestFocus();
            return;
        }

        resetError();

        if (userDao.getUsername(usernameTextField.getText()) == 1) {
            content = "Username tersebut sudah digunakan!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        User user = new User();
        user.setUsername(usernameTextField.getText().trim());

        String password = Helper.hashPassword(passwordTextField.getText());

        user.setPassword(password);
        user.setName(nameTextField.getText().trim());
        user.setAddress(addressTextField.getText().trim());
        user.setGender(genderComboBox.getValue());
        user.setPhone(phoneTextField.getText().trim());

        if (emailTextField.getText().trim().isEmpty()) {
            user.setEmail("-");
        } else {
            user.setEmail(emailTextField.getText().trim());
        }

        user.setLevel(levelComboBox.getValue());
        user.setDateCreated(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (statusComboBox.getValue().equals("Aktif")) {
            user.setStatus("1");
        } else {
            user.setStatus("0");
        }

        try {
            if (userDao.addData(user) == 1) {
                users.clear();
                users.addAll(userDao.fetchAll());
                resetUser();
                usernameTextField.requestFocus();
                content = "Data berhasil ditambahkan!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (nameTextField.getText().trim().isEmpty() ||
                addressTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue() == null ||
                phoneTextField.getText().trim().isEmpty() ||
                levelComboBox.getValue() == null ||
                statusComboBox.getValue() == null) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);

            resetError();

            if (nameTextField.getText().trim().isEmpty()) nameTextField.setStyle("-fx-border-color: RED");
            if (addressTextField.getText().trim().isEmpty()) addressTextField.setStyle("-fx-border-color: RED");
            if (genderComboBox.getValue() == null) genderComboBox.setStyle("-fx-border-color: RED");
            if (phoneTextField.getText().trim().isEmpty()) phoneTextField.setStyle("-fx-border-color: RED");
            if (levelComboBox.getValue() == null) levelComboBox.setStyle("-fx-border-color: RED");
            if (statusComboBox.getValue() == null) statusComboBox.setStyle("-fx-border-color: RED");

            return;
        }

        if (Helper.validateNumberPhone(phoneTextField)) {
            resetError();
            warningLabel.setText("No telp tidak valid");
            phoneTextField.setStyle("-fx-border-color: RED");
            phoneTextField.requestFocus();
            return;
        }

        if (!emailTextField.getText().trim().equals("") &&
                !EmailValidator.getInstance().isValid(emailTextField.getText())) {
            resetError();
            warningLabel.setText("Email tidak valid");
            emailTextField.setStyle("-fx-border-color: RED");
            emailTextField.requestFocus();
            return;
        }

        resetError();

        if (userDao.getUsername(usernameTextField.getText().trim()) == 1 && !Common.oldUsername.equals(usernameTextField.getText().trim())) {
            content = "Username tersebut sudah digunakan!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        selectedUser.setUsername(usernameTextField.getText().trim());

        String password = Helper.hashPassword(passwordTextField.getText());
        selectedUser.setPassword(password);

        selectedUser.setName(nameTextField.getText().trim());
        selectedUser.setAddress(addressTextField.getText().trim());
        selectedUser.setGender(genderComboBox.getValue());
        selectedUser.setPhone(phoneTextField.getText().trim());

        if (emailTextField.getText().trim().isEmpty()) {
            selectedUser.setEmail("-");
        } else {
            selectedUser.setEmail(emailTextField.getText().trim());
        }

        selectedUser.setLevel(levelComboBox.getValue());

        if (statusComboBox.getValue().equals("Aktif")) {
            selectedUser.setStatus("1");
        } else {
            selectedUser.setStatus("0");
        }

        content = "Anda yakin ingin mengubah?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        if (passwordTextField.getText().isEmpty()) {
            try {
                if (userDao.updateDataExceptPassword(selectedUser) == 1) {
                    users.clear();
                    users.addAll(userDao.fetchAll());
                    resetUser();
                    userTableView.requestFocus();
                    content = "Data berhasil diubah!";
                    Helper.alert(Alert.AlertType.INFORMATION, content);
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                if (userDao.updateData(selectedUser) == 1) {
                    users.clear();
                    users.addAll(userDao.fetchAll());
                    resetUser();
                    userTableView.requestFocus();
                    content = "Data berhasil diubah!";
                    Helper.alert(Alert.AlertType.INFORMATION, content);
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        if (selectedUser.getTransactionAmount() > 0) {
            content = "Pengguna ini pernah melakukan transaksi,\ntidak dapat dihapus!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        if (selectedUser.getUsername().equals(Common.user.getUsername())) {
            content = "Pengguna ini sedang login, tidak dapat dihapus!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        content = "Anda yakin ingin menghapus?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (userDao.deleteData(selectedUser) == 1) {
                users.clear();
                users.addAll(userDao.fetchAll());
                resetUser();
                userTableView.requestFocus();
                content = "Data berhasil dihapus!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetUser();
    }

    @FXML
    private void userTableViewClicked(MouseEvent mouseEvent) {
        selectFromTableView();
        userTableView.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                selectFromTableView();
            }

            if (keyEvent.getCode() == KeyCode.ENTER) {
                usernameTextField.requestFocus();
            }
        });
    }

    private void selectFromTableView() {
        selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            usernameTextField.setText(selectedUser.getUsername());
            Common.oldUsername = selectedUser.getUsername();
            passwordTextField.setText("");
            passwordTextField.setPromptText("Kosongkan jika tidak mengubah");
            passwordLabel.setText("Password");
            nameTextField.setText(selectedUser.getName());
            addressTextField.setText(selectedUser.getAddress());
            genderComboBox.setValue(selectedUser.getGender());
            phoneTextField.setText(selectedUser.getPhone());
            emailTextField.setText(selectedUser.getEmail().equals("-") ? "" : selectedUser.getEmail());
            levelComboBox.setValue(selectedUser.getLevel());
            statusComboBox.setValue(selectedUser.getStatus());
            warningLabel.setText("");
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                userTableView.setItems(users);
                return;
            }

            ObservableList<User> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<User, ?>> columns = userTableView.getColumns();

            for (User value : users) {
                for (int j = 0; j < 2; j++) {
                    TableColumn<User, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            userTableView.setItems(tableItems);
        });
    }

    private void resetUser() {
        usernameTextField.clear();
        passwordTextField.clear();
        passwordTextField.setPromptText("");
        passwordLabel.setText("Password *");
        nameTextField.clear();
        addressTextField.clear();
        genderComboBox.setValue(null);
        phoneTextField.clear();
        emailTextField.clear();
        levelComboBox.setValue(null);
        statusComboBox.setValue(null);
        selectedUser = null;
        userTableView.getSelectionModel().clearSelection();
        resetError();
        usernameTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        usernameTextField.requestFocus();
    }

    private void resetError() {
        warningLabel.setText("");
        usernameTextField.setStyle("-fx-border-color: #424242");
        passwordTextField.setStyle("-fx-border-color: #424242");
        nameTextField.setStyle("-fx-border-color: #424242");
        addressTextField.setStyle("-fx-border-color: #424242");
        phoneTextField.setStyle("-fx-border-color: #424242");
        emailTextField.setStyle("-fx-border-color: #424242");
        levelComboBox.setStyle("-fx-border-color: #424242");
    }

    @FXML
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(masterMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void categoryMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(masterMenuButton, "Admin - Kategori", "category-view.fxml");
    }

    @FXML
    private void addStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Tambah Stok", "add-stock-view.fxml");
    }

    @FXML
    private void returnStockMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Return Stok", "return-stock-view.fxml");
    }

    @FXML
    private void supplierButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
    }

    @FXML
    private void stockReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Stok", "stock-report-view.fxml");
    }

    @FXML
    private void incomeReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Pendapatan", "income-report-view.fxml");
    }

    @FXML
    private void supplierReportMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(reportMenuButton, "Admin - Laporan Supplier", "supplier-report-view.fxml");
    }

    @FXML
    private void stockRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Stok", "stock-recap-view.fxml");
    }

    @FXML
    private void incomeRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Pendapatan", "income-recap-view.fxml");
    }

    @FXML
    private void supplierRecapMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(recapMenuButton, "Admin - Rekap Supplier", "supplier-recap-view.fxml");
    }

    @FXML
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
