public class SensorHumedad extends Sensor {

    public SensorHumedad(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public TipoSensor getTipo(){
        return TipoSensor.HUMEDAD;
    }

    @Override
    public String getUnidad(){

        return "%";
    }

    @Override
    public boolean esValorAdmisible(float valor){

        //la humedad se mide de 0 a 100
        return valor >= 0.0f && valor <= 100.0f;
    }


}