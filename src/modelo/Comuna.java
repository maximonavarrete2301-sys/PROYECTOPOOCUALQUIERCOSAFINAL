package modelo;

import java.util.ArrayList;
import java.util.Optional;

public class Comuna {
    private int codigo;
    private String nombre;
    private Region region;
    private ArrayList<EstacionMeteorologica> estaciones;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
        this.estaciones = new ArrayList<>();
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public Region getRegion(){
        return region;
    }

    public void addEstacion(EstacionMeteorologica estacion){
        estaciones.add(estacion);
    }

    public Optional<EstacionMeteorologica> findEstacionById(String codigo){
        for(EstacionMeteorologica estacion : estaciones){
            if (estacion.getCodigo().equals(codigo)) {
                return Optional.of(estacion);
            }
        }
        return Optional.empty();
    }

    public EstacionMeteorologica[] getEstaciones(){
        return estaciones.toArray(new EstacionMeteorologica[0]);
    }

    public int getCantidadEstaciones(){
        return estaciones.size();
    }

    public int getCantidadEstacionesActivas(){
        int cantEstacionesAct = 0;
        for(EstacionMeteorologica estacion : estaciones){
            if(estacion.getEstado() == Estado.ACTIVO){
                cantEstacionesAct++;
            }
        }
        return cantEstacionesAct;
    }
}
