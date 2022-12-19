package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.CustomerDaoImpl;
import com.ecanteen.ecanteen.entities.Customer;
import com.ecanteen.ecanteen.utils.Common;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DetailCustomerCashierController implements Initializable {
    @FXML
    private AnchorPane containerPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> noTableColumn;
    @FXML
    private TableColumn<Customer, String> nameTableColumn;
    @FXML
    private TableColumn<Customer, String> genderTableColumn;
    @FXML
    private TableColumn<Customer, String> roleTableColumn;
    @FXML
    private TableColumn<Customer, String> amountTableColumn;
    @FXML
    private Button backButton;

    private Customer selectedCustomer;
    private ObservableList<Customer> customers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CustomerDaoImpl customerDao = new CustomerDaoImpl();
        customers = FXCollections.observableArrayList();

        try {
            customers.addAll(customerDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        customerTableView.setPlaceholder(new Label("Tidak ada data."));
        customerTableView.setItems(customers);
        noTableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(customerTableView.getItems().indexOf(data.getValue()) + 1));
        nameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        genderTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
        roleTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        amountTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBalance()));
    }

    @FXML
    private void containerPaneKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            Stage stage = (Stage) containerPane.getScene().getWindow();
            stage.close();
        }

        if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
            customerTableView.requestFocus();
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

    @FXML
    private void customerTableViewKeyReleased(KeyEvent keyEvent) {
        selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            customerTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, t1) -> selectedCustomer = customerTableView.getSelectionModel().getSelectedItem());

            if (keyEvent.getCode() == KeyCode.ENTER) {
                Common.buyer = addCustomer(selectedCustomer);
                ((Stage) containerPane.getScene().getWindow()).close();
            }
        }
    }

    private Customer addCustomer(Customer selectedCustomer) {
        Customer customer = new Customer();
        customer.setId(selectedCustomer.getId());
        customer.setName(selectedCustomer.getName());
        customer.setGender(selectedCustomer.getGender());
        customer.setRole(selectedCustomer.getRole());
        customer.setBalance(selectedCustomer.getBalance());

        return customer;
    }

    @FXML
    private void customerTableViewClicked(MouseEvent mouseEvent) {
        selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            if (mouseEvent.getClickCount() > 1) {
                Common.buyer = addCustomer(selectedCustomer);
                ((Stage) containerPane.getScene().getWindow()).close();
            }
        }
    }

    @FXML
    private void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
