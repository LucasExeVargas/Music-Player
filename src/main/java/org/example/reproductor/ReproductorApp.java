package org.example.reproductor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReproductorApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        ReproductorApp.class.getResource(
                                "reproductor-view.fxml"
                        )
                );

        Scene scene =
                new Scene(loader.load(), 1600, 900);

        stage.setTitle("Reproductor de Musica");

        stage.setMaximized(true);

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}