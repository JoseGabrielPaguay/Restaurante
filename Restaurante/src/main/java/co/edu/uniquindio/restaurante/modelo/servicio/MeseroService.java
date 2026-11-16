package co.edu.uniquindio.restaurante.modelo.servicio;

import co.edu.uniquindio.restaurante.modelo.entidad.Mesero;
import co.edu.uniquindio.restaurante.modelo.persistencia.MeseroRepository;

import java.util.ArrayList;
import java.util.List;

public class MeseroService {
    private final MeseroRepository repo;

    public MeseroService(MeseroRepository repo) {
        this.repo = repo;
    }

    public List<Mesero> listar() {
        return repo.listar();
    }

    public List<Mesero> listarActivos() {
        List<Mesero> activos = new ArrayList<>();
        for (Mesero m : repo.listar()) {
            if (m.estaActivo()) {
                activos.add(m);
            }
        }
        return activos;
    }
}