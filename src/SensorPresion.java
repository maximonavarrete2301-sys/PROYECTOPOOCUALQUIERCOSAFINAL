public class SensorPresion extends Sensor {

    public SensorPresion(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);

    }

    @Override
    public String getUnidad() { return "hPa"; }

    @Override
    public boolean esValorAdmisible(float valor) { return valor >= 800.0f && valor <= 1100.0f; }
}
