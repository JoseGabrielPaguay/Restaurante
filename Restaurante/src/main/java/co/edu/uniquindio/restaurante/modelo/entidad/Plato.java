package co.edu.uniquindio.restaurante.modelo.entidad;



public class Plato implements Identificable  {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private CategoriaPlato categoria;
    private double precio;
    private boolean disponible;

    public Plato(int id, String nombre, CategoriaPlato categoria, double precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.disponible = disponible;
    }

    /** Solo los platos disponibles se pueden agregar a una orden (Regla 003). */
    public boolean estaDisponible() {
        return disponible;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public CategoriaPlato getCategoria() { return categoria; }
    public void setCategoria(CategoriaPlato categoria) { this.categoria = categoria; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return nombre;
    }
}
