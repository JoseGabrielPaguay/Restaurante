module co.edu.uniquindio.restaurante {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.restaurante to javafx.fxml;
    exports co.edu.uniquindio.restaurante;
}