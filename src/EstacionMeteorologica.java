import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    private Comuna comuna;
    private ArrayList<Sensor> sensores;

    public EstacionMeteorologica(String cod, String nombre, float lon, float lat, float alt, Comuna comuna){
        codigo = cod;
        this.nombre = nombre;
        longitud = lon;
        latitud = lat;
        altitud = alt;
        this.comuna = comuna;
        estado = Estado.ACTIVO;
        this.sensores = new ArrayList<>();
    }

    public String getCodigo(){
        return codigo;
    }

    public Estado getEstado(){
        return estado;
    }

    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo){
        for(Sensor sensor : sensores){
            if(sensor.getCodigo().equals(codigo)){
                return false;
            }
            if(sensor.getEstado() == Estado.ACTIVO){
                if (tipo == TipoSensor.HUMEDAD && sensor instanceof SensorHumedad){
                    return false;
                }
                if(tipo == TipoSensor.TEMPERATURA && sensor instanceof SensorTemperatura){
                    return false;
                }
                if(tipo == TipoSensor.PRESION && sensor instanceof SensorPresion){
                    return false;
                }
                if(tipo == TipoSensor.VIENTO && sensor instanceof SensorViento){
                    return false;
                }
                if(tipo == TipoSensor.PRECIPITACION && sensor instanceof SensorPrecipitacion){
                    return false;
                }
            }
        }
        Sensor nvoSensor;
        switch(tipo){
            case HUMEDAD:
                nvoSensor = new SensorHumedad(codigo, marca, modelo, this);
                break;
            case TEMPERATURA:
                nvoSensor = new SensorTemperatura(codigo, marca, modelo, this);
                break;
            case PRESION:
                nvoSensor = new SensorPresion(codigo, marca, modelo, this);
                break;
            case VIENTO:
                nvoSensor = new SensorViento(codigo, marca, modelo, this);
                break;
            case PRECIPITACION:
                nvoSensor = new SensorPrecipitacion(codigo, marca, modelo, this);
                break;
            default:
                return false;
        }
        sensores.add(nvoSensor);
        return true;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor){
        if(estado == Estado.ACTIVO){
            for(Sensor sensor : sensores){
                if(sensor.getCodigo().equals(codigoSensor)){
                    return sensor.addMedicion(fechaHora, valor);
                }
            }
        }
        return false;
    }

    public String toString(){
        return codigo + ", " + nombre + ", " + longitud + ", " + latitud + ", " + altitud + ", " + estado + ", " + sensores.size();
    }

    public String[][] getResumenSensores(){
        String[][] resumenSensor = new String[sensores.size()][7];
        for(int i = 0; i < sensores.size(); i++){
            Sensor sensor = sensores.get(i);

            resumenSensor[i][0] = sensor.getCodigo();
            resumenSensor[i][2] = sensor.getMarca();
            resumenSensor[i][3] = sensor.getModelo();
            resumenSensor[i][4] = sensor.getEstado().toString();
            resumenSensor[i][5] = sensor.getUnidad();

            Medicion ultima = sensor.getLastMedicion();

            if(ultima == null){
                resumenSensor[i][5] = "Sin medición";
            }else{
                resumenSensor[i][5] = ultima.toString();
            }
        }
        return resumenSensor;
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/aaaa HH:mm");
        for(Sensor sensor : sensores){
            if(sensor.getCodigo().equals(codigoSensor)){
                Medicion[] mediciones = sensor.getMedicionBetween(inicio, fin);
                String[][] datos = new String[mediciones.length][2];
                for(int i = 0; i < mediciones.length; i++){
                    datos[i][0] = mediciones[i].getFechaHora().format(formatter);
                    datos[i][1] = String.valueOf(mediciones[i].getValor());
                    datos[i][2] = sensor.getUnidad();
                }
                return datos;
            }
        }
        return new String[0][0];
    }
}


