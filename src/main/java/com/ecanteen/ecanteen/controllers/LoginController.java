package com.ecanteen.ecanteen.controllers;

import com.ecanteen.ecanteen.dao.LoginDao;
import com.ecanteen.ecanteen.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
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

    @FXML
    private void loginButtonAction(ActionEvent actionEvent) throws IOException, SQLException, ClassNotFoundException {
        if (usernameTextField.getText().trim().isEmpty() &&
                passwordPasswordField.getText().isEmpty()) {
            infoLabel.setText("Silakan isi username dan password dengan lengkap!");
        } else if (usernameTextField.getText().trim().isEmpty()) {
            infoLabel.setText("Silakan isi username terlebih dahulu!");
        } else if (passwordPasswordField.getText().isEmpty()) {
            infoLabel.setText("Silakan isi password terlebih dahulu!");
        } else {
            String username = usernameTextField.getText().trim();
            String password = passwordPasswordField.getText();

            password = Helper.hashPassword(password);

            LoginDao loginDao = new LoginDao();
            boolean flag = loginDao.validate(username, password);
            String status = loginDao.getStatus(username, password);
            String level = loginDao.getLevel(username, password);

            if (!flag) {
                infoLabel.setText("Username atau password salah. Silakan coba lagi!");
            } else if (status.equals("Tidak Aktif")) {
                infoLabel.setText("Status user sedang tidak aktif. Silakan aktifkan di admin!");
            } else {
                if (level.equals("Admin")) {
                    Helper.changePage(loginButton, "Admin - User", "user-view.fxml");
                } else {
                    Helper.changePage(loginButton, "Kasir - Produk", "product-view.fxml");
                }
            }
        }
    }
}
