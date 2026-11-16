module co.edu.uniquindio.restaurante {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.restaurante to javafx.fxml;
    exports co.edu.uniquindio.restaurante;

    exports co.edu.uniquindio.restaurante.modelo.entidad;
    exports co.edu.uniquindio.restaurante.modelo.excepciones;
    exports co.edu.uniquindio.restaurante.modelo.persistencia;
    exports co.edu.uniquindio.restaurante.modelo.servicio;
    opens co.edu.uniquindio.restaurante.modelo.entidad to javafx.base;

    // Descomentar cuando el paquete ya tenga clases .java:
    // opens co.edu.uniquindio.restaurante.controlador to javafx.fxml;
    // opens co.edu.uniquindio.restaurante.vista to javafx.fxml;
}