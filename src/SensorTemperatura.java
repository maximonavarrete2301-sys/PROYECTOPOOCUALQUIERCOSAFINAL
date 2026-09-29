public class SensorTemperatura extends Sensor {

    public SensorTemperatura(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public String getUnidad() {
        return "°C";
    }

    @Override
    public boolean esValorAdmisible(float valor) {

        //temperatureichon ambienteichon
        return valor >= -80.0f && valor <= 60.0f;
    }

    public float convertirCelciusAFahrenheit(float valor) {
        return (valor * 9 / 5) + 32;
    }
}