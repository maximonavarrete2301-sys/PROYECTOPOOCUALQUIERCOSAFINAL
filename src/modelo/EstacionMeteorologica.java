package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import controlador.MeteorologiaException;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    private Comuna comuna;
    private ArrayList<Sensor> sensores;
    private ArrayList<AlertaMeteorologica> alertas;

    public EstacionMeteorologica(String cod, String nombre, float lon, float lat, float alt, Comuna comuna) {
        codigo = cod;
        this.nombre = nombre;
        longitud = lon;
        latitud = lat;
        altitud = alt;
        this.comuna = comuna;
        estado = Estado.ACTIVO;
        this.sensores = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }

    public String getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public String getPosicion(){
        return "(" + latitud + ";" + longitud + ";" + altitud + ")";
    }

    public Estado getEstado() {
        return estado;
    }

    public Comuna getComuna() {
        return comuna;
    }

    public void instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equals(codigo)) {
                throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor con el código " + codigo + ".");
            }
            if (sensor.getTipo() == tipo && sensor.getEstado() == Estado.ACTIVO) {
                throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor ACTIVO de tipo " + tipo + " (" + sensor.getCodigo() + ").");
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
            default:
                nvoSensor = new SensorPrecipitacion(codigo, marca, modelo, this);
        }
        sensores.add(nvoSensor);
    }

    public void cambiaEstadoSensor(String codigoSensor, Estado estado) throws MeteorologiaException{
        Optional<Sensor> sensorOptional = findSensorById(codigoSensor);
        if (sensorOptional.isEmpty()){
            throw new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + codigo + ".");
        }
        Sensor sensor = sensorOptional.get();
        if (sensor.getEstado() == estado) {
            throw new MeteorologiaException("El sensor " + codigoSensor + " ya se encuentra en estado " + estado + ".");
        }
        if (estado == Estado.ACTIVO) {
            for (Sensor otro : sensores) {
                if (otro.getEstado() == Estado.ACTIVO && otro.getTipo() == sensor.getTipo()) {
                    throw new MeteorologiaException("No es posible activar el sensor " + codigoSensor + ": la estación ya tiene activo el sensor " + otro.getCodigo() + " de tipo " + otro.getTipo() + ".");
                }
            }
        }
        sensor.setEstado(estado);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + codigo + " no está activa.");
        }
        Optional<Sensor> sensorOptional = findSensorById(codigoSensor);
        if (sensorOptional.isEmpty()) {
            throw new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + codigo + ".");
        }
        Sensor sensor = sensorOptional.get();
        sensor.addMedicion(fechaHora, valor);

        if (sensor instanceof Alertable) {
            Alertable alertable = (Alertable) sensor;
            Optional<NivelAlerta> nivel = alertable.evaluaAlerta(valor);
            if (nivel.isPresent()) {
                AlertaMeteorologica alerta = new AlertaMeteorologica(fechaHora, valor, nivel.get(), alertable.getFenomeno(valor), sensor);
                int posicion = alertas.size();
                while (posicion > 0 && alertas.get(posicion - 1).getFechaHora().isAfter(fechaHora)) {
                    posicion--;
                }
                alertas.add(posicion, alerta);
            }
        }
    }

    public Sensor[] getSensores(){
        return sensores.toArray(new Sensor[0]);
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
            resumenSensor[i][1] = sensor.getTipo().toString();
            resumenSensor[i][2] = sensor.getMarca();
            resumenSensor[i][3] = sensor.getModelo();
            resumenSensor[i][4] = sensor.getUnidad();
            resumenSensor[i][5] = sensor.getEstado().toString();
            Medicion ultima = sensor.getLastMedicion();
            if (ultima == null) {
                resumenSensor[i][6] = "";
            } else {
                resumenSensor[i][6] =
                        ultima.getFechaHora().format(formato) + " " + ultima.getValor() + " " + sensor.getUnidad();
            }
        }
        return resumenSensor;
    }
    public String[][] getMedicionesSensorBetween (String codigoSensor, LocalDateTime inicio, LocalDateTime fin){
        Optional<Sensor> sensorOpt = findSensorById(codigoSensor);
        if (sensorOpt.isEmpty()) {
            return new String[0][0];
        }
        Sensor sensor = sensorOpt.get();
        Medicion[] mediciones = sensor.getMedicionesBetween(inicio, fin);
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

    public AlertaMeteorologica[] getAlertasBetween(LocalDateTime inicio, LocalDateTime fin){
        ArrayList<AlertaMeteorologica> resultado = new ArrayList<>();
        for (AlertaMeteorologica alerta : alertas) {
            if (!alerta.getFechaHora().isBefore(inicio) && !alerta.getFechaHora().isAfter(fin)) {
                resultado.add(alerta);
            }
        }
        return resultado.toArray(new AlertaMeteorologica[0]);
    }

    public Optional<Sensor> findSensorById(String codigo){
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equals(codigo)) {
                return Optional.of(sensor);
            }
        }
        return Optional.empty();
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

}


