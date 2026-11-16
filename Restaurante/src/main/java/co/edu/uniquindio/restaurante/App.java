package co.edu.uniquindio.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static final String BASE = "/co/edu/uniquindio/restaurante/";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(BASE + "vista/principal-view.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 800);
        scene.getStylesheets().add(App.class.getResource(BASE + "css/estilos.css").toExternalForm());
        stage.setTitle("Restaurante - Gestión de pedidos");
        stage.setScene(scene);
        stage.show();
    }
}