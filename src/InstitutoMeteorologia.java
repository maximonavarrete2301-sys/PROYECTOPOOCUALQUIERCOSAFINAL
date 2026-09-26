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
        if (regionEncontrada == null) {
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
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");

            if (partes[0].equals(codigoEstacion)) {
                return estacion.instalaSensor(cod, marca, modelo, tipo);
            }
        }
        return false;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor,
                                    String codEstacion, String codSensor) {
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");

            if (partes[0].equals(codEstacion)) {
                return estacion.registraMedicion(fechaHora, valor, codSensor);
            }
        }
        return false;
    }

    public String[][] listaRegiones() {
        if (regiones.isEmpty()) {
            return new String[0][0];
        }
        String[][] datos = new String[regiones.size()][4];

        for (int i = 0; i < regiones.size(); i++) {
            Region region = regiones.get(i);

            datos[i][0] = String.valueOf(region.getCodigo());

            datos[i][1] = region.getNombre();

            datos[i][2] = String.valueOf(region.getComunas().length);

            datos[i][3] = String.valueOf(region.getCantidadEstaciones());

        }
        return datos;
    }

    public String[][] listaComunas() {

        int cantidadComunas = 0;
        for (Region region : regiones) {
            cantidadComunas += region.getComunas().length;
        }
        if (cantidadComunas == 0) {
            return new String[0][0];
        }

        String[][] datos = new String[cantidadComunas][5];
        int fila = 0;
        for (Region region : regiones) {
            Comuna[] comunas = region.getComunas();
            for (Comuna comuna : comunas) {
                datos[fila][0] = String.valueOf(comuna.getCodigo());

                datos[fila][1] = comuna.getNombre();

                datos[fila][2] = comuna.getRegion().getNombre();

                datos[fila][3] = String.valueOf(comuna.getCantidadEstaciones());

                datos[fila][4] = String.valueOf(comuna.getCantidadEstacionesActivas());
                fila++;
            }
        }
        return datos;
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        Region regionEncontrada = null;
        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {
                regionEncontrada = region;
            }
        }
        if (regionEncontrada == null) {
            return new String[0][0];
        }
        Comuna comuna = regionEncontrada.findComunaById(codigoComuna);

        if (comuna == null) {
            return new String[0][0];

        }
        ArrayList<String[]> filas = new ArrayList<>();
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");
            String codigoEstacion = partes[0];
            if (comuna.findEstacionById(codigoEstacion) != null) {
                String[] datosEstacion = new String[5];
                datosEstacion[0] = partes[0];
                datosEstacion[1] = partes[1];
                String ubicacion = "";

                for (int i = 2; i < partes.length - 2; i++) {
                    if (!ubicacion.isEmpty()) {
                        ubicacion += "; ";
                    }
                    ubicacion += partes[i];
                }
                datosEstacion[2] = ubicacion;
                datosEstacion[3] = partes[partes.length - 2];
                datosEstacion[4] = partes[partes.length - 1];
                filas.add(datosEstacion);
            }
        }
        if (filas.isEmpty()) {
            return new String[0][0];
        }
        String[][] datos = new String[filas.size()][5];

        for (int i = 0; i < filas.size(); i++) {
            datos[i] = filas.get(i);
        }
        return datos;
    }

    public String[][] listaSensores(String codigoEstacion) {
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");
            if (partes[0].equals(codigoEstacion)) {
                return estacion.getResumenSensores();
            }
        }
        return new String[0][0];
    }

    public String[][] listaMediciones(String codEstacion, String codSensor,
                                      LocalDateTime inicio, LocalDateTime fin) {
        for (EstacionMeteorologica estacion : estaciones) {
            String[] partes = estacion.toString().split("; ");
            if (partes[0].equals(codEstacion)) {
                return estacion.getMedicionesSensorBetween(codSensor, inicio, fin);
            }
        }
        return new String[0][0];
    }
}
