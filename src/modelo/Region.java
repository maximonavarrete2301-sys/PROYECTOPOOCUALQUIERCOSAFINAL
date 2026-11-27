package modelo;

import controlador.MeteorologiaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class Region implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public void addComuna(int cod, String nom) throws MeteorologiaException{
        if(comunas.stream().anyMatch(c -> c.getCodigo() == cod)){
            throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el código " + cod + ".");
        }
        if (comunas.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nom))){
            throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el nombre " + nom + ".");
        }
        Comuna nvaComuna = new Comuna(cod, nom, this);
        comunas.add(nvaComuna);
    }

    public Optional<Comuna> findComunaById(int codigo){
        return comunas.stream().filter(c -> c.getCodigo() == codigo).findFirst();
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones(){
       return comunas.stream().mapToInt(Comuna :: getCantidadEstaciones).sum();
    }
}
