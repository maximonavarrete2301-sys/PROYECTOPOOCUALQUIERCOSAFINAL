package controlador;

import java.io.*;

public class PersistenciaObjetos {
    private static final String CARPETA = "datos";

    private PersistenciaObjetos(){
    }

    public static void escribeObjeto(String nombreArchivo, Object objeto) throws MeteorologiaException{
        File carpeta = new File(CARPETA);
        if(!carpeta.exists()){
            carpeta.mkdirs();
        }
        File archivo = new File(carpeta, nombreArchivo);
        try(ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo))){
            salida.writeObject(objeto);
        }catch(IOException exception){
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo);
        }
    }

    public static Object leeObjeto(String nombreArchivo) throws MeteorologiaException{
        File archivo = new File(CARPETA, nombreArchivo);
        if(!archivo.exists()){
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo);
        }
        try(ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))){
            return entrada.readObject();
        }catch (IOException | ClassNotFoundException exception){
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo);
        }
    }
}
