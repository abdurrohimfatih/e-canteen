package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.UserDaoImpl;
import com.ecanteen.ecanteen.entities.User;
import com.ecanteen.ecanteen.utils.Common;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Label infoLabel;
    @FXML
    private Hyperlink forgetPasswordHyperlink;
    @FXML
    private Button loginButton;

    private UserDaoImpl userDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userDao = new UserDaoImpl();

        Helper.addTextLimiter(usernameTextField, 20);
    }

    @FXML
    private void loginButtonAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        String username = usernameTextField.getText().trim();
        String password = passwordPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            infoLabel.setText("Silakan isi username dan password dengan lengkap!");
            return;
        }

        password = Helper.hashPassword(password);
        User user = userDao.login(username, password);

        if (user != null && user.getUsername().equals(username)) {
            if (user.getStatus().equals("1")) {
                Common.user = user;
                if (user.getLevel().equals("Admin")) {
                    Helper.changePage(loginButton, "Admin - Produk", "product-view.fxml");
                } else {
                    Helper.changePage(loginButton, "Kasir - Transaksi", "transaction-view.fxml");
                }
            } else {
                infoLabel.setText("Status user sedang tidak aktif. Silakan aktifkan di admin!");
            }
        } else {
            infoLabel.setText("Username atau password salah. Silakan coba lagi!");
        }
    }
}
