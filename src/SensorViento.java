public class SensorViento extends Sensor {

    public SensorViento(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);

    }

    @Override
    public String getUnidad() { return "km/h"; }

    @Override
    public boolean esValorAdmisible(float valor) { return valor >= 0.0f && valor <= 250.0f; }
}
