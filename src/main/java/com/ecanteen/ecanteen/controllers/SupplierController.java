package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Supplier;
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
    private DatePicker lastSuppliedDateDatePicker;
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
    private TableColumn<Supplier, String> lastSuppliedDateTableColumn;

    private ObservableList<Supplier> suppliers;
    private SupplierDaoImpl supplierDao;
    private Supplier selectedSupplier;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        supplierDao = new SupplierDaoImpl();
        suppliers = FXCollections.observableArrayList();

        try {
            suppliers.addAll(supplierDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        supplierTableView.setItems(suppliers);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        lastSuppliedDateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastSuppliedDate()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (idTextField.getText().trim().isEmpty() || nameTextField.getText().trim().isEmpty() || lastSuppliedDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi id dan nama supplier!");
            alert.showAndWait();
        } else {
            Supplier supplier = new Supplier();
            supplier.setId(idTextField.getText().trim());
            supplier.setName(nameTextField.getText().trim());
            supplier.setLastSuppliedDate(String.valueOf(lastSuppliedDateDatePicker.getValue()));

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
        if (nameTextField.getText().trim().isEmpty() || lastSuppliedDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi nama supplier!");
            alert.showAndWait();
        } else {
            selectedSupplier.setName(nameTextField.getText().trim());
            selectedSupplier.setLastSuppliedDate(String.valueOf(lastSuppliedDateDatePicker.getValue()));

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
            lastSuppliedDateDatePicker.setValue(LocalDate.parse(selectedSupplier.getLastSuppliedDate()));
            idTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
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
        lastSuppliedDateDatePicker.setValue(null);
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
}
