package controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PersistenciaTexto {
    public static final String separador = ";";
    private static PersistenciaTexto instancia;

    private PersistenciaTexto(){}

    public static PersistenciaTexto getInstance(){
        if(instancia == null){
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }

    public List<String[]> leeRegistros(String nombreArchivo) throws MeteorologiaException {
        List<String[]> registros = new ArrayList<>();
        Scanner archivo = null;
        try {
            archivo = new Scanner(new File(nombreArchivo), "UTF-8");
            while (archivo.hasNextLine()) {
                String linea = archivo.nextLine();
                if (!linea.trim().isEmpty()) {
                    registros.add(linea.split(separador, -1));
                }
            }
        } catch (FileNotFoundException exception) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo);
        } catch (RuntimeException exception) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo);
        } finally {
            if (archivo != null) {
                archivo.close();
            }
        }
        return registros;
    }

    public void escribeRegistros(String nombreArchivo, String[] lineas) throws MeteorologiaException {
        File archivo = new File(nombreArchivo);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        PrintStream salida = null;
        try {
            salida = new PrintStream(archivo, "UTF-8");
            for (String linea : lineas) {
                salida.println(linea);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException exception) {
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo);
        } finally {
            if (salida != null) {
                salida.close();
            }
        }
    }
}
