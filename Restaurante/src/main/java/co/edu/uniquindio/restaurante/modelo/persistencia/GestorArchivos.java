package co.edu.uniquindio.restaurante.modelo.persistencia;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    private final Path carpeta;

    public GestorArchivos() {
        this(Paths.get("data"));
    }

    public GestorArchivos(Path carpeta) {
        this.carpeta = carpeta;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> List<T> leer(String archivo) {
        Path ruta = carpeta.resolve(archivo);
        if (!Files.exists(ruta)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(ruta))) {
            return (List<T>) in.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Archivo incompatible: " + archivo, e);
        }
    }

    public <T extends Serializable> void escribir(String archivo, List<T> datos) {
        try {
            Files.createDirectories(carpeta);
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(carpeta.resolve(archivo)))) {
                out.writeObject(new ArrayList<>(datos));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + archivo, e);
        }
    }
}