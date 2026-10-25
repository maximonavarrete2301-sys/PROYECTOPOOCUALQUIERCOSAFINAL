package controlador;//Maximo Navarrete Fernandez
import javax.swing.plaf.synth.Region;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instancia ;
    private ArrayList<Region> regiones = new ArrayList<>();
    private ArrayList<EstacionMeteorologica> estaciones = new ArrayList<>();
    private InstitutoMeteorologia(){
    }
    public static InstitutoMeteorologia getInstance() {
        if (instancia == null) {
            instancia = new InstitutoMeteorologia();
        }
        return instancia
    }

    public void creaRegion(int codigo, String nombre)
    throws MeteorologiaException {
        if (findRegion(codigo).isPresent()) {
            throw new MeteorologiaException("Ya existe una región con el codigo " + codigo );
        }
        for (Region region : regiones) {
            if (region.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una región con el nomnbre " + nombre);
            }
        }
        regiones.add(new Region(codigo, nombre));
    }

    public boolean creaComuna(int codigo, String nombre, int codigoRegion)
        throws MeteorologiaException {
            Optional<Region> region = findRegion(codigoRegion);
            if (region.isEmpty() ){
                throw new MeteorologiaException("No existe una región con el codigo " + codigoRegion);
            }
            region.get().addComuna(codigo, nombre);
        }

    public boolean creaEstacion(String cod, String nombre, float lon,
                                float lat, float alt, int codRegion, int codComuna)
            throws MeteorologiaException {
        if (findEstacion(cod).isPresent()) {
            throw new MeteorologiaException("Ya existe una estación con el codigo " + cod);
        }
        Optional<Region> region = findRegion(codRegion);
        if (region.isEmpty()) {
            throw new MeteorologiaException ("No existe una región con el codigo " + codRegion);
        }

        Optional<Comuna> comuna = findComuna(codRegion, codComuna);
        if (comuna.isEmpty()) {
            throw new MeteorologiaException("No existe una comuna con el codigo " + codComuna
            " en la region " + region.get().getNombre());
        }

        EstacionMeteorologica estacion =
                new EstacionMeteorologica(cod, nombre, lon, lat, alt, comuna.get());
        comuna.get().addEstacion(estacion);
        estaciones.add(estacion);
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

    private Optional<Region> findRegion(int codigo) {
        for (Region region : regiones) {
            if (region.getCodigo() == codigo) {
                return Optional.of(region);
            }
        }

        return Optional.empty();
    }

    private Optional<EstacionMeteorologica> findEstacion(String codigo) {

        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equals(codigo)) {
                return Optional.of(estacion);
            }
        }

        return Optional.empty();
    }

    private Optional<Comuna> findComuna(int codigoRegion, int codigoComuna) {

        Optional<Region> region = findRegion(codigoRegion);
        if (region.isEmpty()) {
            return Optional.empty();
        }
        return region.get().findComunaById(codigoComuna);
    }

    private Optional<Sensor> findSensor(String codigoEstacion, String codigoSensor) {

        Optional<EstacionMeteorologica> estacion = findEstacion(codigoEstacion);

        if (estacion.isEmpty()) {
            return Optional.empty();
        }
        return estacion.get().findSensorById(codigoSensor);
    }

}
