package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Helper {
    public static void changePage(Control control, String title, String fxmlFile) throws IOException {
        Stage stage = (Stage) control.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Parent root = fxmlLoader.load();
        stage.setTitle(title + " | e-Canteen");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }

    public static String hashPassword(String password)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));

            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }

            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
