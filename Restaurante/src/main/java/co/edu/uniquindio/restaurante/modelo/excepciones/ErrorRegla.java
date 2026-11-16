package co.edu.uniquindio.restaurante.modelo.excepciones;

import java.io.Serializable;

/** Un incumplimiento de regla: codigo de la regla, campo de la pantalla y mensaje. */
public record ErrorRegla(String regla, String campo, String mensaje) implements Serializable {
}
