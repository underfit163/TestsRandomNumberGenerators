package forms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class FormTestGen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/testGen.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add((Objects.requireNonNull(getClass().getResource("/fxml/style.css"))).toExternalForm());
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/fxml/iconSsau.jpg"))));
        stage.setTitle("Тестирование случайных числел");
        stage.setResizable(false);
        stage.show();
    }
}
