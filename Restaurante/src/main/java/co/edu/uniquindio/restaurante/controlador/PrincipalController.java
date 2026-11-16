package co.edu.uniquindio.restaurante.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/** Ventana principal: barra lateral + área donde se carga cada pantalla. */
public class PrincipalController {
    private static final String RUTA_VISTAS = "/co/edu/uniquindio/restaurante/vista/";
    private static final String ACTIVO = "sidebar-item-activo";

    @FXML private StackPane contenido;
    @FXML private Button btnNavPedido;
    @FXML private Button btnNavPlatos;

    @FXML
    private void initialize() {
        onIrAPedido();
    }

    @FXML
    private void onIrAPedido() {
        mostrar("pedido-view.fxml", btnNavPedido, "Pantalla 1 · Realizar pedido (pendiente de implementar)");
    }

    @FXML
    private void onIrAPlatos() {
        mostrar("plato-view.fxml", btnNavPlatos, "Pantalla 2 · Platos (no se encontró la vista)");
    }

    private void mostrar(String fxml, Button activo, String textoSiNoExiste) {
        btnNavPedido.getStyleClass().remove(ACTIVO);
        btnNavPlatos.getStyleClass().remove(ACTIVO);
        activo.getStyleClass().add(ACTIVO);

        URL url = PrincipalController.class.getResource(RUTA_VISTAS + fxml);
        Node vista;
        if (url == null) {
            Label aviso = new Label(textoSiNoExiste);
            aviso.getStyleClass().add("subtitulo");
            vista = new VBox(aviso);
            ((VBox) vista).setStyle("-fx-padding: 32;");
        } else {
            try {
                vista = new FXMLLoader(url).load();
            } catch (IOException e) {
                throw new UncheckedIOException("No se pudo cargar " + fxml, e);
            }
        }
        contenido.getChildren().setAll(vista);
    }
}