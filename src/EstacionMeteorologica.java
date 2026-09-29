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

    public EstacionMeteorologica(String cod, String nombre, float lon, float lat, float alt, Comuna comuna) {
        codigo = cod;
        this.nombre = nombre;
        longitud = lon;
        latitud = lat;
        altitud = alt;
        this.comuna = comuna;
        estado = Estado.ACTIVO;
        this.sensores = new ArrayList<>();
    }

    public boolean instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) {
        if (estado != Estado.ACTIVO) {
            return false;
        }
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equals(codigo)) {
                return false;
            }
            if (sensor.getEstado() == Estado.ACTIVO) {
                if (tipo == TipoSensor.HUMEDAD && sensor instanceof SensorHumedad) {
                    return false;
                }
                if (tipo == TipoSensor.TEMPERATURA && sensor instanceof SensorTemperatura) {
                    return false;
                }
                if (tipo == TipoSensor.PRESION && sensor instanceof SensorPresion) {
                    return false;
                }
                if (tipo == TipoSensor.VIENTO && sensor instanceof SensorViento) {
                    return false;
                }
                if (tipo == TipoSensor.PRECIPITACION && sensor instanceof SensorPrecipitacion) {
                    return false;
                }
            }
        }
        Sensor nvoSensor;
        switch (tipo) {
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

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) {
        if (estado == Estado.ACTIVO) {
            for (Sensor sensor : sensores) {
                if (sensor.getCodigo().equals(codigoSensor)) {
                    return sensor.addMedicion(fechaHora, valor);
                }
            }
        }
        return false;
    }

    public String toString() {
        int sensoresOperativos = 0;
        for (Sensor sensor : sensores) {
            if (sensor.getEstado() == Estado.ACTIVO) {
                sensoresOperativos++;
            }
        }
        return codigo + "; " + nombre + "; " + "(" + latitud + "; " + longitud + "; " + altitud + ")" + ";" + estado + "; " + sensoresOperativos;
    }

    public String[][] getResumenSensores() {
        if (sensores.isEmpty()) {
            return new String[0][0];
        }

        String[][] resumenSensor = new String[sensores.size()][7];

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < sensores.size(); i++) {
            Sensor sensor = sensores.get(i);
            resumenSensor[i][0] = sensor.getCodigo();
            resumenSensor[i][1] = obtenerTipoSensor(sensor);
            resumenSensor[i][2] = sensor.getMarca();
            resumenSensor[i][3] = sensor.getModelo();
            resumenSensor[i][4] = sensor.getUnidad();
            resumenSensor[i][5] = sensor.getEstado().toString();
            Medicion ultima = sensor.getLastMedicion();
            if (ultima == null) {
                resumenSensor[i][6] = "";
            } else {
                resumenSensor[i][6] =
                        ultima.getFechaHora().format(formato)
                                + " "
                                + ultima.getValor()
                                + " "
                                + sensor.getUnidad();
            }
        }
        return resumenSensor;
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin){
        for(Sensor sensor : sensores){
            if(sensor.getCodigo().equals(codigoSensor)){
                Medicion[] mediciones =
                        sensor.getMedicionesBetween(inicio, fin);
                if(mediciones.length == 0){
                    return new String[0][0];
                }
                String[][] datos = new String[mediciones.length][4];

                DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

                for(int i = 0; i < mediciones.length; i++){
                    datos[i][0] = mediciones[i].getFechaHora().format(formatoFecha);
                    datos[i][1] = mediciones[i].getFechaHora().format(formatoHora);
                    datos[i][2] = String.valueOf(mediciones[i].getValor());
                    datos[i][3] = sensor.getUnidad();
                }
                return datos;
            }
        }
        return new String[0][0];
    }
    private String obtenerTipoSensor(Sensor sensor) {
        if (sensor instanceof SensorTemperatura) {
            return "TEMPERATURA";
        }
        if (sensor instanceof SensorHumedad) {
            return "HUMEDAD";
        }
        if (sensor instanceof SensorPresion) {
            return "PRESION";
        }
        if (sensor instanceof SensorViento) {
            return "VIENTO";
        }
        if (sensor instanceof SensorPrecipitacion) {
            return "PRECIPITACION";
        }
        return "";
    }
}


