package co.edu.uniquindio.restaurante.modelo.entidad;


import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import co.edu.uniquindio.restaurante.modelo.persistencia.GestorArchivos;
import co.edu.uniquindio.restaurante.modelo.servicio.ContextoApp;
import co.edu.uniquindio.restaurante.modelo.servicio.PlatoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Casos Given-When-Then del CRUD de Plato (REQ013 a REQ016), con datos de prueba en carpeta temporal. */
class PlatoServiceTest {

    @TempDir
    Path carpeta;

    private PlatoService servicio;

    @BeforeEach
    void preparar() {
        servicio = new ContextoApp(new GestorArchivos(carpeta)).getPlatoService();
    }

    private Plato buscarPorId(int id) {
        return servicio.listar().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Test
    @DisplayName("GWT-C1: un plato válido se crea con id automático (REQ014)")
    void crearPlatoValido() throws ReglaNegocioException {
        int antes = servicio.listar().size();

        Plato creado = servicio.crear(new Plato(0, "Lasaña", CategoriaPlato.PLATO_FUERTE, 21000, true));

        assertTrue(creado.getId() > 0);
        assertEquals(antes + 1, servicio.listar().size());
    }

    @Test
    @DisplayName("GWT-C2: un nombre repetido (sin importar mayúsculas ni tildes) se rechaza (REQ016)")
    void nombreDuplicado() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.crear(new Plato(0, "bandeja PAISA", CategoriaPlato.PLATO_FUERTE, 20000, true)));

        assertEquals("Ya existe un plato con ese nombre", e.mensajeDe(PlatoService.CAMPO_NOMBRE));
    }

    @Test
    @DisplayName("GWT-C3: sin categoría y con precio 0 se reportan ambos errores a la vez (REQ016)")
    void categoriaYPrecioInvalidos() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.crear(new Plato(0, "Nuevo plato", null, 0, true)));

        assertEquals("Seleccione una categoría", e.mensajeDe(PlatoService.CAMPO_CATEGORIA));
        assertEquals("El precio debe ser mayor que 0", e.mensajeDe(PlatoService.CAMPO_PRECIO));
        assertNull(e.mensajeDe(PlatoService.CAMPO_NOMBRE));
    }

    @Test
    @DisplayName("GWT-C4: un nombre vacío se rechaza (REQ016)")
    void nombreVacio() {
        ReglaNegocioException e = assertThrows(ReglaNegocioException.class,
                () -> servicio.crear(new Plato(0, "   ", CategoriaPlato.BEBIDA, 5000, true)));

        assertEquals("Ingrese el nombre del plato", e.mensajeDe(PlatoService.CAMPO_NOMBRE));
    }

    @Test
    @DisplayName("GWT-C5: actualizar un plato cambia sus datos y lo puede conservar con su mismo nombre (REQ014)")
    void actualizarPlato() throws ReglaNegocioException {
        servicio.actualizar(new Plato(2, "Sopa de lentejas", CategoriaPlato.ENTRADA, 13000, true));

        assertEquals(13000, buscarPorId(2).getPrecio(), 0.001);
    }

    @Test
    @DisplayName("GWT-C6: eliminar un plato que no está en ninguna orden lo borra (REQ015)")
    void eliminarSinUso() throws ReglaNegocioException {
        Plato temporal = servicio.crear(new Plato(0, "Temporal", CategoriaPlato.POSTRE, 3000, true));

        PlatoService.ResultadoEliminacion resultado = servicio.eliminar(temporal.getId());

        assertEquals(PlatoService.ResultadoEliminacion.ELIMINADO, resultado);
        assertNull(buscarPorId(temporal.getId()));
    }

    @Test
    @DisplayName("GWT-C7: eliminar un plato que ya está en una orden lo marca como no disponible (Regla 003, REQ015)")
    void eliminarEnUso() {
        assertTrue(servicio.estaEnUso(1)); // Bandeja paisa está en la primera orden de prueba

        PlatoService.ResultadoEliminacion resultado = servicio.eliminar(1);

        assertEquals(PlatoService.ResultadoEliminacion.MARCADO_NO_DISPONIBLE, resultado);
        assertNotNull(buscarPorId(1));
        assertFalse(buscarPorId(1).isDisponible());
    }

    @Test
    @DisplayName("GWT-C8: la búsqueda ignora tildes y mayúsculas, y filtra por categoría (REQ013)")
    void buscar() {
        List<Plato> cafe = servicio.buscar("CAFE");
        List<Plato> bebidas = servicio.buscar("bebida");
        List<Plato> postres = servicio.buscar("", CategoriaPlato.POSTRE);

        assertTrue(cafe.stream().anyMatch(p -> p.getNombre().equals("Café")));
        assertFalse(bebidas.isEmpty());
        assertTrue(bebidas.stream().allMatch(p -> p.getCategoria() == CategoriaPlato.BEBIDA));
        assertTrue(postres.stream().allMatch(p -> p.getCategoria() == CategoriaPlato.POSTRE));
    }

    @Test
    @DisplayName("GWT-C9: los datos de prueba cumplen el mínimo del RNF-05 (20 platos)")
    void datosDePrueba() {
        assertTrue(servicio.listar().size() >= 20);
    }
}