package co.edu.uniquindio.restaurante.modelo.entidad;

import java.io.Serializable;

public interface Identificable extends Serializable {
    int getId();

    void setId(int id);
}