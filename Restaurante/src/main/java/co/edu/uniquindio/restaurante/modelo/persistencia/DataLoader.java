package co.edu.uniquindio.restaurante.modelo.persistencia;

import co.edu.uniquindio.restaurante.modelo.entidad.*;
import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;

public class DataLoader {

    public static void cargarSiHaceFalta(MesaRepository mesas, MeseroRepository meseros,
                                         PlatoRepository platos, OrdenPedidoRepository ordenes) {
        if (mesas.listar().isEmpty()) {
            cargarMesas(mesas);
        }
        if (meseros.listar().isEmpty()) {
            cargarMeseros(meseros);
        }
        if (platos.listar().isEmpty()) {
            cargarPlatos(platos);
        }
        if (ordenes.listar().isEmpty()) {
            cargarOrdenes(ordenes, mesas, meseros, platos);
        }
    }

    private static void cargarMesas(MesaRepository repo) {
        int[] capacidades = {2, 4, 4, 6, 2, 4, 6, 8, 4, 2, 4, 6};
        for (int i = 1; i <= 12; i++) {
            EstadoMesa estado = EstadoMesa.LIBRE;
            if (i == 9 || i == 10) {
                estado = EstadoMesa.OCUPADA;
            } else if (i >= 11) {
                estado = EstadoMesa.FUERA_DE_SERVICIO;
            }
            repo.guardar(new Mesa(0, i, capacidades[i - 1], estado));
        }
    }

    private static void cargarMeseros(MeseroRepository repo) {
        repo.guardar(new Mesero(0, "Ana Torres", "1001230001", "Noche", true));
        repo.guardar(new Mesero(0, "Luis Ortega", "1001230002", "Mañana", true));
        repo.guardar(new Mesero(0, "Camila Rojas", "1001230003", "Tarde", true));
        repo.guardar(new Mesero(0, "Mateo Salazar", "1001230004", "Mañana", true));
        repo.guardar(new Mesero(0, "Valentina Cruz", "1001230005", "Tarde", true));
        repo.guardar(new Mesero(0, "Jorge Pineda", "1001230006", "Noche", false));
    }

    private static void cargarPlatos(PlatoRepository repo) {
        repo.guardar(new Plato(0, "Bandeja paisa", CategoriaPlato.PLATO_FUERTE, 28000, true));
        repo.guardar(new Plato(0, "Sopa de lentejas", CategoriaPlato.ENTRADA, 12000, true));
        repo.guardar(new Plato(0, "Trucha al ajillo", CategoriaPlato.PLATO_FUERTE, 32000, false));
        repo.guardar(new Plato(0, "Limonada de coco", CategoriaPlato.BEBIDA, 8000, true));
        repo.guardar(new Plato(0, "Tres leches", CategoriaPlato.POSTRE, 9500, true));
        repo.guardar(new Plato(0, "Arroz con pollo", CategoriaPlato.PLATO_FUERTE, 22000, true));
        repo.guardar(new Plato(0, "Jugo de mora", CategoriaPlato.BEBIDA, 7000, true));
        repo.guardar(new Plato(0, "Ensalada de la casa", CategoriaPlato.ENTRADA, 16000, true));
        repo.guardar(new Plato(0, "Ajiaco", CategoriaPlato.PLATO_FUERTE, 26000, true));
        repo.guardar(new Plato(0, "Sancocho de gallina", CategoriaPlato.PLATO_FUERTE, 25000, true));
        repo.guardar(new Plato(0, "Churrasco", CategoriaPlato.PLATO_FUERTE, 34000, true));
        repo.guardar(new Plato(0, "Pechuga a la plancha", CategoriaPlato.PLATO_FUERTE, 24000, true));
        repo.guardar(new Plato(0, "Mondongo", CategoriaPlato.PLATO_FUERTE, 23000, true));
        repo.guardar(new Plato(0, "Empanadas", CategoriaPlato.ENTRADA, 7000, true));
        repo.guardar(new Plato(0, "Patacones con hogao", CategoriaPlato.ENTRADA, 9000, true));
        repo.guardar(new Plato(0, "Limonada natural", CategoriaPlato.BEBIDA, 6000, true));
        repo.guardar(new Plato(0, "Café", CategoriaPlato.BEBIDA, 4500, true));
        repo.guardar(new Plato(0, "Gaseosa", CategoriaPlato.BEBIDA, 5000, true));
        repo.guardar(new Plato(0, "Brownie con helado", CategoriaPlato.POSTRE, 11000, true));
        repo.guardar(new Plato(0, "Flan de caramelo", CategoriaPlato.POSTRE, 8500, false));
    }

    private static void cargarOrdenes(OrdenPedidoRepository repo, MesaRepository mesas,
                                      MeseroRepository meseros, PlatoRepository platos) {
        try {
            crearOrden(repo, mesas.buscarPorId(9), meseros.buscarPorId(1), "Familia Rojas",
                    platos.buscarPorId(1), 2, platos.buscarPorId(4), 2);
            crearOrden(repo, mesas.buscarPorId(10), meseros.buscarPorId(2), "Grupo de oficina",
                    platos.buscarPorId(9), 3, platos.buscarPorId(7), 3);
            crearOrden(repo, mesas.buscarPorId(9), meseros.buscarPorId(3), null,
                    platos.buscarPorId(6), 1, platos.buscarPorId(5), 1);
        } catch (ReglaNegocioException e) {
            throw new IllegalStateException("Datos de prueba invalidos: " + e.getMessage(), e);
        }
    }

    private static void crearOrden(OrdenPedidoRepository repo, Mesa mesa, Mesero mesero, String cliente,
                                   Plato p1, int c1, Plato p2, int c2) throws ReglaNegocioException {
        OrdenPedido orden = new OrdenPedido();
        orden.asignarMesa(mesa);
        orden.asignarMesero(mesero);
        orden.setNombreCliente(cliente);
        orden.agregarItem(p1, c1);
        orden.agregarItem(p2, c2);
        orden.registrarAuditoria();
        orden.enviarACocina();
        repo.guardar(orden);
    }
}