package co.edu.uniquindio.restaurante.controlador;

import co.edu.uniquindio.restaurante.modelo.entidad.CategoriaPlato;
import co.edu.uniquindio.restaurante.modelo.entidad.ItemPedido;
import co.edu.uniquindio.restaurante.modelo.entidad.Mesa;
import co.edu.uniquindio.restaurante.modelo.entidad.Mesero;
import co.edu.uniquindio.restaurante.modelo.entidad.OrdenPedido;
import co.edu.uniquindio.restaurante.modelo.entidad.Plato;
import co.edu.uniquindio.restaurante.modelo.excepciones.ReglaNegocioException;
import co.edu.uniquindio.restaurante.modelo.servicio.ContextoApp;
import co.edu.uniquindio.restaurante.modelo.servicio.MesaService;
import co.edu.uniquindio.restaurante.modelo.servicio.MeseroService;
import co.edu.uniquindio.restaurante.modelo.servicio.PedidoService;
import co.edu.uniquindio.restaurante.modelo.servicio.PlatoService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Pantalla 1: Realizar pedido (REQ001 a REQ012). Sin reglas de negocio: valida el modelo. */
public class PedidoController {
    private static final String CLASE_ERROR = "campo-error";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PedidoService pedidoService = ContextoApp.getInstancia().getPedidoService();
    private final PlatoService platoService = ContextoApp.getInstancia().getPlatoService();
    private final MesaService mesaService = ContextoApp.getInstancia().getMesaService();
    private final MeseroService meseroService = ContextoApp.getInstancia().getMeseroService();
    private final DecimalFormat formatoPrecio = crearFormatoPrecio();

    private OrdenPedido orden;
    private CategoriaPlato categoriaActiva;
    private boolean enviada;

    @FXML private Label lblIdPedido;
    @FXML private Label lblFechaHora;
    @FXML private Label lblBanner;
    @FXML private ComboBox<Mesa> cmbMesa;
    @FXML private ComboBox<Mesero> cmbMesero;
    @FXML private Label lblErrorMesa;
    @FXML private Label lblErrorMesero;
    @FXML private TextField txtBuscarPlato;
    @FXML private HBox hboxCategorias;
    @FXML private TableView<Plato> tblPlatosMenu;
    @FXML private TableColumn<Plato, String> colMenuNombre;
    @FXML private TableColumn<Plato, String> colMenuCategoria;
    @FXML private TableColumn<Plato, String> colMenuPrecio;
    @FXML private TableColumn<Plato, Boolean> colMenuEstado;
    @FXML private Label lblErrorPlato;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private Button btnAgregarPlato;
    @FXML private Label lblErrorCantidad;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtObservaciones;
    @FXML private TableView<ItemPedido> tblItemsPedido;
    @FXML private TableColumn<ItemPedido, String> colItemPlato;
    @FXML private TableColumn<ItemPedido, String> colItemImporte;
    @FXML private Label lblErrorOrden;
    @FXML private Button btnQuitarPlato;
    @FXML private Label lblTotal;
    @FXML private Button btnCancelar;
    @FXML private Button btnEnviarACocina;

    @FXML
    private void initialize() {
        for (Label l : List.of(lblBanner, lblErrorMesa, lblErrorMesero, lblErrorPlato, lblErrorCantidad, lblErrorOrden)) {
            l.visibleProperty().bind(l.textProperty().isNotEmpty());
            l.managedProperty().bind(l.textProperty().isNotEmpty());
        }
        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 1));
        configurarTablas();
        crearChipsCategoria();

        txtBuscarPlato.textProperty().addListener((obs, a, b) -> onBuscarPlato());
        tblItemsPedido.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> actualizarBotones());

        nuevoBorrador();
    }

    // ---------- Configuración ----------

    private void configurarTablas() {
        colMenuNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colMenuCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().toString()));
        colMenuPrecio.setCellValueFactory(c -> new SimpleStringProperty(formatoPrecio.format(c.getValue().getPrecio())));
        colMenuEstado.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isDisponible()));
        colMenuEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean disponible, boolean empty) {
                super.updateItem(disponible, empty);
                if (empty || disponible == null) {
                    setGraphic(null);
                    return;
                }
                Label pill = new Label(disponible ? "Disponible" : "No disponible");
                pill.getStyleClass().addAll("pill", disponible ? "pill-disponible" : "pill-no-disponible");
                setGraphic(pill);
            }
        });
        tblPlatosMenu.setPlaceholder(new Label("No hay platos que coincidan con la búsqueda."));

        colItemPlato.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCantidad() + " × " + c.getValue().getNombrePlato()
                        + "  (" + formatoPrecio.format(c.getValue().getPrecioUnitario()) + " c/u)"));
        colItemImporte.setCellValueFactory(c ->
                new SimpleStringProperty(formatoPrecio.format(c.getValue().getSubtotal())));
        tblItemsPedido.setPlaceholder(new Label("Todavía no hay platos en la comanda."));
    }

    private void crearChipsCategoria() {
        for (CategoriaPlato categoria : CategoriaPlato.values()) {
            ToggleButton chip = new ToggleButton(categoria.toString());
            chip.getStyleClass().add("chip");
            chip.setOnAction(e -> {
                for (Node otro : hboxCategorias.getChildren()) {
                    if (otro != chip) {
                        ((ToggleButton) otro).setSelected(false);
                    }
                }
                categoriaActiva = chip.isSelected() ? categoria : null;
                onBuscarPlato();
            });
            hboxCategorias.getChildren().add(chip);
        }
    }

    // ---------- Menú (REQ001) ----------

    @FXML
    private void onBuscarPlato() {
        tblPlatosMenu.getItems().setAll(platoService.buscar(txtBuscarPlato.getText(), categoriaActiva));
        tblPlatosMenu.refresh();
    }

    // ---------- Comanda (REQ005 a REQ007, REQ011) ----------

    @FXML
    private void onAgregarItem() {
        limpiarErrores();
        Plato plato = tblPlatosMenu.getSelectionModel().getSelectedItem();
        if (plato == null) {
            lblErrorPlato.setText("Seleccione un plato del menú");
            return;
        }
        try {
            pedidoService.agregarItem(orden, plato, leerCantidad());
        } catch (ReglaNegocioException e) {
            mostrarError(tblPlatosMenu, lblErrorPlato, e.mensajeDe(ReglaNegocioException.CAMPO_PLATO));
            mostrarError(spnCantidad, lblErrorCantidad, e.mensajeDe(ReglaNegocioException.CAMPO_CANTIDAD));
            mostrarError(tblItemsPedido, lblErrorOrden, e.mensajeDe(ReglaNegocioException.CAMPO_ORDEN));
            return;
        }
        refrescarComanda();
    }

    @FXML
    private void onQuitarItem() {
        limpiarErrores();
        ItemPedido item = tblItemsPedido.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        try {
            pedidoService.quitarItem(orden, item);
        } catch (ReglaNegocioException e) {
            mostrarError(tblItemsPedido, lblErrorOrden, e.mensajeDe(ReglaNegocioException.CAMPO_ORDEN));
            return;
        }
        refrescarComanda();
    }

    /** Si el texto no es un entero devuelve 0 y el modelo responde con el mensaje de la Regla 004. */
    private int leerCantidad() {
        try {
            return Integer.parseInt(spnCantidad.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void refrescarComanda() {
        tblItemsPedido.getItems().setAll(orden.getItems());
        lblTotal.setText(formatoPrecio.format(pedidoService.calcularTotal(orden)));
        actualizarBotones();
    }

    private void actualizarBotones() {
        btnAgregarPlato.setDisable(enviada);
        btnQuitarPlato.setDisable(enviada || tblItemsPedido.getSelectionModel().getSelectedItem() == null);
    }

    // ---------- Envío (REQ008 a REQ012) ----------

    @FXML
    private void onEnviarACocina() {
        limpiarErrores();
        orden.setNombreCliente(textoOpcional(txtNombreCliente));
        orden.setObservaciones(textoOpcional(txtObservaciones));
        try {
            if (cmbMesa.getValue() != null) {
                pedidoService.seleccionarMesa(orden, cmbMesa.getValue());
            }
            if (cmbMesero.getValue() != null) {
                pedidoService.seleccionarMesero(orden, cmbMesero.getValue());
            }
            pedidoService.enviarACocina(orden);
        } catch (ReglaNegocioException e) {
            lblBanner.setText("No se pudo enviar la orden. Revise los campos marcados en rojo.");
            mostrarError(cmbMesa, lblErrorMesa, e.mensajeDe(ReglaNegocioException.CAMPO_MESA));
            mostrarError(cmbMesero, lblErrorMesero, e.mensajeDe(ReglaNegocioException.CAMPO_MESERO));
            mostrarError(tblItemsPedido, lblErrorOrden, e.mensajeDe(ReglaNegocioException.CAMPO_ORDEN));
            return;
        }

        enviada = true;
        actualizarBotones();
        lblFechaHora.setText("Fecha y hora: " + orden.getFechaHora().format(FORMATO_FECHA));
        mostrarConfirmacion();
        nuevoBorrador();
    }

    private void mostrarConfirmacion() {
        ButtonType aceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        Alert dialogo = new Alert(Alert.AlertType.INFORMATION);
        dialogo.setTitle("Pedido enviado a cocina");
        dialogo.setHeaderText("Pedido enviado a cocina");
        dialogo.setContentText("La comanda se registró correctamente.\n\n"
                + "Pedido: N.º " + orden.getId() + "\n"
                + "Mesa: " + cmbMesa.getValue() + "\n"
                + "Mesero: " + cmbMesero.getValue() + "\n"
                + "Fecha y hora: " + orden.getFechaHora().format(FORMATO_FECHA) + "\n"
                + "Total: " + formatoPrecio.format(orden.getTotal()));
        dialogo.getButtonTypes().setAll(aceptar);
        dialogo.showAndWait();
    }

    @FXML
    private void onCancelar() {
        nuevoBorrador();
    }

    // ---------- Estado de la pantalla ----------

    private void nuevoBorrador() {
        orden = pedidoService.crearBorrador();
        enviada = false;
        categoriaActiva = null;
        for (Node chip : hboxCategorias.getChildren()) {
            ((ToggleButton) chip).setSelected(false);
        }
        txtBuscarPlato.clear();
        txtNombreCliente.clear();
        txtObservaciones.clear();
        cmbMesa.getItems().setAll(mesaService.listarActivas());
        cmbMesero.getItems().setAll(meseroService.listarActivos());
        cmbMesa.setValue(null);
        cmbMesero.setValue(null);
        spnCantidad.getValueFactory().setValue(1);
        lblIdPedido.setText("N.º de pedido " + pedidoService.siguienteNumeroPedido());
        lblFechaHora.setText("Fecha y hora: se registra al enviar");
        limpiarErrores();
        onBuscarPlato();
        refrescarComanda();
    }

    // ---------- Utilidades de UI ----------

    private String textoOpcional(TextField campo) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

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
        for (Control c : List.of(cmbMesa, cmbMesero, tblPlatosMenu, spnCantidad, tblItemsPedido)) {
            c.getStyleClass().remove(CLASE_ERROR);
        }
        for (Label l : List.of(lblBanner, lblErrorMesa, lblErrorMesero, lblErrorPlato, lblErrorCantidad, lblErrorOrden)) {
            l.setText("");
        }
    }

    private static DecimalFormat crearFormatoPrecio() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        return new DecimalFormat("$#,##0", simbolos);
    }
}