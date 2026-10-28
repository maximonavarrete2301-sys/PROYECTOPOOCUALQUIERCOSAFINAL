package modelo;

import controlador.MeteorologiaException;

import java.util.ArrayList;
import java.util.Optional;

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

    public void addComuna(int cod, String nom) throws MeteorologiaException{
        for(Comuna comuna : comunas){
            if(comuna.getCodigo() == cod){
                throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el código " + cod + ".");
            }
            if (comuna.getNombre().equalsIgnoreCase(nom)){
                throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el nombre " + nom + ".");
            }
        }
        Comuna nvaComuna = new Comuna(cod, nom, this);
        comunas.add(nvaComuna);
    }

    public Optional<Comuna> findComunaById(int codigo){
        for(Comuna comuna : comunas){
            if(comuna.getCodigo() == codigo){
                return Optional.of(comuna);
            }
        }
        return Optional.empty();
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones(){
        int cantidad = 0;
        for(Comuna comuna : comunas){
            cantidad += comuna.getCantidadEstaciones();
        }
        return cantidad;
    }
}
