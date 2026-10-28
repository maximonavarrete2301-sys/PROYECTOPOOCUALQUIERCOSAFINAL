package modelo;

import java.util.Optional;

public class SensorTemperatura extends Sensor implements Alertable {

    public SensorTemperatura(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.TEMPERATURA;
    }

    @Override
    public String getUnidad() {
        return "°C";
    }

    @Override
    public boolean esValorAdmisible(float valor) {
        return valor >= -80.0f && valor <= 60.0f;
    }

    public float convertirCelsiusAFahrenheit(float celsius) {
        return (celsius * 9.0f / 5.0f) + 32.0f;
    }

    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {

        if (valor <= -5.0f || valor >= 38.0f) {
            return Optional.of(NivelAlerta.ALARMA);

        } else if (valor <= -2.0f || valor >= 35.0f) {
            return Optional.of(NivelAlerta.ALERTA);

        } else if (valor <= 0.0f || valor >= 32.0f) {
            return Optional.of(NivelAlerta.AVISO);
        }

        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {

        if (valor <= 0.0f) {
            return "Helada";

        } else if (valor >= 32.0f) {
            return "Calor extremo";
        }

        return "";
    }
}