package com.example.librarymanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LibraryApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("/com/example/librarymanagement/library-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 620, 440);

        stage.setTitle("Alexandria OS - System Manager");
        stage.setScene(scene);
        stage.show();

        // Bind thread shutdown to application close
        stage.setOnCloseRequest(event -> {
            LibraryController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.shutdownScheduler();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}