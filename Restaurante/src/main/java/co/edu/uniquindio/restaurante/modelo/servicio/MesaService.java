package co.edu.uniquindio.restaurante.modelo.servicio;

import co.edu.uniquindio.restaurante.modelo.entidad.EstadoMesa;
import co.edu.uniquindio.restaurante.modelo.entidad.Mesa;
import co.edu.uniquindio.restaurante.modelo.persistencia.MesaRepository;

import java.util.ArrayList;
import java.util.List;

public class MesaService {
    private final MesaRepository repo;

    public MesaService(MesaRepository repo) {
        this.repo = repo;
    }

    public List<Mesa> listar() {
        return repo.listar();
    }

    public List<Mesa> listarActivas() {
        List<Mesa> activas = new ArrayList<>();
        for (Mesa m : repo.listar()) {
            if (m.estaActiva()) {
                activas.add(m);
            }
        }
        return activas;
    }

    public void cambiarEstado(int idMesa, EstadoMesa estado) {
        Mesa mesa = repo.buscarPorId(idMesa);
        if (mesa != null) {
            mesa.setEstado(estado);
            repo.guardar(mesa);
        }
    }
}