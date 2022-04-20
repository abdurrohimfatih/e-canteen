package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Helper {
    public static void changePage(Control control, String title, String fxmlFile) throws IOException {
        Stage stage = (Stage) control.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(title + " | IDC");
        stage.setScene(scene);
        stage.show();
    }

    public static void addTextLimiter(final TextInputControl tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }

    public static void toNumberField(final TextField tf) {
        tf.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.matches("\\d*")) {
                tf.setText(t1.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static boolean validateNumberPhone(final TextField textField) {
        if (textField.getText().matches("\\d{10}")) return true;
        else if (textField.getText().matches("\\d{11}")) return true;
        else if (textField.getText().matches("\\d{12}")) return true;
        else return textField.getText().matches("\\d{13}");
    }

    public static void addThousandSeparator(final TextInputControl textField) {
        final char separator = '.';
        final Pattern p = Pattern.compile("[0-9" + separator + "]*");
        textField.setTextFormatter(new TextFormatter<>(c -> {
            if (!c.isContentChange()) {
                return c;
            }
            String newText = c.getControlNewText();
            if (newText.isEmpty()) {
                return c;
            }
            if (!p.matcher(newText).matches()) {
                return null;
            }

            int suffixCount = c.getControlText().length() - c.getRangeEnd();
            int digits = suffixCount - suffixCount / 4;
            StringBuilder sb = new StringBuilder();

            if (digits % 3 == 0 && digits > 0 && suffixCount % 4 != 0) {
                sb.append(separator);
            }

            for (int i = c.getRangeStart() + c.getText().length() - 1; i >= 0; i--) {
                char letter = newText.charAt(i);
                if (Character.isDigit(letter)) {
                    sb.append(letter);
                    digits++;
                    if (digits % 3 == 0) {
                        sb.append(separator);
                    }
                }
            }

            if (digits % 3 == 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.reverse();
            int length = sb.length();

            c.setRange(0, c.getRangeEnd());
            c.setText(sb.toString());
            c.setCaretPosition(length);
            c.setAnchor(length);

            return c;
        }));
    }

    public static void formatDatePicker(DatePicker datePicker) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                }
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String s) {
                if (s == null || s.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(s, dateTimeFormatter);
            }
        });
    }

    public static String formattedDateNow() {
        LocalDate now = LocalDate.now();
        return now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static String formattedTimeNow() {
        LocalTime now = LocalTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static LocalDate formatter(String text) {
        return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
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

    public static ButtonType alert(Alert.AlertType alertType, String content) {
        Image image;
        String title;
        if (alertType == Alert.AlertType.CONFIRMATION) {
            image = new Image(String.valueOf(Main.class.getResource("image/confirm.png")));
            title = "Konfirmasi";
        } else if (alertType == Alert.AlertType.ERROR) {
            image = new Image(String.valueOf(Main.class.getResource("image/warning.png")));
            title = "Error";
        } else {
            image = new Image(String.valueOf(Main.class.getResource("image/success.png")));
            title = "Sukses";
        }

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(String.valueOf(Main.class.getResource("css/style.css")));
        pane.getStyleClass().add("myDialog");

        alert.showAndWait();

        return alert.getResult();
    }
}
