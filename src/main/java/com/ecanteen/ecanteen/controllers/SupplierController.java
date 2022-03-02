package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.SupplierDaoImpl;
import com.ecanteen.ecanteen.entities.Supplier;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {
    @FXML
    private Button productMenuButton;
    @FXML
    private Button categoryMenuButton;
    @FXML
    private Button supplierMenuButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private DatePicker lastSuppliedDateTextField;
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
        if (idTextField.getText().trim().isEmpty() || nameTextField.getText().trim().isEmpty() || lastSuppliedDateTextField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi id dan nama supplier!");
            alert.showAndWait();
        } else {
            Supplier supplier = new Supplier();
            supplier.setId(idTextField.getText().trim());
            supplier.setName(nameTextField.getText().trim());
            supplier.setLastSuppliedDate(String.valueOf(lastSuppliedDateTextField.getValue()));

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
        if (nameTextField.getText().trim().isEmpty() || lastSuppliedDateTextField.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi nama supplier!");
            alert.showAndWait();
        } else {
            selectedSupplier.setName(nameTextField.getText().trim());
            selectedSupplier.setLastSuppliedDate(String.valueOf(lastSuppliedDateTextField.getValue()));

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
            lastSuppliedDateTextField.setValue(LocalDate.parse(selectedSupplier.getLastSuppliedDate()));
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

            if (String.valueOf(supplier.getId()).toLowerCase().contains(searchKeyword)) {
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
        lastSuppliedDateTextField.setValue(null);
        selectedSupplier = null;
        idTextField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        idTextField.requestFocus();
        infoLabel.setText("");
    }

    @FXML
    private void productMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Stage productStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("product-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        productStage.setTitle("Produk | e-Canteen");
        productStage.setMaximized(true);
        productStage.setScene(scene);
        productStage.show();

        Stage stage = (Stage) productMenuButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void categoryMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Stage categoryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("category-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        categoryStage.setTitle("Kategori | e-Canteen");
        categoryStage.setMaximized(true);
        categoryStage.setScene(scene);
        categoryStage.show();

        Stage stage = (Stage) categoryMenuButton.getScene().getWindow();
        stage.close();
    }
}
