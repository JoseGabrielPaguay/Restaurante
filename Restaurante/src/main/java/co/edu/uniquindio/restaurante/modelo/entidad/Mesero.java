package co.edu.uniquindio.restaurante.modelo.entidad;



public class Mesero implements Identificable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String cedula;
    private String turno;
    private boolean activo;

    public Mesero(int id, String nombre, String cedula, String turno, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.turno = turno;
        this.activo = activo;
    }

    /** Solo un mesero activo puede tomar ordenes (Regla 002). */
    public boolean estaActivo() {
        return activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombre;
    }
}
