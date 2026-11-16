package co.edu.uniquindio.restaurante.modelo.persistencia;

import co.edu.uniquindio.restaurante.modelo.entidad.Mesa;

public class MesaRepository extends RepositorioArchivo<Mesa> {
    public MesaRepository(GestorArchivos gestor) {
        super(gestor, "mesas.dat");
    }
}