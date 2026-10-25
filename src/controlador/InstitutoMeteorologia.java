package controlador;//Maximo Navarrete Fernandez
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

    public void creaComuna(int codigo, String nombre, int codigoRegion)
        throws MeteorologiaException {
            Optional<Region> region = findRegion(codigoRegion);
            if (region.isEmpty() ){
                throw new MeteorologiaException("No existe una región con el codigo " + codigoRegion);
            }
            region.get().addComuna(codigo, nombre);
        }

    public void creaEstacion(String cod, String nombre, float lon,
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

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo,
                                 String codigoEstacion)
            throws MeteorologiaException {
        Optional<EstacionMeteorologica> estacion = findEstacion(codigoEstacion);
        if (estacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion);
        }
        estacion.get().instalaSensor(cod, marca, modelo, tipo);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor,
                                    String codEstacion, String codSensor)
            throws MeteorologiaException {
        Optional<EstacionMeteorologica> estacion = findEstacion(codEstacion);
        if (estacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el codigo " + codEstacion);
        }
        estacion.get().registraMedicion(fechaHora, valor , codSensor);
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

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna)
            throws MeteorologiaException {
        Optional<Region> region = findRegion(codigoRegion);

        if (region.isEmpty()) {
            throw new MeteorologiaException("No existe una region con el código " + codigoRegion);
        }
        Optional<Comuna> comuna = findComuna(codigoRegion, codigoComuna);
        if (comuna.isEmpty()){
            throw new MeteorologiaException("No existe una comuna con el codigo " +codigoComuna "en la region "
                    + region.get().getNombre());
        }
        EstacionMeteorologica[] estacionesComuna = comuna.get().getEstaciones();
        if (estacionesComuna.length == 0) {
            return new String[0][0];
        }

        String[][] datos = new String[estacionesComuna.length][5];
        for (int i = 0; i < estacionesComuna.length; i++) {

            EstacionMeteorologica estacion = estacionesComuna[i];

            int sensoresOperativos = 0;
            for (Sensor sensor : estacion.getSensores()) {
                if (sensor.getEstado() == Estado.ACTIVO) {
                    sensoresOperativos++;
                }
            }
            datos[i][0] = estacion.getCodigo();
            datos[i][1] = estacion.getNombre();
            datos[i][2] = estacion.getPosicion();
            datos[i][3] = estacion.getEstado().toString();
            datos[i][4] = String.valueOf(sensoresOperativos);
        }
        return datos;
    }

    public String[][] listaSensores(String codigoEstacion)
            throws MeteorologiaException {
        Optional<EstacionMeteorologica> estacion = findEstacion(codigoEstacion);
        if (estacion.isEmpty() ){
            throw new MetorologiaException("No existe una estacion con el codigo " + codigoEstacion);
        }
        return estacion.get().getResumenSensores();
    }

    public String[][] listaMediciones(String codEstacion, String codSensor,
                                      LocalDateTime inicio, LocalDateTime fin)
            throws MeteorologiaException {
        Optional<EstacionMeteorologica> estacion = findEstacion(codEstacion);

        if (estacion.isEmpty()){
        throw  new MeteorologiaException("No existe una estacion con el codigo " + codEstacion);
        }
        Optional<Sensor> sensor = findSensor(codEstacion, codSensor);
        if (sensor.isEmpty()){
            throw new MeteorologiaException("No existe el sensor " + "codSensor en la estacion" + codEstacion);
        }
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
