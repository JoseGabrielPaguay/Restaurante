package co.edu.uniquindio.restaurante.modelo.entidad;


public class Mesa implements Identificable  {
    private static final long serialVersionUID = 1L;

    private int id;
    private int numero;
    private int capacidad;
    private EstadoMesa estado;

    public Mesa(int id, int numero, int capacidad, EstadoMesa estado) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    /** Una mesa esta activa si no esta fuera de servicio (Regla 001). */
    public boolean estaActiva() {
        return estado != EstadoMesa.FUERA_DE_SERVICIO;
    }

    /** Al enviar una orden a cocina la mesa pasa a ocupada (Regla 006). */
    public void ocupar() {
        this.estado = EstadoMesa.OCUPADA;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public EstadoMesa getEstado() { return estado; }
    public void setEstado(EstadoMesa estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Mesa " + numero;
    }
}
