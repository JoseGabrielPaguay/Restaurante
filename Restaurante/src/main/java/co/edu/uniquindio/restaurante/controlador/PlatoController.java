package co.edu.uniquindio.restaurante.controlador;

import co.edu.uniquindio.restaurante.modelo.entidad.CategoriaPlato;
import co.edu.uniquindio.restaurante.modelo.entidad.Plato;
import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import co.edu.uniquindio.restaurante.modelo.servicio.ContextoApp;
import co.edu.uniquindio.restaurante.modelo.servicio.PlatoService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Optional;

/** Pantalla 2: CRUD de Plato (REQ013 a REQ016). Sin reglas de negocio: todo lo valida PlatoService. */
public class PlatoController {
    private static final int POR_PAGINA = 8;
    private static final String CLASE_ERROR = "campo-error";

    private final PlatoService servicio = ContextoApp.getInstancia().getPlatoService();
    private final DecimalFormat formatoPrecio = crearFormatoPrecio();

    private List<Plato> filtrados = List.of();
    private int pagina = 0;
    private Plato enEdicion;   // null = modo crear

    @FXML private TextField txtBuscar;
    @FXML private TableView<Plato> tblPlatos;
    @FXML private TableColumn<Plato, Integer> colId;
    @FXML private TableColumn<Plato, String> colNombre;
    @FXML private TableColumn<Plato, String> colCategoria;
    @FXML private TableColumn<Plato, String> colPrecio;
    @FXML private TableColumn<Plato, Boolean> colEstado;
    @FXML private Label lblSeleccion;
    @FXML private Label lblPaginacion;
    @FXML private Label lblMensaje;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnGuardar;

    @FXML private Label lblTituloForm;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<CategoriaPlato> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private CheckBox chkDisponible;
    @FXML private Label lblErrorNombre;
    @FXML private Label lblErrorCategoria;
    @FXML private Label lblErrorPrecio;

    @FXML
    private void initialize() {
        configurarTabla();
        cmbCategoria.getItems().setAll(CategoriaPlato.values());

        for (Label l : List.of(lblMensaje, lblErrorNombre, lblErrorCategoria, lblErrorPrecio)) {
            l.visibleProperty().bind(l.textProperty().isNotEmpty());
            l.managedProperty().bind(l.textProperty().isNotEmpty());
        }

        txtBuscar.textProperty().addListener((obs, antes, ahora) -> onBuscar());
        tblPlatos.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> actualizarSeleccion());

        modoCrear();
        recargar();
    }

    // ---------- Tabla y paginación (REQ013) ----------

    private void configurarTabla() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().toString()));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(formatoPrecio.format(c.getValue().getPrecio())));
        colEstado.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isDisponible()));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);
                if (empty || disponible == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label pill = new Label(disponible ? "Disponible" : "No disponible");
                pill.getStyleClass().addAll("pill", disponible ? "pill-disponible" : "pill-no-disponible");
                setText(null);
                setGraphic(pill);
            }
        });
        tblPlatos.setPlaceholder(new Label("No hay platos que coincidan con la búsqueda."));
    }

    @FXML
    private void onBuscar() {
        pagina = 0;
        recargar();
    }

    @FXML
    private void onAnterior() {
        pagina--;
        recargar();
    }

    @FXML
    private void onSiguiente() {
        pagina++;
        recargar();
    }

    private void recargar() {
        filtrados = servicio.buscar(txtBuscar.getText());
        int total = filtrados.size();
        int ultimaPagina = Math.max(0, (total - 1) / POR_PAGINA);
        pagina = Math.max(0, Math.min(pagina, ultimaPagina));

        int desde = pagina * POR_PAGINA;
        int hasta = Math.min(total, desde + POR_PAGINA);
        tblPlatos.getItems().setAll(filtrados.subList(desde, hasta));
        tblPlatos.refresh();

        lblPaginacion.setText(total == 0 ? "" : "Mostrando " + (hasta - desde) + " de " + total + " platos");
        btnAnterior.setDisable(pagina == 0);
        btnSiguiente.setDisable(pagina >= ultimaPagina);
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        boolean hay = tblPlatos.getSelectionModel().getSelectedItem() != null;
        btnEditar.setDisable(!hay);
        btnEliminar.setDisable(!hay);
        lblSeleccion.setText(hay ? "1 plato seleccionado" : "Seleccione un plato para gestionar");
    }

    // ---------- Crear / editar (REQ014, REQ016) ----------

    @FXML
    private void onNuevo() {
        lblMensaje.setText("");
        modoCrear();
    }

    @FXML
    private void onCancelar() {
        modoCrear();
    }

    private void modoCrear() {
        enEdicion = null;
        lblTituloForm.setText("Nuevo plato");
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        chkDisponible.setSelected(true);
        limpiarErrores();
        tblPlatos.getSelectionModel().clearSelection();
    }

    @FXML
    private void onEditar() {
        Plato plato = tblPlatos.getSelectionModel().getSelectedItem();
        if (plato == null) {
            return;
        }
        lblMensaje.setText("");
        enEdicion = plato;
        lblTituloForm.setText("Editar plato");
        txtNombre.setText(plato.getNombre());
        cmbCategoria.setValue(plato.getCategoria());
        txtPrecio.setText(plato.getPrecio() == Math.rint(plato.getPrecio())
                ? String.valueOf((long) plato.getPrecio())
                : String.valueOf(plato.getPrecio()));
        chkDisponible.setSelected(plato.isDisponible());
        limpiarErrores();
    }

    @FXML
    private void onGuardar() {
        limpiarErrores();
        lblMensaje.setText("");

        // Objeto NUEVO (mismo id al editar) para no mutar el de la tabla si la validación falla.
        Plato candidato = new Plato(
                enEdicion == null ? 0 : enEdicion.getId(),
                txtNombre.getText(),
                cmbCategoria.getValue(),
                leerPrecio(),
                chkDisponible.isSelected());
        boolean eraNuevo = enEdicion == null;

        try {
            if (eraNuevo) {
                servicio.crear(candidato);
            } else {
                servicio.actualizar(candidato);
            }
        } catch (ReglaNegocioException e) {
            mostrarError(txtNombre, lblErrorNombre, e.mensajeDe(PlatoService.CAMPO_NOMBRE));
            mostrarError(cmbCategoria, lblErrorCategoria, e.mensajeDe(PlatoService.CAMPO_CATEGORIA));
            mostrarError(txtPrecio, lblErrorPrecio, e.mensajeDe(PlatoService.CAMPO_PRECIO));
            return;
        }

        modoCrear();
        recargar();
        lblMensaje.setText(eraNuevo ? "Plato creado correctamente." : "Plato actualizado correctamente.");
    }

    /** Si el texto no es un número devuelve 0 y el servicio responde "El precio debe ser mayor que 0". */
    private double leerPrecio() {
        String texto = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
        texto = texto.replace(".", "").replace(",", ".");
        try {
            return texto.isEmpty() ? 0 : Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ---------- Eliminar (REQ015) ----------

    @FXML
    private void onEliminar() {
        Plato plato = tblPlatos.getSelectionModel().getSelectedItem();
        if (plato == null) {
            return;
        }
        boolean enUso = servicio.estaEnUso(plato.getId());
        ButtonType accion = new ButtonType(enUso ? "Marcar como no disponible" : "Eliminar",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
        dialogo.setTitle("Eliminar plato");
        dialogo.setHeaderText("Eliminar plato");
        dialogo.setContentText(enUso
                ? plato.getNombre() + " · ID " + plato.getId() + "\n\n"
                + "Este plato ya aparece en órdenes. No se borrará: se marcará como no disponible."
                : "¿Eliminar «" + plato.getNombre() + "»? Esta acción no se puede deshacer.");
        dialogo.getButtonTypes().setAll(accion, cancelar);

        Optional<ButtonType> respuesta = dialogo.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != accion) {
            return;
        }

        PlatoService.ResultadoEliminacion resultado = servicio.eliminar(plato.getId());
        if (enEdicion != null && enEdicion.getId() == plato.getId()) {
            modoCrear();
        }
        recargar();
        lblMensaje.setText(resultado == PlatoService.ResultadoEliminacion.ELIMINADO
                ? "Plato eliminado."
                : "El plato ya estaba en órdenes: quedó marcado como no disponible.");
    }

    // ---------- Utilidades de UI ----------

    private void mostrarError(Control control, Label etiqueta, String mensaje) {
        if (mensaje == null) {
            return;
        }
        etiqueta.setText(mensaje);
        if (!control.getStyleClass().contains(CLASE_ERROR)) {
            control.getStyleClass().add(CLASE_ERROR);
        }
    }

    private void limpiarErrores() {
        for (Control c : List.of(txtNombre, cmbCategoria, txtPrecio)) {
            c.getStyleClass().remove(CLASE_ERROR);
        }
        lblErrorNombre.setText("");
        lblErrorCategoria.setText("");
        lblErrorPrecio.setText("");
    }

    private static DecimalFormat crearFormatoPrecio() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        return new DecimalFormat("$#,##0", simbolos);
    }
}