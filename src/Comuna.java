import java.util.ArrayList;

public class Comuna {
    private int codigo;
    private String nombre;
    private Region region;
    private ArrayList<EstacionMeteorologica> estaciones;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public void addEstacion(EstacionMeteorologica estacion){
        estaciones.add(estacion);
    }

    public EstacionMeteorologica findEstacionByld(String codigo){

    }

    public Region getRegion(){
        return region;
    }

    public int getCantidadEstaciones(){
        return //cantidadEstaciones
    }

    public int getCantidadEstacionesActivas(){

    }
}
