package controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PersistenciaTexto {
    public static final String SEPARADOR = ";";
    private static PersistenciaTexto instancia;

    private PersistenciaTexto() {
    }

    public static PersistenciaTexto getInstance() {
        if (instancia == null) {
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }

    public static List<String[]> leeRegistros(String nombreArchivo) throws MeteorologiaException {
        List<String[]> registros = new ArrayList<>();
        try (Scanner scan = new Scanner(new File(nombreArchivo), "UTF-8")) {
            while (scan.hasNextLine()) {
                String linea = scan.nextLine();
                if (!linea.isBlank()) {
                    registros.add(linea.split(SEPARADOR, -1));
                }
            }
            if (scan.ioException() != null) {
                throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo);
            }
        } catch (FileNotFoundException exception) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo);
        }
        return registros;
    }
}