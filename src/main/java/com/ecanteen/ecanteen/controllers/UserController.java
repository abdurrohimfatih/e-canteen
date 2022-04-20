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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserController implements Initializable {
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
    private TextField usernameTextField;
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

        profileButton.setText(Common.user.getName());
        Helper.toNumberField(phoneTextField);
        Helper.addTextLimiter(usernameTextField, 20);
        Helper.addTextLimiter(nameTextField, 30);
        Helper.addTextLimiter(addressTextField, 15);
        Helper.addTextLimiter(phoneTextField, 14);
        Helper.addTextLimiter(emailTextField, 50);
        genderComboBox.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
        levelComboBox.setItems(FXCollections.observableArrayList("Admin", "Kasir"));
        statusComboBox.setItems(FXCollections.observableArrayList("Aktif", "Tidak Aktif"));
        userTableView.setItems(users);
        usernameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        addressTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        phoneTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        levelTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLevel()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (usernameTextField.getText().trim().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                levelComboBox.getValue().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else if (!Helper.validateNumberPhone(phoneTextField)) {
            warningLabel.setText("No telp tidak valid");
            phoneTextField.setStyle("-fx-border-color: RED");
            phoneTextField.requestFocus();
        } else if (!emailTextField.getText().trim().equals("") &&
                !EmailValidator.getInstance().isValid(emailTextField.getText())) {
            phoneTextField.setStyle("-fx-border-color: #424242");
            warningLabel.setText("Email tidak valid");
            emailTextField.setStyle("-fx-border-color: RED");
            emailTextField.requestFocus();
        } else {
            warningLabel.setText("");
            phoneTextField.setStyle("-fx-border-color: #424242");
            emailTextField.setStyle("-fx-border-color: #424242");

            if (userDao.getUsername(usernameTextField.getText()) == 1) {
                content = "Username tersebut sudah digunakan!";
                Helper.alert(Alert.AlertType.ERROR, content);
            } else {
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
                user.setDateCreated(Helper.formattedDateNow());

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
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (usernameTextField.getText().trim().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                phoneTextField.getText().trim().isEmpty() ||
                levelComboBox.getValue().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else if (!Helper.validateNumberPhone(phoneTextField)) {
            warningLabel.setText("No telp tidak valid");
            phoneTextField.setStyle("-fx-border-color: RED");
            phoneTextField.requestFocus();
        } else if (!emailTextField.getText().trim().equals("") &&
                !EmailValidator.getInstance().isValid(emailTextField.getText())) {
            phoneTextField.setStyle("-fx-border-color: #424242");
            warningLabel.setText("Email tidak valid");
            emailTextField.setStyle("-fx-border-color: RED");
            emailTextField.requestFocus();
        } else {
            warningLabel.setText("");
            phoneTextField.setStyle("-fx-border-color: #424242");
            emailTextField.setStyle("-fx-border-color: #424242");

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
            if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
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
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        content = "Anda yakin ingin menghapus?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
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
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetUser();
    }

    @FXML
    private void userTableViewClicked(MouseEvent mouseEvent) {
        selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            usernameTextField.setText(selectedUser.getUsername());
            nameTextField.setText(selectedUser.getName());
            addressTextField.setText(selectedUser.getAddress());
            genderComboBox.setValue(selectedUser.getGender());
            phoneTextField.setText(selectedUser.getPhone());
            emailTextField.setText(selectedUser.getEmail());
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
        nameTextField.clear();
        addressTextField.clear();
        genderComboBox.setValue(null);
        phoneTextField.clear();
        emailTextField.clear();
        levelComboBox.setValue(null);
        statusComboBox.setValue(null);
        selectedUser = null;
        userTableView.getSelectionModel().clearSelection();
        warningLabel.setText("");
        phoneTextField.setStyle("-fx-border-color: #424242");
        emailTextField.setStyle("-fx-border-color: #424242");
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        usernameTextField.requestFocus();
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
