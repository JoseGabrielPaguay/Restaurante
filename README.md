# Restaurante · Gestión de pedidos

Aplicación de escritorio (JavaFX + Maven) para tomar pedidos en un restaurante y mantener el menú.
Proyecto final de Software, Universidad del Quindío.

**Integrantes:** Luis Daniel Gómez · Juan Esteban Guerrero · José Gabriel Paguay

| Pantalla | Qué hace | Requisitos |
|---|---|---|
| 1 · Realizar pedido | Consultar el menú, elegir mesa y mesero, armar la comanda y enviarla a cocina | REQ001 a REQ012 |
| 2 · Platos (CRUD) | Listar, buscar, crear, editar y eliminar platos del menú | REQ013 a REQ016 |

---

## 1. Requisitos previos

- **JDK 17 o superior** (el proyecto compila con `release 17`; se probó con JDK 21).
- **Maven 3.8+**. Si no lo tienes instalado, usa el wrapper incluido (`./mvnw` en Linux/macOS, `mvnw.cmd` en Windows) en lugar de `mvn`.
- JavaFX **no** hay que instalarlo: Maven descarga `javafx-controls` y `javafx-fxml` 21.0.6.

## 2. Cómo correr la aplicación

El proyecto está en la carpeta interna `Restaurante/Restaurante` del repositorio.

```bash
git clone https://github.com/JoseGabrielPaguay/Restaurante.git
cd Restaurante/Restaurante
mvn clean javafx:run
```

Con el wrapper: `./mvnw clean javafx:run` (Windows: `mvnw.cmd clean javafx:run`).

La clase principal es `co.edu.uniquindio.restaurante.App` (también existe `Launcher` para ejecutar desde un IDE sin la configuración del módulo).

> **Importante:** ejecuta el comando desde `Restaurante/Restaurante` (donde está el `pom.xml`).
> Los datos se guardan en la carpeta `data/` relativa al directorio desde el que se lanza la app.

## 3. Cómo probar

```bash
mvn test
```

Son **179 pruebas unitarias** (JUnit 5) sobre el modelo: entidades, excepciones, persistencia y servicios.
Todas usan carpetas temporales (`@TempDir`), así que **no tocan** tu carpeta `data/`.

| Paquete | Clases de prueba | Pruebas |
|---|---|---|
| `modelo.entidad` | `OrdenPedidoTest` (casos Given-When-Then GWT-P1 a P9), `OrdenPedidoCasosAdicionalesTest`, `ItemPedidoTest`, `MesaTest`, `MeseroTest`, `PlatoTest`, `CategoriaPlatoTest` | 63 |
| `modelo.excepciones` | `ReglaNegocioExceptionTest` | 10 |
| `modelo.persistencia` | `GestorArchivosTest`, `RepositorioArchivoTest`, `RepositoriosConcretosTest`, `DataLoaderTest` | 40 |
| `modelo.servicio` | `PedidoServiceTest`, `PlatoServiceTest`, `MesaServiceTest`, `MeseroServiceTest`, `ContextoAppTest` | 66 |

Para ejecutar una sola clase: `mvn test -Dtest=PedidoServiceTest`.

Los controladores JavaFX no tienen pruebas automáticas: se verifican manualmente con la guía de la sección 5.

### Casos de aceptación (`OrdenPedidoTest`)

| Caso | Escenario | Regla |
|---|---|---|
| GWT-P1 | Orden completa se envía con auditoría y total correcto; la mesa queda ocupada | 004, 005, 006 |
| GWT-P2 | Sin mesa, mesero ni platos se reportan los tres errores a la vez | 001, 002, 004 |
| GWT-P3 | Una mesa fuera de servicio no se puede asignar | 001 |
| GWT-P4 | Un plato no disponible no se agrega | 003 |
| GWT-P5 | Cantidad fuera de 1 a 20 se rechaza | 004 |
| GWT-P6 | El total lo calcula el sistema y se actualiza al quitar un plato | 005 |
| GWT-P7 | El precio se copia al ítem y no cambia si el plato cambia de precio | 005 |
| GWT-P8 | Una orden enviada a cocina no se puede modificar | 007 |
| GWT-P9 | El mismo plato agregado dos veces suma la cantidad en una sola línea | 004 |

## 4. Datos de prueba

Si la carpeta `data/` no existe o los archivos están vacíos, al arrancar se cargan automáticamente
(`DataLoader.cargarSiHaceFalta`): **12 mesas, 6 meseros, 20 platos y 3 órdenes de ejemplo**.

Para volver a los datos originales, cierra la app y **borra la carpeta `data/`**.

### Usuarios demo (meseros)

La aplicación no tiene inicio de sesión: el mesero se elige en la lista **Mesero** de la pantalla *Realizar pedido*.
Esa lista solo muestra los meseros **activos** (Regla 002).

| Id | Nombre | Cédula | Turno | Estado |
|---|---|---|---|---|
| 1 | Ana Torres | 1001230001 | Noche | Activo |
| 2 | Luis Ortega | 1001230002 | Mañana | Activo |
| 3 | Camila Rojas | 1001230003 | Tarde | Activo |
| 4 | Mateo Salazar | 1001230004 | Mañana | Activo |
| 5 | Valentina Cruz | 1001230005 | Tarde | Activo |
| 6 | Jorge Pineda | 1001230006 | Noche | **Inactivo** (no aparece en la lista) |

### Mesas

La lista **Mesa** solo muestra las mesas **activas** (libres u ocupadas), es decir 10 de las 12 (Regla 001).

| Mesas | Capacidad | Estado |
|---|---|---|
| 1, 5, 10 | 2 | 1 y 5 libres · 10 ocupada |
| 2, 3, 6, 9, 11 | 4 | 2, 3, 6 libres · 9 ocupada · 11 fuera de servicio |
| 4, 7, 12 | 6 | 4 y 7 libres · 12 fuera de servicio |
| 8 | 8 | Libre |

Resumen: libres 1 a 8 · ocupadas 9 y 10 · fuera de servicio 11 y 12.

### Platos

| Id | Plato | Categoría | Precio | Estado | Está en alguna orden |
|---|---|---|---|---|---|
| 1 | Bandeja paisa | Plato fuerte | $28.000 | Disponible | Sí (orden 1) |
| 2 | Sopa de lentejas | Entrada | $12.000 | Disponible | No |
| 3 | Trucha al ajillo | Plato fuerte | $32.000 | **No disponible** | No |
| 4 | Limonada de coco | Bebida | $8.000 | Disponible | Sí (orden 1) |
| 5 | Tres leches | Postre | $9.500 | Disponible | Sí (orden 3) |
| 6 | Arroz con pollo | Plato fuerte | $22.000 | Disponible | Sí (orden 3) |
| 7 | Jugo de mora | Bebida | $7.000 | Disponible | Sí (orden 2) |
| 8 | Ensalada de la casa | Entrada | $16.000 | Disponible | No |
| 9 | Ajiaco | Plato fuerte | $26.000 | Disponible | Sí (orden 2) |
| 10 | Sancocho de gallina | Plato fuerte | $25.000 | Disponible | No |
| 11 | Churrasco | Plato fuerte | $34.000 | Disponible | No |
| 12 | Pechuga a la plancha | Plato fuerte | $24.000 | Disponible | No |
| 13 | Mondongo | Plato fuerte | $23.000 | Disponible | No |
| 14 | Empanadas | Entrada | $7.000 | Disponible | No |
| 15 | Patacones con hogao | Entrada | $9.000 | Disponible | No |
| 16 | Limonada natural | Bebida | $6.000 | Disponible | No |
| 17 | Café | Bebida | $4.500 | Disponible | No |
| 18 | Gaseosa | Bebida | $5.000 | Disponible | No |
| 19 | Brownie con helado | Postre | $11.000 | Disponible | No |
| 20 | Flan de caramelo | Postre | $8.500 | **No disponible** | No |

### Órdenes de ejemplo (ya enviadas a cocina)

| N.º | Mesa | Mesero | Cliente | Platos | Total |
|---|---|---|---|---|---|
| 1 | 9 | Ana Torres | Familia Rojas | 2 × Bandeja paisa, 2 × Limonada de coco | $72.000 |
| 2 | 10 | Luis Ortega | Grupo de oficina | 3 × Ajiaco, 3 × Jugo de mora | $99.000 |
| 3 | 9 | Camila Rojas | (sin nombre) | 1 × Arroz con pollo, 1 × Tres leches | $31.500 |

La próxima orden que se cree recibe el **N.º 4**.

## 5. Guía de demostración

Escenarios rápidos para comprobar cada regla en la interfaz.

**Pantalla 1 · Realizar pedido**

| Qué hacer | Resultado esperado | Regla |
|---|---|---|
| Pulsar *Enviar a cocina* con todo vacío | Banner rojo y mensajes «Seleccione una mesa activa», «Seleccione un mesero activo» y «Agregue al menos un plato» | 001, 002, 004 |
| Seleccionar *Trucha al ajillo* o *Flan de caramelo* y agregar | «El plato no está disponible» | 003 |
| Escribir 0, 21 o un texto en la cantidad y agregar | «Cantidad inválida: use un número de 1 a 20» | 004 |
| Agregar *Bandeja paisa* dos veces | Una sola línea con la cantidad sumada; el total se actualiza solo | 004, 005 |
| Buscar «cafe» (sin tilde) o pulsar un chip de categoría | La lista se filtra sin importar tildes ni mayúsculas | REQ001 |
| Elegir mesa, mesero y platos y enviar | Ventana «Pedido enviado a cocina» con n.º, mesa, mesero, fecha y total; la mesa pasa a ocupada | 006 |
| Cerrar y abrir la app | La orden y el estado de la mesa se conservan | RNF-05 |

**Pantalla 2 · Platos**

| Qué hacer | Resultado esperado | Requisito |
|---|---|---|
| Guardar con campos vacíos | Mensajes bajo nombre, categoría y precio | REQ016 |
| Crear un plato con un nombre existente (ej. «café») | «Ya existe un plato con ese nombre» | REQ016 |
| Precio 0, negativo o con letras | «El precio debe ser mayor que 0» | REQ016 |
| Eliminar *Empanadas* (id 14) | Se borra definitivamente | REQ015 |
| Eliminar *Bandeja paisa* (id 1) | Aviso «ya aparece en órdenes»: no se borra, queda como no disponible | REQ015 |

## 6. Arquitectura

Arquitectura en capas, sin reglas de negocio en los controladores (RNF-06).

```
src/main/java/co/edu/uniquindio/restaurante/
├── App.java, Launcher.java          Arranque de JavaFX
├── controlador/                     PrincipalController, PedidoController, PlatoController
└── modelo/
    ├── entidad/                     OrdenPedido, ItemPedido, Plato, Mesa, Mesero + enums
    ├── excepciones/                 ReglaNegocioException, ErrorRegla
    ├── persistencia/                GestorArchivos, RepositorioArchivo<T> + 4 repositorios, DataLoader
    └── servicio/                    PedidoService, PlatoService, MesaService, MeseroService, ContextoApp
src/main/resources/.../vista/        pedido-view.fxml, plato-view.fxml, principal-view.fxml
src/main/resources/.../css/          estilos.css
data/                                Archivos .dat (se crean al primer arranque)
```

- **Persistencia:** serialización Java en `data/mesas.dat`, `meseros.dat`, `platos.dat` y `ordenes.dat`.
- **Validación en dos niveles (RNF-02):** la interfaz valida formato (números, campos vacíos) y el modelo valida las reglas; los controladores solo muestran el mensaje de `ReglaNegocioException`.

### Reglas de negocio y dónde se aplican

| Regla | Descripción | Implementación | Mensaje |
|---|---|---|---|
| 001 | Mesa asignada y activa | `Mesa.estaActiva()`, `OrdenPedido.asignarMesa()`, `OrdenPedido.validarReglas()`, `PedidoService.enviarACocina()` | Seleccione una mesa activa |
| 002 | Mesero asignado y activo | `Mesero.estaActivo()`, `OrdenPedido.asignarMesero()`, `OrdenPedido.validarReglas()` | Seleccione un mesero activo |
| 003 | Solo platos disponibles | `Plato.estaDisponible()`, `OrdenPedido.agregarItem()` | El plato no está disponible |
| 004 | Al menos un ítem y cantidad de 1 a 20 | `OrdenPedido.agregarItem()`, `OrdenPedido.validarReglas()` | Cantidad inválida: use un número de 1 a 20 · Agregue al menos un plato |
| 005 | Total calculado por el sistema | `ItemPedido.calcularSubtotal()`, `OrdenPedido.calcularTotal()` | (no editable) |
| 006 | Auditoría al enviar y mesa ocupada | `OrdenPedido.registrarAuditoria()`, `Mesa.ocupar()`, `PedidoService.enviarACocina()` | (automático) |
| 007 | Orden enviada no se modifica | `OrdenPedido.exigirBorrador()` (usado por `asignarMesa`, `asignarMesero`, `agregarItem`, `quitarItem`, `enviarACocina`) | La orden ya fue enviada a cocina |

## 7. Matriz de trazabilidad

Los nombres de `fx:id` y de métodos corresponden al código actual.

### Pantalla 1 · Realizar pedido (`pedido-view.fxml` · `PedidoController`)

| # | Requisito | CUS / Diagrama | Widget (`fx:id`) | Controlador / método | Servicio / modelo | Pruebas |
|---|---|---|---|---|---|---|
| 1 | **REQ001** Consultar menú actualizado y buscar por nombre o categoría (R003) | CUS-01 · Act-01 «Consultar el menú actualizado» | `txtBuscarPlato`, `hboxCategorias` (chips), `tblPlatosMenu` (`colMenuNombre`, `colMenuCategoria`, `colMenuPrecio`, `colMenuEstado`) | `PedidoController.onBuscarPlato()` | `PlatoService.buscar(texto, categoria)` | `PlatoServiceTest` |
| 2 | **REQ002** Seleccionar mesa y mesero activos (R001, R002) | CUS-01 · Act-01 «Seleccionar mesa y mesero» | `cmbMesa`, `cmbMesero` | `PedidoController.nuevoBorrador()` carga las listas | `MesaService.listarActivas()`, `MeseroService.listarActivos()` | `MesaServiceTest`, `MeseroServiceTest` |
| 3 | **REQ003** Crear orden en borrador con cliente y observaciones opcionales (R001, R002) | CUS-01 · Act-01 «Indicar platos, cantidades y observaciones» | `txtNombreCliente`, `txtObservaciones` | `PedidoController.nuevoBorrador()`, `PedidoController.onEnviarACocina()` | `PedidoService.crearBorrador()`, `OrdenPedido.setNombreCliente()`, `OrdenPedido.setObservaciones()` | `PedidoServiceTest`, `OrdenPedidoCasosAdicionalesTest` |
| 4 | **REQ004** Id de la orden único, automático e incremental | Diagrama de clases (`OrdenPedido.id`) | `lblIdPedido` («N.º de pedido») | `PedidoController.nuevoBorrador()` | `PedidoService.siguienteNumeroPedido()`, `RepositorioArchivo.siguienteId()`, `RepositorioArchivo.guardar()` | `RepositorioArchivoTest`, `PedidoServiceTest` |
| 5 | **REQ005** Agregar y quitar platos mientras es borrador (R007) | CUS-01 · Act-01 «Agregar plato a la orden» | `spnCantidad`, `btnAgregarPlato`, `tblItemsPedido` (`colItemPlato`, `colItemImporte`), `btnQuitarPlato` | `PedidoController.onAgregarItem()`, `PedidoController.onQuitarItem()` | `PedidoService.agregarItem()`, `PedidoService.quitarItem()` | `OrdenPedidoTest` (P6, P9), `OrdenPedidoCasosAdicionalesTest` |
| 6 | **REQ006** Rechazar platos no disponibles y cantidades fuera de 1 a 20 (R003, R004) | CUS-01 · Act-01 «¿Plato disponible?» y «¿Cantidad entre 1 y 20?» | `lblErrorPlato` (junto a `tblPlatosMenu`), `lblErrorCantidad` (junto a `spnCantidad`) | `PedidoController.onAgregarItem()` (captura `ReglaNegocioException`) | `OrdenPedido.agregarItem()` | `OrdenPedidoTest` (P4, P5), `OrdenPedidoCasosAdicionalesTest` |
| 7 | **REQ007** Copiar precio, calcular subtotal y total sin edición manual (R005) | CUS-01 · Act-01 «Calcular el total de la orden» | `lblTotal` (solo lectura), «$ c/u» en `colItemPlato` | `PedidoController.refrescarComanda()` | `PedidoService.calcularTotal()`, `OrdenPedido.calcularTotal()`, `ItemPedido.calcularSubtotal()` | `ItemPedidoTest`, `OrdenPedidoTest` (P6, P7) |
| 8 | **REQ008** No enviar sin mesa ni mesero activos (R001, R002) | CUS-01 · Act-01 «¿Mesa y mesero válidos?» · Com-01 `validarReglas()` | `cmbMesa` + `lblErrorMesa`, `cmbMesero` + `lblErrorMesero`, `lblBanner` | `PedidoController.onEnviarACocina()` | `PedidoService.enviarACocina()`, `OrdenPedido.validarReglas()` | `OrdenPedidoTest` (P2, P3), `PedidoServiceTest` |
| 9 | **REQ009** Enviar a cocina solo si cumple todas las reglas; guardar y ocupar mesa (R004, R006) | CUS-01 · Act-01 «¿Orden con al menos un plato?» / «Enviar orden a cocina» · Com-01 `ocupar()` | `btnEnviarACocina`, `lblErrorOrden` | `PedidoController.onEnviarACocina()` | `PedidoService.enviarACocina()`, `OrdenPedido.enviarACocina()`, `Mesa.ocupar()` | `OrdenPedidoTest` (P1), `PedidoServiceTest` |
| 10 | **REQ010** Registrar fecha, hora y mesero al enviar (R006) | CUS-01 · Act-01 «Registrar fecha, hora y mesero» · Com-01 `registrarAuditoria()` | `lblFechaHora` (solo lectura) | `PedidoController.onEnviarACocina()` | `OrdenPedido.registrarAuditoria()`, `OrdenPedido.getIdMesero()` | `OrdenPedidoCasosAdicionalesTest` |
| 11 | **REQ011** Bloquear orden enviada (R007) | CUS-01 · Act-01 «Recibir orden» | `btnAgregarPlato` y `btnQuitarPlato` quedan deshabilitados al enviar | `PedidoController.actualizarBotones()` | `PedidoService.agregarItem()`, `PedidoService.quitarItem()`, `OrdenPedido.exigirBorrador()` | `OrdenPedidoTest` (P8), `OrdenPedidoCasosAdicionalesTest`, `PedidoServiceTest` |
| 12 | **REQ012** Confirmación con id, mesa, mesero, fecha y total | Com-01 `mostrarResultado()` | Ventana «Pedido enviado a cocina» con botón «Aceptar» (`Alert`, sin `fx:id`) | `PedidoController.mostrarConfirmacion()` (llamado desde `onEnviarACocina()`) | `OrdenPedido.getId()`, `getFechaHora()`, `getTotal()` | Manual (sección 5) |

### Pantalla 2 · CRUD de Plato (`plato-view.fxml` · `PlatoController`)

| # | Requisito | CUS / Diagrama | Widget (`fx:id`) | Controlador / método | Servicio / modelo | Pruebas |
|---|---|---|---|---|---|---|
| 13 | **REQ013** Listar platos y buscar por nombre o categoría | CUS-02 · Gestionar menú, paso 1 | `txtBuscar`, `tblPlatos` (`colId`, `colNombre`, `colCategoria`, `colPrecio`, `colEstado`), `btnAnterior`, `btnSiguiente`, `lblPaginacion` | `PlatoController.onBuscar()`, `onAnterior()`, `onSiguiente()` | `PlatoService.buscar()` | `PlatoServiceTest` |
| 14 | **REQ014** Crear y actualizar platos | CUS-02 · Gestionar menú, pasos 2 a 5 | `btnNuevo`, `btnEditar`, `btnGuardar`, `lblTituloForm`, `txtNombre`, `cmbCategoria`, `txtPrecio`, `chkDisponible` | `PlatoController.onNuevo()`, `onEditar()`, `onGuardar()`, `onCancelar()` | `PlatoService.crear()`, `PlatoService.actualizar()` | `PlatoServiceTest`, `RepositorioArchivoTest` |
| 15 | **REQ015** Eliminar plato; si está en una orden se marca como no disponible (R003) | CUS-02 · Flujo alterno 2 | `btnEliminar`, diálogo «Este plato ya aparece en órdenes» (`Alert`) | `PlatoController.onEliminar()` | `PlatoService.estaEnUso()`, `PlatoService.eliminar()` | `PlatoServiceTest` |
| 16 | **REQ016** Validar plato: nombre obligatorio y único, categoría obligatoria, precio > 0 | CUS-02 · Flujo alterno 1 | `lblErrorNombre`, `lblErrorCategoria`, `lblErrorPrecio` | `PlatoController.onGuardar()` (captura `ReglaNegocioException`) | `PlatoService.crear()`, `PlatoService.actualizar()` (validación interna) | `PlatoServiceTest` |

### Requisitos no funcionales

| RNF | Cómo se cumple |
|---|---|
| RNF-01 Rendimiento | Consultas en memoria sobre listas pequeñas; la persistencia es un archivo local por entidad |
| RNF-02 Validación | Reglas en `OrdenPedido` y `PlatoService`; la interfaz valida formato (`leerCantidad()`, `leerPrecio()`) |
| RNF-03 Mensajes de error | Mensaje en español junto al control (`lblError*`), tomado de `ReglaNegocioException.mensajeDe(campo)` |
| RNF-04 Usabilidad | Etiquetas visibles, errores con texto (no solo color) y borde rojo con la clase CSS `campo-error` |
| RNF-05 Persistencia | `GestorArchivos` + `RepositorioArchivo`; `DataLoader` carga 12 mesas, 6 meseros y 20 platos si no hay datos |
| RNF-06 Mantenibilidad | Capas `controlador` / `modelo`; nombres del diagrama de clases; reglas fuera de los controladores |
| RNF-07 Ejecución | `mvn clean javafx:run` (sección 2) |

## 8. Solución de problemas

| Problema | Solución |
|---|---|
| `mvn` no se reconoce | Usa `./mvnw` / `mvnw.cmd` o instala Maven |
| Error de versión de Java | Verifica con `java -version` que sea 17 o superior |
| La app abre con datos raros o vacíos | Cierra la app y borra la carpeta `data/` para regenerar los datos de prueba |
| `data/` aparece en otra carpeta | Se crea donde se ejecuta el comando; lanza siempre desde `Restaurante/Restaurante` |
