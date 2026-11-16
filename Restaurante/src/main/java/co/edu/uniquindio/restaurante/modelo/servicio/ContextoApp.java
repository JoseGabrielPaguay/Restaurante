package co.edu.uniquindio.restaurante.modelo.servicio;

import co.edu.uniquindio.restaurante.modelo.persistencia.DataLoader;
import co.edu.uniquindio.restaurante.modelo.persistencia.GestorArchivos;
import co.edu.uniquindio.restaurante.modelo.persistencia.MesaRepository;
import co.edu.uniquindio.restaurante.modelo.persistencia.MeseroRepository;
import co.edu.uniquindio.restaurante.modelo.persistencia.OrdenPedidoRepository;
import co.edu.uniquindio.restaurante.modelo.persistencia.PlatoRepository;

/** Crea una sola vez los repositorios y servicios; los controladores los piden aqui. */
public class ContextoApp {
    private static ContextoApp instancia;

    private final PedidoService pedidoService;
    private final PlatoService platoService;
    private final MesaService mesaService;
    private final MeseroService meseroService;

    public ContextoApp(GestorArchivos gestor) {
        MesaRepository mesas = new MesaRepository(gestor);
        MeseroRepository meseros = new MeseroRepository(gestor);
        PlatoRepository platos = new PlatoRepository(gestor);
        OrdenPedidoRepository ordenes = new OrdenPedidoRepository(gestor);
        DataLoader.cargarSiHaceFalta(mesas, meseros, platos, ordenes);
        this.mesaService = new MesaService(mesas);
        this.meseroService = new MeseroService(meseros);
        this.platoService = new PlatoService(platos, ordenes);
        this.pedidoService = new PedidoService(ordenes, mesas);
    }

    public static synchronized ContextoApp getInstancia() {
        if (instancia == null) {
            instancia = new ContextoApp(new GestorArchivos());
        }
        return instancia;
    }

    public PedidoService getPedidoService() { return pedidoService; }
    public PlatoService getPlatoService() { return platoService; }
    public MesaService getMesaService() { return mesaService; }
    public MeseroService getMeseroService() { return meseroService; }
}