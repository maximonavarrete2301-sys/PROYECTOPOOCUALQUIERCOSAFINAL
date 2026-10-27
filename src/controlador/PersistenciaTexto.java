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
        File archivo = new File("datos", nombreArchivo);
        Scanner lector = null;

        try {
            lector = new Scanner(archivo, "UTF-8");
            while (lector.hasNextLine()) {
                String linea = lector.nextLine();
                if (!linea.trim().isEmpty()) {
                    String[] campos = linea.split(SEPARADOR, -1);
                    registros.add(campos);
                }
            }
        } catch (FileNotFoundException e) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");

        } catch (RuntimeException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");

        } finally {
            if (lector != null) {
                lector.close();
            }
        }
        return registros;
    }

    public static void escribeRegistros(String nombreArchivo, List<String> lineas) throws MeteorologiaException {
        File carpeta = new File("datos");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        File archivo = new File(carpeta, nombreArchivo);
        PrintStream salida = null;

        try {
            salida = new PrintStream(archivo, "UTF-8");
            for (String linea : lineas) {
                salida.println(linea);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo + ".");
        } finally {
            if (salida != null) {
                salida.close();
            }
        }
    }
}