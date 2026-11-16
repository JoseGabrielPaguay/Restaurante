package co.edu.uniquindio.restaurante.modelo.excepciones;

import java.util.List;

/**
 * Se lanza cuando se incumple una regla de negocio. Puede traer varios errores a la vez
 * para mostrarlos junto a cada campo de la pantalla.
 */
public class ReglaNegocioException extends Exception {
    private static final long serialVersionUID = 1L;

    public static final String CAMPO_MESA = "mesa";
    public static final String CAMPO_MESERO = "mesero";
    public static final String CAMPO_PLATO = "plato";
    public static final String CAMPO_CANTIDAD = "cantidad";
    public static final String CAMPO_ORDEN = "orden";

    private final List<ErrorRegla> errores;

    public ReglaNegocioException(String regla, String campo, String mensaje) {
        this(List.of(new ErrorRegla(regla, campo, mensaje)));
    }

    public ReglaNegocioException(List<ErrorRegla> errores) {
        super(errores.get(0).mensaje());
        this.errores = List.copyOf(errores);
    }

    public List<ErrorRegla> getErrores() {
        return errores;
    }

    /** Mensaje asociado a un campo, o null si ese campo no tiene error. */
    public String mensajeDe(String campo) {
        for (ErrorRegla e : errores) {
            if (e.campo().equals(campo)) {
                return e.mensaje();
            }
        }
        return null;
    }
}
