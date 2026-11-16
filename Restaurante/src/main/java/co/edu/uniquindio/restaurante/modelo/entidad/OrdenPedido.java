package co.edu.uniquindio.restaurante.modelo.entidad;

import co.edu.uniquindio.restaurante.modelo.excepciones.ErrorRegla;
import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException.*;

public class OrdenPedido implements Identificable  {
    private static final long serialVersionUID = 1L;

    public static final int CANTIDAD_MINIMA = 1;
    public static final int CANTIDAD_MAXIMA = 20;

    private int id;
    private LocalDateTime fechaHora;
    private EstadoOrden estado;
    private String nombreCliente;
    private String observaciones;
    private double total;
    private int idMesa;
    private int idMesero;
    private final List<ItemPedido> items;

    public OrdenPedido() {
        this.estado = EstadoOrden.BORRADOR;
        this.items = new ArrayList<>();
    }

    /** Regla 001: la orden necesita una mesa activa. */
    public void asignarMesa(Mesa mesa) throws ReglaNegocioException {
        exigirBorrador();
        if (mesa == null || !mesa.estaActiva()) {
            throw new ReglaNegocioException("Regla 001", CAMPO_MESA, "Seleccione una mesa activa");
        }
        this.idMesa = mesa.getId();
    }

    /** Regla 002: la orden necesita un mesero activo. */
    public void asignarMesero(Mesero mesero) throws ReglaNegocioException {
        exigirBorrador();
        if (mesero == null || !mesero.estaActivo()) {
            throw new ReglaNegocioException("Regla 002", CAMPO_MESERO, "Seleccione un mesero activo");
        }
        this.idMesero = mesero.getId();
    }

    /**
     * Reglas 003, 004, 005 y 007: solo platos disponibles, cantidad de 1 a 20,
     * precio copiado del plato y orden todavia en borrador.
     * Si el plato ya esta en la orden, se suma la cantidad.
     */
    public ItemPedido agregarItem(Plato plato, int cantidad) throws ReglaNegocioException {
        exigirBorrador();
        if (plato == null || !plato.estaDisponible()) {
            throw new ReglaNegocioException("Regla 003", CAMPO_PLATO, "El plato no está disponible");
        }
        ItemPedido existente = buscarItemDePlato(plato.getId());
        int cantidadFinal = cantidad + (existente != null ? existente.getCantidad() : 0);
        if (cantidad < CANTIDAD_MINIMA || cantidadFinal > CANTIDAD_MAXIMA) {
            throw new ReglaNegocioException("Regla 004", CAMPO_CANTIDAD,
                    "Cantidad inválida: use un número de 1 a 20");
        }
        ItemPedido item;
        if (existente != null) {
            existente.setCantidad(cantidadFinal);
            item = existente;
        } else {
            item = new ItemPedido(siguienteIdItem(), plato, cantidad);
            items.add(item);
        }
        calcularTotal();
        return item;
    }

    /** Regla 007: no se puede modificar una orden ya enviada. */
    public void quitarItem(ItemPedido item) throws ReglaNegocioException {
        exigirBorrador();
        items.remove(item);
        calcularTotal();
    }

    /** Regla 005: el total es la suma de los subtotales; no se edita a mano. */
    public double calcularTotal() {
        double suma = 0;
        for (ItemPedido item : items) {
            suma += item.calcularSubtotal();
        }
        this.total = suma;
        return total;
    }

    /** Reglas 001, 002 y 004 antes de enviar a cocina. Reporta todos los errores juntos. */
    public void validarReglas() throws ReglaNegocioException {
        List<ErrorRegla> errores = new ArrayList<>();
        if (idMesa <= 0) {
            errores.add(new ErrorRegla("Regla 001", CAMPO_MESA, "Seleccione una mesa activa"));
        }
        if (idMesero <= 0) {
            errores.add(new ErrorRegla("Regla 002", CAMPO_MESERO, "Seleccione un mesero activo"));
        }
        if (items.isEmpty()) {
            errores.add(new ErrorRegla("Regla 004", CAMPO_ORDEN, "Agregue al menos un plato"));
        }
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(errores);
        }
    }

    /** Regla 006: fecha y hora automaticas al enviar (el mesero ya esta en idMesero). */
    public void registrarAuditoria() {
        this.fechaHora = LocalDateTime.now();
    }

    /** Regla 007: despues de enviada, la orden queda bloqueada. */
    public void enviarACocina() throws ReglaNegocioException {
        exigirBorrador();
        this.estado = EstadoOrden.ENVIADA_A_COCINA;
    }

    public boolean estaEnviada() {
        return estado == EstadoOrden.ENVIADA_A_COCINA;
    }

    private void exigirBorrador() throws ReglaNegocioException {
        if (estado != EstadoOrden.BORRADOR) {
            throw new ReglaNegocioException("Regla 007", CAMPO_ORDEN, "La orden ya fue enviada a cocina");
        }
    }

    private ItemPedido buscarItemDePlato(int idPlato) {
        for (ItemPedido item : items) {
            if (item.getIdPlato() == idPlato) {
                return item;
            }
        }
        return null;
    }

    private int siguienteIdItem() {
        int mayor = 0;
        for (ItemPedido item : items) {
            mayor = Math.max(mayor, item.getId());
        }
        return mayor + 1;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoOrden getEstado() { return estado; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public double getTotal() { return total; }
    public int getIdMesa() { return idMesa; }
    public int getIdMesero() { return idMesero; }
    public List<ItemPedido> getItems() { return Collections.unmodifiableList(items); }
}
