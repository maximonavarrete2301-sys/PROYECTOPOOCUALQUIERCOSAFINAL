import java.util.ArrayList;

public class Region {
    private int codigo;
    private String nombre;
    private ArrayList<Comuna> comunas;

    public Region(int cod, String nom){
        codigo = cod;
        nombre = nom;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public boolean addComuna(int cod, String nom){

    }

    public Comuna findComunaByld(int codigo){

    }

    public Comuna getComunas(){

    }

    public int CantidadEstaciones(){

    }
}
