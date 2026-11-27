package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import controlador.MeteorologiaException;

public class EstacionMeteorologica implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

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

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return "(" + latitud + "; " + longitud + "; " + altitud + ")";
    }

    public Estado getEstado() {
        return estado;
    }

    public Comuna getComuna() {
        return comuna;
    }

    public Sensor[] getSensores() {
        return sensores.toArray(new Sensor[0]);
    }

    public Optional<Sensor> findSensorById(String codigo) {
        return sensores.stream()
                .filter(s -> s.getCodigo().equals(codigo))
                .findFirst();
    }

    public void instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        if (sensores.stream().anyMatch(s -> s.getCodigo().equals(codigo))) {
            throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor con el código " + codigo + ".");
        }
        Optional<Sensor> activoMismoTipo = sensores.stream()
                .filter(s -> s.getEstado() == Estado.ACTIVO && s.getTipo() == tipo)
                .findFirst();
        if (activoMismoTipo.isPresent()) {
            throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor ACTIVO de tipo "
                    + tipo + " (" + activoMismoTipo.get().getCodigo() + ").");
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
                break;
        }
        sensores.add(nvoSensor);
    }

    public void cambiaEstadoSensor(String codigoSensor, Estado estado) throws MeteorologiaException {
        Sensor sensor = findSensorById(codigoSensor)
                .orElseThrow(() -> new MeteorologiaException(
                        "No existe el sensor " + codigoSensor + " en la estación " + codigo + "."));
        if (sensor.getEstado() == estado) {
            throw new MeteorologiaException("El sensor " + codigoSensor + " ya se encuentra en estado " + estado + ".");
        }
        if (estado == Estado.ACTIVO) {
            Optional<Sensor> otroActivo = sensores.stream()
                    .filter(s -> s.getEstado() == Estado.ACTIVO && s.getTipo() == sensor.getTipo())
                    .findFirst();
            if (otroActivo.isPresent()) {
                throw new MeteorologiaException("No es posible activar el sensor " + codigoSensor
                        + ": la estación ya tiene activo el sensor " + otroActivo.get().getCodigo()
                        + " de tipo " + sensor.getTipo() + ".");
            }
        }
        sensor.setEstado(estado);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException {
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación " + codigo + " no está activa.");
        }
        Sensor sensor = findSensorById(codigoSensor)
                .orElseThrow(() -> new MeteorologiaException(
                        "No existe el sensor " + codigoSensor + " en la estación " + codigo + "."));
        sensor.addMedicion(fechaHora, valor);

        if (sensor instanceof Alertable) {
            Alertable alertable = (Alertable) sensor;
            Optional<NivelAlerta> nivel = alertable.evaluaAlerta(valor);
            if (nivel.isPresent()) {
                alertas.add(new AlertaMeteorologica(fechaHora, valor, nivel.get(),
                        alertable.getFenomeno(valor), sensor));
            }
        }
    }

    public String[][] getResumenSensores() {
        return sensores.stream()
                .map(s -> new String[]{
                        s.getCodigo(),
                        s.getTipo().toString(),
                        s.getMarca(),
                        s.getModelo(),
                        s.getUnidad(),
                        s.getEstado().toString(),
                        textoUltimaMedicion(s)})
                .toArray(String[][]::new);
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin) {
        return findSensorById(codigoSensor)
                .map(sensor -> Arrays.stream(sensor.getMedicionesBetween(inicio, fin))
                        .map(m -> new String[]{
                                m.getFechaHora().format(FORMATO_FECHA),
                                m.getFechaHora().format(FORMATO_HORA),
                                String.valueOf(m.getValor()),
                                sensor.getUnidad()})
                        .toArray(String[][]::new))
                .orElse(new String[0][0]);
    }

    public AlertaMeteorologica[] getAlertasBetween(LocalDateTime inicio, LocalDateTime fin) {
        return alertas.stream()
                .filter(a -> !a.getFechaHora().isBefore(inicio) && !a.getFechaHora().isAfter(fin))
                .sorted(Comparator.comparing(AlertaMeteorologica::getFechaHora))
                .toArray(AlertaMeteorologica[]::new);
    }

    @Override
    public String toString() {
        long sensoresOperativos = sensores.stream()
                .filter(s -> s.getEstado() == Estado.ACTIVO)
                .count();
        return codigo + "; " + nombre + "; " + getPosicion() + ";" + estado + "; " + sensoresOperativos;
    }

    private String textoUltimaMedicion(Sensor sensor) {
        Medicion[] mediciones = sensor.getMediciones();
        if (mediciones.length == 0) {
            return "";
        }
        Medicion ultima = mediciones[mediciones.length - 1];
        return ultima.getFechaHora().format(FORMATO_FECHA_HORA) + " " + ultima.getValor() + " " + sensor.getUnidad();
    }
}

