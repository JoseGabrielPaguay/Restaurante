package co.edu.uniquindio.restaurante.modelo.entidad;

import java.io.Serializable;

/**
 * Linea de una orden. Guarda el id y el nombre del plato (no el objeto completo)
 * para que la persistencia en archivos no duplique platos. El precio unitario se
 * copia del plato al agregarlo (Regla 005).
 */
public class ItemPedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int idPlato;
    private String nombrePlato;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public ItemPedido(int id, Plato plato, int cantidad) {
        this.id = id;
        this.idPlato = plato.getId();
        this.nombrePlato = plato.getNombre();
        this.precioUnitario = plato.getPrecio();
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public double calcularSubtotal() {
        this.subtotal = cantidad * precioUnitario;
        return subtotal;
    }

    public int getId() { return id; }
    public int getIdPlato() { return idPlato; }
    public String getNombrePlato() { return nombrePlato; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}
