package co.edu.uniquindio.restaurante.modelo.entidad;

import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import co.edu.uniquindio.restaurante.modelo.persistencia.GestorArchivos;
import co.edu.uniquindio.restaurante.modelo.servicio.ContextoApp;
import co.edu.uniquindio.restaurante.modelo.servicio.MesaService;
import co.edu.uniquindio.restaurante.modelo.servicio.PedidoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Casos Given-When-Then de la transacción "Realizar pedido" a nivel de servicio. */
class PedidoServiceTest {

    @TempDir
    Path carpeta;

    private PedidoService pedidos;
    private MesaService mesas;
    private Mesa mesaLibre;
    private Mesero mesero;
    private Plato bandeja;

    @BeforeEach
    void preparar() {
        ContextoApp contexto = new ContextoApp(new GestorArchivos(carpeta));
        pedidos = contexto.getPedidoService();
        mesas = contexto.getMesaService();
        mesaLibre = mesas.listarActivas().stream()
                .filter(m -> m.getEstado() == EstadoMesa.LIBRE).findFirst().orElseThrow();
        mesero = contexto.getMeseroService().listarActivos().get(0);
        bandeja = contexto.getPlatoService().listar().get(0); // Bandeja paisa, $28.000, disponible
    }

    @Test
    @DisplayName("GWT-S1: una orden completa se envía, ocupa la mesa y queda guardada (Reglas 001-006)")
    void ordenValidaSeEnvia() throws ReglaNegocioException {
        OrdenPedido orden = pedidos.crearBorrador();
        pedidos.seleccionarMesa(orden, mesaLibre);
        pedidos.seleccionarMesero(orden, mesero);
        pedidos.agregarItem(orden, bandeja, 2);
        int antes = pedidos.listarOrdenes().size();

        pedidos.enviarACocina(orden);

        assertEquals(EstadoOrden.ENVIADA_A_COCINA, orden.getEstado());
        assertNotNull(orden.getFechaHora());
        assertEquals(56000, orden.getTotal(), 0.001);
        assertEquals(EstadoMesa.OCUPADA, mesaLibre.getEstado());
        assertEquals(antes + 1, pedidos.listarOrdenes().size());
    }

    @Test
    @DisplayName("GWT-S2: una orden vacía reporta mesa, mesero y plato faltantes a la vez y no se guarda")
    void ordenVacia() {
        OrdenPedido orden = pedidos.crearBorrador();
        int antes = pedidos.listarOrdenes().size();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class, () -> pedidos.enviarACocina(orden));

        assertEquals("Seleccione una mesa activa", e.mensajeDe(ReglaNegocioException.CAMPO_MESA));
        assertEquals("Seleccione un mesero activo", e.mensajeDe(ReglaNegocioException.CAMPO_MESERO));
        assertEquals("Agregue al menos un plato", e.mensajeDe(ReglaNegocioException.CAMPO_ORDEN));
        assertEquals(antes, pedidos.listarOrdenes().size());
    }

    @Test
    @DisplayName("GWT-S3: una mesa fuera de servicio no se puede seleccionar (Regla 001)")
    void mesaFueraDeServicio() {
        Mesa danada = mesas.listar().stream()
                .filter(m -> m.getEstado() == EstadoMesa.FUERA_DE_SERVICIO).findFirst().orElseThrow();
        OrdenPedido orden = pedidos.crearBorrador();

        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> pedidos.seleccionarMesa(orden, danada));

        assertEquals("Seleccione una mesa activa", e.getMessage());
    }

    @Test
    @DisplayName("GWT-S4: una orden ya enviada no se puede reenviar ni modificar (Regla 007)")
    void ordenEnviadaBloqueada() throws ReglaNegocioException {
        OrdenPedido orden = pedidos.crearBorrador();
        pedidos.seleccionarMesa(orden, mesaLibre);
        pedidos.seleccionarMesero(orden, mesero);
        pedidos.agregarItem(orden, bandeja, 1);
        pedidos.enviarACocina(orden);

        ReglaNegocioException reenvio = assertThrows(ReglaNegocioException.class, () -> pedidos.enviarACocina(orden));
        ReglaNegocioException agregar = assertThrows(ReglaNegocioException.class,
                () -> pedidos.agregarItem(orden, bandeja, 1));

        assertEquals("La orden ya fue enviada a cocina", reenvio.getMessage());
        assertEquals("La orden ya fue enviada a cocina", agregar.getMessage());
    }
}