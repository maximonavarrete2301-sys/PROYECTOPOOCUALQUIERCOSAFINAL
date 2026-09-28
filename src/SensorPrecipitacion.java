public class SensorPrecipitacion extends Sensor {

    public SensorPrecipitacion(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);

    }

    @Override
    public String getUnidad() { return "mm"; }

    @Override
    public boolean esValorAdmisible(float valor) { return valor >= 0.0f && valor <= 500.0f; }
}
