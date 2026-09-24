import java.time.LocalDateTime;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    private Comuna comuna;

    public EstacionMeteorologica(String cod, String nombre, float lon, float lat, float alt, Comuna comuna){
        codigo = cod;
        this.nombre = nombre;
        longitud = lon;
        latitud = lat;
        altitud = alt;
        this.comuna = comuna;
        estado = Estado.ACTIVO;
    }

    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo){

    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor){

    }

    public String toString(){

    }

    public String[][] getResumenSensores(){

    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin){

    }
}


