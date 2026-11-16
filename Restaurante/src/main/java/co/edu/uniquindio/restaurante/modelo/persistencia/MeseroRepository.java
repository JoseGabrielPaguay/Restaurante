package co.edu.uniquindio.restaurante.modelo.persistencia;

import co.edu.uniquindio.restaurante.modelo.entidad.Mesero;

public class MeseroRepository extends RepositorioArchivo<Mesero> {
    public MeseroRepository(GestorArchivos gestor) {
        super(gestor, "meseros.dat");
    }
}