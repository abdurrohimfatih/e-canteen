package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.input.KeyCode;
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
    private TableColumn<Category, Integer> noTableColumn;
    @FXML
    private TableColumn<Category, String> nameTableColumn;
    @FXML
    private TableColumn<Category, Integer> productAmountTableColumn;
    @FXML
    private TableColumn<Category, String> dateCreatedTableColumn;

    private ObservableList<Category> categories;
    private CategoryDaoImpl categoryDao;
    static Category selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryDao = new CategoryDaoImpl();
        categories = FXCollections.observableArrayList();

        try {
            categories.addAll(categoryDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Helper.addTextLimiter(nameTextField, 30);
        categoryTableView.setItems(categories);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(categoryTableView.getItems().indexOf(data.getValue()) + 1));
        noTableColumn.setSortable(false);
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        productAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getProductAmount()).asObject());
        dateCreatedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateCreated()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.showAndWait();
        } else {
            Category category = new Category();
            category.setName(nameTextField.getText().trim());
            category.setDateCreated(String.valueOf(LocalDate.now()));

            try {
                if (categoryDao.addData(category) == 1) {
                    categories.clear();
                    categories.addAll(categoryDao.fetchAll());
                    resetCategory();
                    nameTextField.requestFocus();
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
            alert.setContentText("Silakan isi semua field yang wajib diisi!");
            alert.showAndWait();
        } else {
            selectedCategory.setName(nameTextField.getText().trim());
            selectedCategory.setDateCreated(String.valueOf(LocalDate.now()));

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

            return category.getName().toLowerCase().contains(searchKeyword);
        }));

        SortedList<Category> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(categoryTableView.comparatorProperty());
        categoryTableView.setItems(sortedList);
    }

    @FXML
    private void categoryTableViewClicked(MouseEvent mouseEvent) {
        selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            idTextField.setText(String.valueOf(selectedCategory.getId()));
            nameTextField.setText(selectedCategory.getName());
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(selectedCategory.getProductAmount() > 0);
            resetButton.setDisable(false);

            if (mouseEvent.getClickCount() > 1) {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-category-view.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setTitle("Detail Kategori");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

                Stage categoryStage = (Stage) categoryMenuButton.getScene().getWindow();
                categoryStage.setOnCloseRequest(event -> {
                    stage.close();
                });
            }
        }
    }

    private void resetCategory() {
        idTextField.clear();
        nameTextField.clear();
        selectedCategory = null;
        categoryTableView.getSelectionModel().clearSelection();
        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetButton.setDisable(true);
        nameTextField.requestFocus();
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
    private void supplierMenuButtonAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(supplierMenuButton, "Admin - Supplier", "supplier-view.fxml");
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

    @FXML
    private void categoryTableViewReleased(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode().isWhitespaceKey() && selectedCategory != null) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("detail-category-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Detail Kategori");
            stage.setScene(scene);
            stage.show();
        }
    }
}
