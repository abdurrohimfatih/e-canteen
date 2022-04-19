package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.Main;
import com.ecanteen.ecanteen.dao.CategoryDaoImpl;
import com.ecanteen.ecanteen.entities.Category;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {
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
    private String content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryDao = new CategoryDaoImpl();
        categories = FXCollections.observableArrayList();

        try {
            categories.addAll(categoryDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        profileButton.setText(Common.user.getName());
        Helper.addTextLimiter(nameTextField, 30);
        categoryTableView.setItems(categories);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(categoryTableView.getItems().indexOf(data.getValue()) + 1));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        productAmountTableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getProductAmount()).asObject());
        dateCreatedTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateCreated()));
    }

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty()) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            Category category = new Category();
            category.setName(nameTextField.getText().trim());
            category.setDateCreated(Helper.formattedDateNow());

            try {
                if (categoryDao.addData(category) == 1) {
                    categories.clear();
                    categories.addAll(categoryDao.fetchAll());
                    resetCategory();
                    nameTextField.requestFocus();
                    content = "Data berhasil ditambahkan!";
                    Helper.alert(Alert.AlertType.INFORMATION, content);
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
        if (nameTextField.getText().trim().isEmpty()) {
            content = "Silakan isi semua field yang wajib diisi!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            selectedCategory.setName(nameTextField.getText().trim());
            selectedCategory.setDateCreated(Helper.formattedDateNow());

            content = "Anda yakin ingin mengubah?";
            if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
                try {
                    if (categoryDao.updateData(selectedCategory) == 1) {
                        categories.clear();
                        categories.addAll(categoryDao.fetchAll());
                        resetCategory();
                        categoryTableView.requestFocus();
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
    private void deleteButtonAction(ActionEvent actionEvent) throws IOException {
        if (selectedCategory.getProductAmount() > 0) {
            content = "Kategori ini memiliki produk, tidak dapat dihapus!";
            Helper.alert(Alert.AlertType.ERROR, content);
        } else {
            content = "Anda yakin ingin menghapus?";
            if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
                try {
                    if (categoryDao.deleteData(selectedCategory) == 1) {
                        categories.clear();
                        categories.addAll(categoryDao.fetchAll());
                        resetCategory();
                        categoryTableView.requestFocus();
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
        resetCategory();
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
        searchTextField.textProperty().addListener(observable -> {
            if (searchTextField.textProperty().get().isEmpty()) {
                categoryTableView.setItems(categories);
                return;
            }

            ObservableList<Category> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Category, ?>> columns = categoryTableView.getColumns();

            for (Category value : categories) {
                for (int j = 1; j < 2; j++) {
                    TableColumn<Category, ?> col = columns.get(j);
                    String cellValue = String.valueOf(col.getCellData(value)).toLowerCase();

                    if (cellValue.contains(searchTextField.getText().toLowerCase().trim())) {
                        tableItems.add(value);
                        break;
                    }
                }
            }

            categoryTableView.setItems(tableItems);
        });
    }

    @FXML
    private void categoryTableViewClicked(MouseEvent mouseEvent) {
        selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            idTextField.setText(String.valueOf(selectedCategory.getId()));
            nameTextField.setText(selectedCategory.getName());
            addButton.setDisable(true);
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
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
                stage.initOwner(categoryTableView.getScene().getWindow());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
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
    }

    @FXML
    private void productMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Produk", "product-view.fxml");
    }

    @FXML
    private void promotionMenuItemAction(ActionEvent actionEvent) throws IOException {
        Helper.changePage(stockMenuButton, "Admin - Promosi", "promotion-view.fxml");
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
    private void logoutButtonAction(ActionEvent actionEvent) throws IOException {
        content = "Anda yakin ingin keluar?";

        if (Helper.alert(Alert.AlertType.CONFIRMATION, content) == ButtonType.OK) {
            Helper.changePage(logoutButton, "Login", "login-view.fxml");
        }
    }
}
