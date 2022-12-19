package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CustomerDaoImpl;
import com.ecanteen.ecanteen.entities.Customer;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
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
    private TextField searchTextField;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private ComboBox<String> roleComboBox;
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
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> noTableColumn;
    @FXML
    private TableColumn<Customer, String> nameTableColumn;
    @FXML
    private TableColumn<Customer, String> genderTableColumn;
    @FXML
    private TableColumn<Customer, String> balanceTableColumn;
    @FXML
    private TableColumn<Customer, String> roleTableColumn;

    private ObservableList<Customer> customers;
    private CustomerDaoImpl customerDao;
    private Customer selectedCustomer;
    private String content;
    private String previousBalance;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerDao = new CustomerDaoImpl();
        customers = FXCollections.observableArrayList();

        try {
            customers.addAll(customerDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.addTextLimiter(nameTextField, 30);
        genderComboBox.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
        roleComboBox.setItems(FXCollections.observableArrayList("Guru/Staf", "Siswa"));
        customerTableView.setPlaceholder(new Label("Tidak ada data."));
        customerTableView.setItems(customers);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(customerTableView.getItems().indexOf(data.getValue()) + 1));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        genderTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
        roleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        balanceTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBalance()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty() ||
                genderComboBox.getValue() == null ||
                roleComboBox.getValue() == null) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);

            resetError();

            if (nameTextField.getText().trim().isEmpty()) nameTextField.setStyle("-fx-border-color: RED");
            if (genderComboBox.getValue() == null) genderComboBox.setStyle("-fx-border-color: RED");
            if (roleComboBox.getValue() == null) roleComboBox.setStyle("-fx-border-color: RED");

            return;
        }

        resetError();

        Customer customer = new Customer();
        customer.setName(nameTextField.getText().trim());
        customer.setGender(genderComboBox.getValue());
        customer.setRole(roleComboBox.getValue());

        try {
            if (customerDao.addData(customer) == 1) {
                customers.clear();
                customers.addAll(customerDao.fetchAll());
                resetCustomer();
                nameTextField.requestFocus();
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
                genderComboBox.getValue() == null ||
                roleComboBox.getValue() == null) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);

            resetError();

            if (nameTextField.getText().trim().isEmpty()) nameTextField.setStyle("-fx-border-color: RED");
            if (genderComboBox.getValue() == null) genderComboBox.setStyle("-fx-border-color: RED");
            if (roleComboBox.getValue() == null) roleComboBox.setStyle("-fx-border-color: RED");

            return;
        }

        resetError();

        selectedCustomer.setName(nameTextField.getText());
        selectedCustomer.setGender(genderComboBox.getValue());
        selectedCustomer.setRole(roleComboBox.getValue());

        content = "Anda yakin ingin mengubah?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (customerDao.updateData(selectedCustomer) == 1) {
                customers.clear();
                customers.addAll(customerDao.fetchAll());
                resetCustomer();
                customerTableView.requestFocus();
                content = "Data berhasil diubah!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
        if (selectedCustomer.getTransactionAmount() > 0) {
            content = "Pelanggan ini pernah melakukan transaksi,\ntidak dapat dihapus!";
            Helper.alert(Alert.AlertType.ERROR, content);
            return;
        }

        content = "Anda yakin ingin menghapus?";
        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) != ButtonType.OK) {
            return;
        }

        try {
            if (customerDao.deleteData(selectedCustomer) == 1) {
                customers.clear();
                customers.addAll(customerDao.fetchAll());
                resetCustomer();
                customerTableView.requestFocus();
                content = "Data berhasil dihapus!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
        resetCustomer();
    }

    @FXML
    private void customerTableViewClicked(MouseEvent mouseEvent) {
        selectFromTableView();

        customerTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, t1) -> selectFromTableView());

        customerTableView.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                nameTextField.requestFocus();
            }
        });
    }

    private void selectFromTableView() {
        selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            idTextField.setText(String.valueOf(selectedCustomer.getId()));
            nameTextField.setText(selectedCustomer.getName());
            genderComboBox.setValue(selectedCustomer.getGender());
            roleComboBox.setValue(selectedCustomer.getRole());
            previousBalance = selectedCustomer.getBalance();
            warningLabel.setText("");
            addButton.setText("TOPUP");
            addButton.setOnAction(this::topupButtonAction);
            addButton.setDefaultButton(false);
            updateButton.setDisable(false);
            updateButton.setDefaultButton(true);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    private void topupButtonAction(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Topup");
        dialog.setContentText("Jumlah topup Rp");
        dialog.setHeaderText("Saldo sebelumnya\t: Rp " + previousBalance);
        dialog.setGraphic(null);
        DialogPane pane = dialog.getDialogPane();
        pane.setPrefWidth(400);
        pane.getStylesheets().add(String.valueOf(Main.class.getResource("css/style.css")));
        pane.getStyleClass().add("myDialog");
        pane.getContent().setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: center;");
        pane.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                Stage stage = (Stage) pane.getScene().getWindow();
                stage.close();
            }
        });
        ((Button) pane.lookupButton(ButtonType.OK)).setText("OK");
        ((Button) pane.lookupButton(ButtonType.CANCEL)).setText("Batal");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("image/logo.png"))));
        Optional<String> result;
        TextInputControl control = dialog.getEditor();
        control.setStyle("-fx-font-size: 20px; -fx-pref-width: 200px;");
        Helper.addThousandSeparator(control);

        int balanceStartInt;
        int balanceAddInt = 0;
        int balanceEndInt = 0;
        do {
            result = dialog.showAndWait();
            if (result.isPresent()) {
                if (!dialog.getResult().equals("")) {
                    balanceStartInt = Helper.currencyToInt(selectedCustomer.getBalance());
                    balanceAddInt = Helper.currencyToInt(dialog.getResult());
                    balanceEndInt = balanceStartInt + balanceAddInt;
                }

                dialog.setHeaderText("Jumlah topup tidak boleh kosong!\n" +
                        "Saldo sebelumnya\t: Rp " + previousBalance);
            } else {
                return;
            }
        } while (dialog.getResult().equals("") || balanceAddInt == 0);

        try {
            if (customerDao.updateBalance(balanceEndInt, selectedCustomer) == 1) {
                customers.clear();
                customers.addAll(customerDao.fetchAll());
                resetCustomer();
                customerTableView.requestFocus();
                content = "Berhasil mengisi saldo!";
                Helper.alert(Alert.AlertType.INFORMATION, content);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                customerTableView.setItems(customers);
                return;
            }

            ObservableList<Customer> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Customer, ?>> columns = customerTableView.getColumns();

            for (Customer value : customers) {
                for (int j = 1; j < 4; j++) {
                    TableColumn<Customer, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            customerTableView.setItems(tableItems);
        });
    }

    private void resetCustomer() {
        idTextField.clear();
        nameTextField.clear();
        genderComboBox.setValue(null);
        roleComboBox.setValue(null);
        selectedCustomer = null;
        customerTableView.getSelectionModel().clearSelection();
        resetError();
        addButton.setText("TAMBAH");
        addButton.setOnAction(this::addButtonAction);
        addButton.setDefaultButton(true);
        updateButton.setDisable(true);
        updateButton.setDefaultButton(false);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        nameTextField.requestFocus();
    }

    private void resetError() {
        warningLabel.setText("");
        nameTextField.setStyle("-fx-border-color: #424242");
        genderComboBox.setStyle("-fx-border-color: #424242");
        roleComboBox.setStyle("-fx-border-color: #424242");
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
    private void userButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(userMenuButton, "Admin - User", "user-view.fxml");
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
