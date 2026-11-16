package co.edu.uniquindio.restaurante.modelo.servicio;

import co.edu.uniquindio.restaurante.modelo.entidad.ItemPedido;
import co.edu.uniquindio.restaurante.modelo.entidad.Mesa;
import co.edu.uniquindio.restaurante.modelo.entidad.Mesero;
import co.edu.uniquindio.restaurante.modelo.entidad.OrdenPedido;
import co.edu.uniquindio.restaurante.modelo.entidad.Plato;
import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import co.edu.uniquindio.restaurante.modelo.persistencia.MesaRepository;
import co.edu.uniquindio.restaurante.modelo.persistencia.OrdenPedidoRepository;

import java.util.List;

public class PedidoService {
    private final OrdenPedidoRepository ordenes;
    private final MesaRepository mesas;

    public PedidoService(OrdenPedidoRepository ordenes, MesaRepository mesas) {
        this.ordenes = ordenes;
        this.mesas = mesas;
    }

    public OrdenPedido crearBorrador() {
        return new OrdenPedido();
    }

    /** Numero que tendra la proxima orden (se muestra antes de guardarla). */
    public int siguienteNumeroPedido() {
        return ordenes.siguienteId();
    }

    public void seleccionarMesa(OrdenPedido orden, Mesa mesa) throws ReglaNegocioException {
        orden.asignarMesa(mesa);
    }

    public void seleccionarMesero(OrdenPedido orden, Mesero mesero) throws ReglaNegocioException {
        orden.asignarMesero(mesero);
    }

    public ItemPedido agregarItem(OrdenPedido orden, Plato plato, int cantidad) throws ReglaNegocioException {
        return orden.agregarItem(plato, cantidad);
    }

    public void quitarItem(OrdenPedido orden, ItemPedido item) throws ReglaNegocioException {
        orden.quitarItem(item);
    }

    public double calcularTotal(OrdenPedido orden) {
        return orden.calcularTotal();
    }

    /** Valida las reglas, registra la auditoria, ocupa la mesa y guarda la orden (REQ008 a REQ010). */
    public OrdenPedido enviarACocina(OrdenPedido orden) throws ReglaNegocioException {
        if (orden.estaEnviada()) {
            throw new ReglaNegocioException("Regla 007", ReglaNegocioException.CAMPO_ORDEN,
                    "La orden ya fue enviada a cocina");
        }
        orden.validarReglas();
        Mesa mesa = mesas.buscarPorId(orden.getIdMesa());
        if (mesa == null || !mesa.estaActiva()) {
            throw new ReglaNegocioException("Regla 001", ReglaNegocioException.CAMPO_MESA,
                    "Seleccione una mesa activa");
        }
        orden.registrarAuditoria();
        orden.enviarACocina();
        mesa.ocupar();
        mesas.guardar(mesa);
        return ordenes.guardar(orden);
    }

    public List<OrdenPedido> listarOrdenes() {
        return ordenes.listar();
    }
}