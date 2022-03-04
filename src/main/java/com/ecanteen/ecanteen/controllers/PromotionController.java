package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.entities.Product;
import com.ecanteen.ecanteen.entities.Promotion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class PromotionController {
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
    private ComboBox<Product> productComboBox;
    @FXML
    private TextField percentageTextField;
    @FXML
    private TextArea descriptionTextField;
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
    private TableView<Promotion> promotionTableView;
    @FXML
    private TableColumn<Promotion, String> idTableColumn;
    @FXML
    private TableColumn<Promotion, String> nameTableColumn;
    @FXML
    private TableColumn<Promotion, Product> productBarcodeTableColumn;
    @FXML
    private TableColumn<Promotion, Product> productNameTableColumn;
    @FXML
    private TableColumn<Promotion, Integer> percentageTableColumn;
    @FXML
    private TableColumn<Promotion, String> descriptionTableColumn;

    @FXML
    private void addButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void updateButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void deleteButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void resetButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void searchTextFieldKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    private void categoryTableViewClicked(MouseEvent mouseEvent) {
    }

    @FXML
    private void productMenuButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void categoryMenuButtonAction(ActionEvent actionEvent) {
    }

    @FXML
    private void supplierMenuButtonAction(ActionEvent actionEvent) {
    }
}
