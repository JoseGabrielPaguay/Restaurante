package co.edu.uniquindio.restaurante.modelo.servicio;

import co.edu.uniquindio.restaurante.modelo.entidad.CategoriaPlato;
import co.edu.uniquindio.restaurante.modelo.entidad.ItemPedido;
import co.edu.uniquindio.restaurante.modelo.entidad.OrdenPedido;
import co.edu.uniquindio.restaurante.modelo.entidad.Plato;
import co.edu.uniquindio.restaurante.modelo.excepciones.ErrorRegla;
import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import co.edu.uniquindio.restaurante.modelo.persistencia.OrdenPedidoRepository;
import co.edu.uniquindio.restaurante.modelo.persistencia.PlatoRepository;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class PlatoService {
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_CATEGORIA = "categoria";
    public static final String CAMPO_PRECIO = "precio";

    public enum ResultadoEliminacion { ELIMINADO, MARCADO_NO_DISPONIBLE }

    private final PlatoRepository platos;
    private final OrdenPedidoRepository ordenes;

    public PlatoService(PlatoRepository platos, OrdenPedidoRepository ordenes) {
        this.platos = platos;
        this.ordenes = ordenes;
    }

    public List<Plato> listar() {
        return platos.listar();
    }

    /** Busca por nombre o categoria, sin importar mayusculas ni tildes (REQ001, REQ013). */
    public List<Plato> buscar(String texto) {
        return buscar(texto, null);
    }

    public List<Plato> buscar(String texto, CategoriaPlato categoria) {
        String filtro = normalizar(texto);
        List<Plato> resultado = new ArrayList<>();
        for (Plato p : platos.listar()) {
            boolean coincideTexto = filtro.isEmpty()
                    || normalizar(p.getNombre()).contains(filtro)
                    || normalizar(p.getCategoria().toString()).contains(filtro);
            boolean coincideCategoria = categoria == null || p.getCategoria() == categoria;
            if (coincideTexto && coincideCategoria) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Plato crear(Plato plato) throws ReglaNegocioException {
        validar(plato);
        plato.setId(0);
        return platos.guardar(plato);
    }

    public Plato actualizar(Plato plato) throws ReglaNegocioException {
        validar(plato);
        return platos.guardar(plato);
    }

    /** Si el plato ya aparece en alguna orden, no se puede borrar (Regla 003, REQ015). */
    public boolean estaEnUso(int idPlato) {
        for (OrdenPedido orden : ordenes.listar()) {
            for (ItemPedido item : orden.getItems()) {
                if (item.getIdPlato() == idPlato) {
                    return true;
                }
            }
        }
        return false;
    }

    public ResultadoEliminacion eliminar(int idPlato) {
        Plato plato = platos.buscarPorId(idPlato);
        if (plato == null) {
            throw new IllegalArgumentException("El plato no existe");
        }
        if (estaEnUso(idPlato)) {
            plato.setDisponible(false);
            platos.guardar(plato);
            return ResultadoEliminacion.MARCADO_NO_DISPONIBLE;
        }
        platos.eliminar(idPlato);
        return ResultadoEliminacion.ELIMINADO;
    }

    /** REQ016: nombre obligatorio y unico, categoria obligatoria y precio mayor que 0. */
    private void validar(Plato plato) throws ReglaNegocioException {
        List<ErrorRegla> errores = new ArrayList<>();
        String nombre = plato.getNombre() == null ? "" : plato.getNombre().trim();
        if (nombre.isEmpty()) {
            errores.add(new ErrorRegla("REQ016", CAMPO_NOMBRE, "Ingrese el nombre del plato"));
        } else if (nombreRepetido(nombre, plato.getId())) {
            errores.add(new ErrorRegla("REQ016", CAMPO_NOMBRE, "Ya existe un plato con ese nombre"));
        }
        if (plato.getCategoria() == null) {
            errores.add(new ErrorRegla("REQ016", CAMPO_CATEGORIA, "Seleccione una categoría"));
        }
        if (plato.getPrecio() <= 0) {
            errores.add(new ErrorRegla("REQ016", CAMPO_PRECIO, "El precio debe ser mayor que 0"));
        }
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(errores);
        }
    }

    private boolean nombreRepetido(String nombre, int idPropio) {
        for (Plato p : platos.listar()) {
            if (p.getId() != idPropio && normalizar(p.getNombre()).equals(normalizar(nombre))) {
                return true;
            }
        }
        return false;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinTildes.toLowerCase();
    }
}