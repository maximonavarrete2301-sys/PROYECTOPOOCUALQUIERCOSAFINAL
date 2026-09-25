//Maximo Navarrete Fernandez
import java.time.LocalDateTime;
import java.util.ArrayList;

public class InstitutoMeteorologia {
    private ArrayList<Region> regiones = new ArrayList<>();
    private ArrayList<EstacionMeteorologica> estaciones = new ArrayList<>();

    public boolean creaRegion(int codigo, String nombre) {
        for (Region region : regiones) {
            if (region.getCodigo() == codigo ||
                    region.getNombre().equalsIgnoreCase(nombre)) {
                return false;
            }
        }
        Region region = new Region(codigo, nombre);
        regiones.add(region);
        return true;
    }

    public boolean creaComuna(int codigo, String nombre, int codigoRegion) {
        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {
                return region.addComuna(codigo, nombre);
            }
        }
        return false;
    }

    public boolean creaEstacion(String cod, String nombre, float lon,
                                float lat, float alt, int codRegion, int codComuna) {
        Region regionEncontrada = null;
        for (Region region : regiones) {
            if (region.getCodigo() == codRegion) {
                regionEncontrada = region;
            }
        }
        if (regionEncontrada==null ){
            return false;
        }
        Comuna comuna = regionEncontrada.findComunaById(codComuna);
        if (comuna == null) {
            return false;
        }
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");
            if (partes[0].equals(cod)) {
                return false;
            }
        }
        EstacionMeteorologica estacion =
                new EstacionMeteorologica(cod, nombre, lon, lat, alt, comuna);
        comuna.addEstacion(estacion);
        estaciones.add(estacion);
        return true;
    }

    public boolean instalaSensor(String cod, String marca, String modelo, TipoSensor tipo,
                                 String codigoEstacion) {
        for (EstacionMeteorologica estacion: estaciones) {
            String[] partes = estacion.toString().split("; ");

            if (partes[0].equals(codigoEstacion)) {
                return estacion.instalaSensor(cod, marca, modelo, tipo);
            }
        }
        return false;
    }

    public boolean registraMedicion (LocalDateTime fechaHora , float valor ,
                                     String codEstacion, String codSensor) {
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");

            if (partes[0].equals(codEstacion)) {
                return estacion.registraMedicion(fechaHora, valor, codSensor);
            }
        }
        return false;
    }



}
