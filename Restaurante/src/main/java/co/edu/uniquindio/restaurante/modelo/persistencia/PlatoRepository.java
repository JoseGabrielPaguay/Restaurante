package co.edu.uniquindio.restaurante.modelo.persistencia;

import co.edu.uniquindio.restaurante.modelo.entidad.Plato;

public class PlatoRepository extends RepositorioArchivo<Plato> {
    public PlatoRepository(GestorArchivos gestor) {
        super(gestor, "platos.dat");
    }
}