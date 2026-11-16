package co.edu.uniquindio.restaurante.modelo.persistencia;

import co.edu.uniquindio.restaurante.modelo.entidad.Identificable;

import java.util.ArrayList;
import java.util.List;

public abstract class RepositorioArchivo<T extends Identificable> {
    private final GestorArchivos gestor;
    private final String archivo;
    private final List<T> datos;

    protected RepositorioArchivo(GestorArchivos gestor, String archivo) {
        this.gestor = gestor;
        this.archivo = archivo;
        this.datos = new ArrayList<>(gestor.<T>leer(archivo));
    }

    public List<T> listar() {
        return new ArrayList<>(datos);
    }

    public T buscarPorId(int id) {
        for (T e : datos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public int siguienteId() {
        int mayor = 0;
        for (T e : datos) {
            mayor = Math.max(mayor, e.getId());
        }
        return mayor + 1;
    }

    public T guardar(T entidad) {
        if (entidad.getId() <= 0) {
            entidad.setId(siguienteId());
        }
        int pos = -1;
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId() == entidad.getId()) {
                pos = i;
            }
        }
        if (pos >= 0) {
            datos.set(pos, entidad);
        } else {
            datos.add(entidad);
        }
        gestor.escribir(archivo, datos);
        return entidad;
    }

    public boolean eliminar(int id) {
        boolean borrado = datos.removeIf(e -> e.getId() == id);
        if (borrado) {
            gestor.escribir(archivo, datos);
        }
        return borrado;
    }
}