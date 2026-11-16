package co.edu.uniquindio.restaurante.modelo.entidad;

import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Casos de aceptacion Given-When-Then sobre las reglas de la orden. */
class OrdenPedidoTest {

    private final Mesa mesa = new Mesa(1, 5, 4, EstadoMesa.LIBRE);
    private final Mesero mesero = new Mesero(1, "Ana Torres", "1001", "Noche", true);
    private final Plato bandeja = new Plato(1, "Bandeja paisa", CategoriaPlato.PLATO_FUERTE, 28000, true);
    private final Plato limonada = new Plato(2, "Limonada de coco", CategoriaPlato.BEBIDA, 8000, true);
    private final Plato trucha = new Plato(3, "Trucha al ajillo", CategoriaPlato.PLATO_FUERTE, 32000, false);

    @Test
    @DisplayName("GWT-P1: una orden completa se envia a cocina con auditoria y total correcto")
    void ordenCompletaSeEnvia() throws ReglaNegocioException {
        OrdenPedido orden = new OrdenPedido();
        orden.asignarMesa(mesa);
        orden.asignarMesero(mesero);
        orden.agregarItem(bandeja, 2);
        orden.agregarItem(limonada, 2);

        orden.validarReglas();
        orden.registrarAuditoria();
        orden.enviarACocina();
        mesa.ocupar();

        assertEquals(EstadoOrden.ENVIADA_A_COCINA, orden.getEstado());
        assertNotNull(orden.getFechaHora());
        assertEquals(72000, orden.getTotal(), 0.001);
        assertEquals(EstadoMesa.OCUPADA, mesa.getEstado());
    }

    @Test
    @DisplayName("GWT-P2: sin mesa ni mesero ni platos se reportan los tres errores (Reglas 001, 002, 004)")
    void faltanDatosObligatorios() {
        OrdenPedido orden = new OrdenPedido();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, orden::validarReglas);

        assertEquals("Seleccione una mesa activa", e.mensajeDe(ReglaNegocioException.CAMPO_MESA));
        assertEquals("Seleccione un mesero activo", e.mensajeDe(ReglaNegocioException.CAMPO_MESERO));
        assertEquals("Agregue al menos un plato", e.mensajeDe(ReglaNegocioException.CAMPO_ORDEN));
    }

    @Test
    @DisplayName("GWT-P3: una mesa fuera de servicio no se puede asignar (Regla 001)")
    void mesaFueraDeServicio() {
        Mesa danada = new Mesa(2, 6, 4, EstadoMesa.FUERA_DE_SERVICIO);
        OrdenPedido orden = new OrdenPedido();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> orden.asignarMesa(danada));

        assertEquals("Seleccione una mesa activa", e.getMessage());
    }

    @Test
    @DisplayName("GWT-P4: un plato no disponible no se agrega (Regla 003)")
    void platoNoDisponible() {
        OrdenPedido orden = new OrdenPedido();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> orden.agregarItem(trucha, 1));

        assertEquals("El plato no está disponible", e.getMessage());
        assertTrue(orden.getItems().isEmpty());
    }

    @Test
    @DisplayName("GWT-P5: una cantidad fuera de 1 a 20 se rechaza (Regla 004)")
    void cantidadInvalida() {
        OrdenPedido orden = new OrdenPedido();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> orden.agregarItem(bandeja, 25));
        ReglaNegocioException e0 = assertThrows(ReglaNegocioException.class, () -> orden.agregarItem(bandeja, 0));

        assertEquals("Cantidad inválida: use un número de 1 a 20", e.getMessage());
        assertEquals("Cantidad inválida: use un número de 1 a 20", e0.getMessage());
    }

    @Test
    @DisplayName("GWT-P6: el total lo calcula el sistema y se actualiza al quitar un plato (Regla 005)")
    void totalSeCalculaSolo() throws ReglaNegocioException {
        OrdenPedido orden = new OrdenPedido();
        ItemPedido item = orden.agregarItem(bandeja, 2);
        orden.agregarItem(limonada, 1);
        assertEquals(64000, orden.getTotal(), 0.001);

        orden.quitarItem(item);

        assertEquals(8000, orden.getTotal(), 0.001);
    }

    @Test
    @DisplayName("GWT-P7: el precio se copia del plato y no cambia si el plato cambia de precio (Regla 005)")
    void precioCopiado() throws ReglaNegocioException {
        OrdenPedido orden = new OrdenPedido();
        orden.agregarItem(bandeja, 1);

        bandeja.setPrecio(50000);

        assertEquals(28000, orden.getItems().get(0).getPrecioUnitario(), 0.001);
    }

    @Test
    @DisplayName("GWT-P8: una orden enviada a cocina no se puede modificar (Regla 007)")
    void ordenEnviadaBloqueada() throws ReglaNegocioException {
        OrdenPedido orden = new OrdenPedido();
        orden.asignarMesa(mesa);
        orden.asignarMesero(mesero);
        orden.agregarItem(bandeja, 1);
        orden.enviarACocina();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> orden.agregarItem(limonada, 1));

        assertEquals("La orden ya fue enviada a cocina", e.getMessage());
        assertEquals(1, orden.getItems().size());
    }

    @Test
    @DisplayName("GWT-P9: el mismo plato agregado dos veces suma la cantidad en una sola linea")
    void mismoPlatoSumaCantidad() throws ReglaNegocioException {
        OrdenPedido orden = new OrdenPedido();
        orden.agregarItem(bandeja, 2);
        orden.agregarItem(bandeja, 3);

        assertEquals(1, orden.getItems().size());
        assertEquals(5, orden.getItems().get(0).getCantidad());
    }
}
