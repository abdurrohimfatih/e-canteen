package com.ecanteen.ecanteen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        if (Screen.getPrimary().getBounds().getWidth() == 1366 &&
                Screen.getPrimary().getBounds().getHeight() == 768) {
            stage.setMaximized(true);
        }

        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("image/logo.png"))));
        stage.setTitle("Login | IDC");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}