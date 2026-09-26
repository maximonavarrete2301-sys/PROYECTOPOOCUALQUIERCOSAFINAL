import java.util.ArrayList;

public class Region {
    private int codigo;
    private String nombre;
    private ArrayList<Comuna> comunas;

    public Region(int cod, String nom){
        codigo = cod;
        nombre = nom;
        this.comunas = new ArrayList<>();
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public boolean addComuna(int cod, String nom){
        for(Comuna comuna : comunas){
            if(comuna.getCodigo() == codigo || comuna.getNombre().equalsIgnoreCase(nombre)){
                return false;
            }
        }
        Comuna nvaComuna = new Comuna(codigo, nombre, this);
        comunas.add(nvaComuna);
        return true;
    }

    public Comuna findComunaById(int codigo){
        for(Comuna comuna : comunas){
            if(comuna.getCodigo() == codigo){
                return comuna;
            }
        }
        return null;
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int CantidadEstaciones(){
        int cantidad = 0;
        for(Comuna comuna : comunas){
            cantidad += comuna.getCantidadEstaciones();
        }
        return cantidad;
    }
}
