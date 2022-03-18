package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.UserDaoImpl;
import com.ecanteen.ecanteen.entities.User;
import com.ecanteen.ecanteen.utils.Helper;
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

public class UserController implements Initializable {
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
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
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
    private ComboBox<String> levelComboBox;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userDao = new UserDaoImpl();
        users = FXCollections.observableArrayList();

        try {
            users.addAll(userDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.addTextLimiter(usernameTextField, 20);
        Helper.addTextLimiter(nameTextField, 30);
        Helper.addTextLimiterTextArea(addressTextArea, 15);
        Helper.addTextLimiter(phoneTextField, 14);
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
    private void addButtonAction(ActionEvent actionEvent) {
        if (usernameTextField.getText().trim().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextArea.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                levelComboBox.getValue().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.showAndWait();
        } else {
            User user = new User();
            user.setUsername(usernameTextField.getText().trim());
            user.setPassword(passwordTextField.getText());
            user.setName(nameTextField.getText().trim());
            user.setAddress(addressTextArea.getText().trim());
            user.setGender(genderComboBox.getValue());
            user.setPhone(phoneTextField.getText().trim());
            if (emailTextField.getText().trim().isEmpty()) {
                user.setEmail("-");
            } else {
                user.setEmail(emailTextField.getText().trim());
            }
            user.setLevel(levelComboBox.getValue());
            user.setDateCreated(String.valueOf(LocalDate.now()));
            user.setStatus(statusComboBox.getValue());

            try {
                if (userDao.addData(user) == 1) {
                    users.clear();
                    users.addAll(userDao.fetchAll());
                    resetUser();
                    usernameTextField.requestFocus();
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
        if (usernameTextField.getText().trim().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                nameTextField.getText().trim().isEmpty() ||
                addressTextArea.getText().trim().isEmpty() ||
                genderComboBox.getValue().isEmpty() ||
                levelComboBox.getValue().isEmpty() ||
                statusComboBox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.showAndWait();
        } else {
            selectedUser.setUsername(usernameTextField.getText().trim());
            selectedUser.setPassword(passwordTextField.getText());
            selectedUser.setName(nameTextField.getText().trim());
            selectedUser.setAddress(addressTextArea.getText().trim());
            selectedUser.setGender(genderComboBox.getValue());
            selectedUser.setPhone(phoneTextField.getText().trim());
            if (emailTextField.getText().trim().isEmpty()) {
                selectedUser.setEmail("-");
            } else {
                selectedUser.setEmail(emailTextField.getText().trim());
            }
            selectedUser.setLevel(levelComboBox.getValue());
            selectedUser.setStatus(statusComboBox.getValue());

            try {
                if (userDao.updateData(selectedUser) == 1) {
                    users.clear();
                    users.addAll(userDao.fetchAll());
                    resetUser();
                    userTableView.requestFocus();
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
                if (userDao.deleteData(selectedUser) == 1) {
                    users.clear();
                    users.addAll(userDao.fetchAll());
                    resetUser();
                    userTableView.requestFocus();
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
        resetUser();
    }

    @FXML
    private void userTableViewClicked(MouseEvent mouseEvent) {
        selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            usernameTextField.setText(selectedUser.getUsername());
            passwordTextField.setText(selectedUser.getPassword());
            nameTextField.setText(selectedUser.getName());
            addressTextArea.setText(selectedUser.getAddress());
            genderComboBox.setValue(selectedUser.getGender());
            phoneTextField.setText(selectedUser.getPhone());
            emailTextField.setText(selectedUser.getEmail());
            levelComboBox.setValue(selectedUser.getLevel());
            statusComboBox.setValue(selectedUser.getStatus());
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        FilteredList<User> filteredList = new FilteredList<>(users, b -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(user -> {
            if (newValue.isEmpty()) {
                return true;
            }

            String searchKeyword = newValue.toLowerCase().trim();

            if (user.getUsername().toLowerCase().contains(searchKeyword)) {
                return true;
            } else return user.getName().toLowerCase().contains(searchKeyword);
        }));

        SortedList<User> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(userTableView.comparatorProperty());
        userTableView.setItems(sortedList);
    }

    private void resetUser() {
        usernameTextField.clear();
        passwordTextField.clear();
        nameTextField.clear();
        addressTextArea.clear();
        genderComboBox.setValue(null);
        phoneTextField.clear();
        emailTextField.clear();
        levelComboBox.setValue(null);
        statusComboBox.setValue(null);
        selectedUser = null;
        userTableView.getSelectionModel().clearSelection();
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        usernameTextField.requestFocus();
        infoLabel.setText("");
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
