package co.edu.uniquindio.restaurante.modelo.persistencia;

import co.edu.uniquindio.restaurante.modelo.entidad.Mesa;
import co.edu.uniquindio.restaurante.modelo.entidad.OrdenPedido;

public class OrdenPedidoRepository extends RepositorioArchivo<OrdenPedido> {
    public OrdenPedidoRepository(GestorArchivos gestor) {
        super(gestor, "ordenes.dat");
    }
}