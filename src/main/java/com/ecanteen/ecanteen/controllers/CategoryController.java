package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
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

public class CategoryController implements Initializable {
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
    private DatePicker dateCreatedDatePicker;
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
    private TableView<Category> categoryTableView;
    @FXML
    private TableColumn<Category, String> idTableColumn;
    @FXML
    private TableColumn<Category, String> nameTableColumn;
    @FXML
    private TableColumn<Category, String> dateCreatedTableColumn;

    private ObservableList<Category> categories;
    private CategoryDaoImpl categoryDao;
    private Category selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryDao = new CategoryDaoImpl();
        categories = FXCollections.observableArrayList();

        try {
            categories.addAll(categoryDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        dateCreatedDatePicker.setValue(LocalDate.now());

        categoryTableView.setItems(categories);
        idTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        dateCreatedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateCreated()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (idTextField.getText().trim().isEmpty() || nameTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi id dan nama supplier!");
            alert.showAndWait();
        } else {
            Category category = new Category();
            category.setId(idTextField.getText().trim());
            category.setName(nameTextField.getText().trim());
            category.setDateCreated(String.valueOf(dateCreatedDatePicker.getValue()));

            try {
                if (categoryDao.addData(category) == 1) {
                    categories.clear();
                    categories.addAll(categoryDao.fetchAll());
                    resetCategory();
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
        if (nameTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi nama supplier!");
            alert.showAndWait();
        } else {
            selectedCategory.setName(nameTextField.getText().trim());
            selectedCategory.setDateCreated(String.valueOf(dateCreatedDatePicker.getValue()));

            try {
                if (categoryDao.updateData(selectedCategory) == 1) {
                    categories.clear();
                    categories.addAll(categoryDao.fetchAll());
                    resetCategory();
                    categoryTableView.requestFocus();
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
                if (categoryDao.deleteData(selectedCategory) == 1) {
                    categories.clear();
                    categories.addAll(categoryDao.fetchAll());
                    resetCategory();
                    categoryTableView.requestFocus();
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
        resetCategory();
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        FilteredList<Category> filteredList = new FilteredList<>(categories, b -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(category -> {
            if (newValue.isEmpty()) {
                return true;
            }

            String searchKeyword = newValue.toLowerCase().trim();

            if (String.valueOf(category.getId()).toLowerCase().contains(searchKeyword)) {
                return true;
            } else return category.getName().toLowerCase().contains(searchKeyword);
        }));

        SortedList<Category> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(categoryTableView.comparatorProperty());
        categoryTableView.setItems(sortedList);
    }

    @FXML
    private void categoryTableViewClicked(MouseEvent mouseEvent) {
        selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            idTextField.setText(selectedCategory.getId());
            nameTextField.setText(selectedCategory.getName());
            dateCreatedDatePicker.setValue(LocalDate.parse(selectedCategory.getDateCreated()));
            idTextField.setDisable(true);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            resetButton.setDisable(false);
        }
    }

    private void resetCategory() {
        idTextField.clear();
        nameTextField.clear();
        dateCreatedDatePicker.setValue(null);
        selectedCategory = null;
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
    private void supplierMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Stage supplierStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("supplier-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        supplierStage.setTitle("Supplier | e-Canteen");
        supplierStage.setMaximized(true);
        supplierStage.setScene(scene);
        supplierStage.show();

        Stage stage = (Stage) supplierMenuButton.getScene().getWindow();
        stage.close();
    }
}
