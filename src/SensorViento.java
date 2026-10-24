import java.util.Optional;

public class SensorViento extends Sensor implements Alertable{

    public SensorViento(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public TipoSensor getTipo(){
        return TipoSensor.VIENTO;
    }

    @Override
    public String getUnidad(){
        return "km/h";
    }

    @Override
    public boolean esValorAdmisible(float valor){
        // El viento no puede ser negativo, el rango es de 0 a 250 km/h
        return valor >= 0.0f && valor <= 250.0f;
    }

    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor){
        if (valor >= 120.0f){
            return Optional.of(NivelAlerta.ALARMA);
        } else if (valor >= 80.0f){
            return Optional.of(NivelAlerta.ALERTA);
        } else if (valor >= 60.0f){
            return Optional.of(NivelAlerta.AVISO);
        }
        return Optional.empty(); //vnto normal o leve
    }


    @Override
    public String getFenomeno(float valor){
        if ( valor >= 120.0f){
            return "Viento huracanado";
        } else if (valor >= 60.0f){
            return "Temporal de viento";
        }
        return "Normal";
    }
}