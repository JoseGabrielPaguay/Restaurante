package co.edu.uniquindio.restaurante.modelo.entidad;

public enum CategoriaPlato {
    ENTRADA("Entrada"),
    PLATO_FUERTE("Plato fuerte"),
    BEBIDA("Bebida"),
    POSTRE("Postre");

    private final String etiqueta;

    CategoriaPlato(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
