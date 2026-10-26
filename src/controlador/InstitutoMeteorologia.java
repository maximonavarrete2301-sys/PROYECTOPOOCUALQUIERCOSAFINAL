package controlador;//Maximo Navarrete Fernandez
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import modelo.AlertaMeteorologica;
import modelo.Comuna;
import modelo.Estado;
import modelo.Medicion;
import modelo.Region;
import modelo.ResumenMediciones;
import modelo.Sensor;
import modelo.TipoSensor;
import modelo.EstacionMeteorologica;
import java.time.format.ResolverStyle;


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
        return instancia;
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
            throw new MeteorologiaException("No existe una comuna con el codigo " + codComuna +
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
            throw new MeteorologiaException("No existe una comuna con el codigo " + codigoComuna + "en la region "
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
            throw new MeteorologiaException("No existe una estacion con el codigo " + codigoEstacion);
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
            throw new MeteorologiaException("No existe el sensor " + codSensor + "en la estacion" + codEstacion);
        }
        validarFecha(inicio, fin);
        return estacion.get().getMedicionesSensorBetween(
                codSensor,
                inicio,
                fin);
    }

    public void cambiaEstadoSensor(String codigoEstacion, String codigoSensor, Estado estado)
            throws MeteorologiaException{
        Optional<EstacionMeteorologica> estacion = findEstacion(codigoEstacion);
        if (estacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estacion con el codigo " + codigoEstacion);
        }
        estacion.get().cambiaEstadoSensor(codigoSensor , estado);
    }
    public String[][] listaAlertas(
            String codigoEstacion,
            LocalDateTime inicio,
            LocalDateTime fin)
            throws MeteorologiaException {

        Optional<EstacionMeteorologica> estacion = findEstacion(codigoEstacion);

        if (estacion.isEmpty()) {
            throw new MeteorologiaException(
                    "No existe una estación con el código " + codigoEstacion);
        }

        validarFecha(inicio, fin);

        AlertaMeteorologica[] alertas = estacion.get().getAlertasBetween(inicio, fin);

        if (alertas.length == 0) {
            return new String[0][0];
        }

        DateTimeFormatter formatoFecha =  DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        String[][] datos = new String[alertas.length][7];

        for (int i = 0; i < alertas.length; i++) {

            AlertaMeteorologica alerta =
                    alertas[i];

            datos[i][0] = alerta.getFechaHora().format(formatoFecha);

            datos[i][1] = alerta.getFechaHora().format(formatoHora);

            datos[i][2] = alerta.getSensor().getCodigo();

            datos[i][3] = alerta.getFenomeno();

            datos[i][4] = formatoValor(alerta.getValor());

            datos[i][5] = alerta.getSensor().getUnidad();

            datos[i][6] = alerta.getNivel().toString();
        }

        return datos;
    }
    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor,
                                           LocalDateTime inicio, LocalDateTime fin)
            throws MeteorologiaException {

        Optional<EstacionMeteorologica> estacion =
                findEstacion(codigoEstacion);

        if (estacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código "+ codigoEstacion);
        }

        Optional<Sensor> sensor = findSensor(codigoEstacion, codigoSensor);

        if (sensor.isEmpty()) {
            throw new MeteorologiaException("No existe el sensor " + codigoSensor+ " en la estación " + codigoEstacion);
        }

        validarFecha(inicio, fin);

        Optional<ResumenMediciones> resumen = sensor.get().getResumenBetween(inicio, fin);

        if (resumen.isEmpty()) {
            return new String[0];
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        ResumenMediciones r = resumen.get();

        String[] datos = new String[8];

        datos[0] = sensor.get().getTipo().toString();
        datos[1] = sensor.get().getUnidad();
        datos[2] = String.valueOf(r.getCantidad());
        datos[3] = formatoValor(r.getMinimo().getValor());
        datos[4] = r.getMinimo().getFechaHora().format(formato);
        datos[5] = formatoValor(r.getMaximo().getValor());
        datos[6] = r.getMaximo().getFechaHora().format(formato);
        datos[7] = formatoValor(r.getPromedio());

        return datos;
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
    public void cargaDatos()
            throws MeteorologiaException {

        if (!regiones.isEmpty() || !estaciones.isEmpty()) {
            throw new MeteorologiaException(
                    "La carga de datos solo puede realizarse cuando no existen datos registrados."
            );
        }

        String[] lineasRegiones;
        String[] lineasComunas;
        String[] lineasEstaciones;
        String[] lineasSensores;
        String[] lineasMediciones;

        try {
            lineasRegiones = PersistenciaTexto.leeRegistros("regiones.txt");
            lineasComunas = PersistenciaTexto.leeRegistros("comunas.txt");
            lineasEstaciones = PersistenciaTexto.leeRegistros("estaciones.txt");
            lineasSensores = PersistenciaTexto.leeRegistros("sensores.txt");
            lineasMediciones = PersistenciaTexto.leeRegistros("mediciones.txt");

        } catch (MeteorologiaException e) {

            regiones.clear();
            estaciones.clear();

            throw e;
        }

        try {

            for (int i = 0; i < lineasRegiones.length; i++) {

                String[] campos;
                int codigo;
                String nombre;

                try {
                    campos = separarCampos(lineasRegiones[i], 2);

                    codigo =
                            Integer.parseInt(campos[0].trim());

                    nombre =
                            campoTexto(campos[1]);

                } catch (RuntimeException e) {

                    throw errorFormato(
                            "regiones.txt",
                            i + 1
                    );
                }

                try {
                    creaRegion(codigo, nombre);

                } catch (MeteorologiaException e) {

                    throw errorRegla(
                            "regiones.txt",
                            i + 1,
                            e
                    );
                }
            }

            for (int i = 0; i < lineasComunas.length; i++) {

                String[] campos;
                int codigoRegion;
                int codigoComuna;
                String nombre;

                try {
                    campos =
                            separarCampos(lineasComunas[i], 3);

                    codigoRegion =
                            Integer.parseInt(campos[0].trim());

                    codigoComuna =
                            Integer.parseInt(campos[1].trim());

                    nombre =
                            campoTexto(campos[2]);

                } catch (RuntimeException e) {

                    throw errorFormato(
                            "comunas.txt",
                            i + 1
                    );
                }

                try {
                    creaComuna(
                            codigoComuna,
                            nombre,
                            codigoRegion
                    );

                } catch (MeteorologiaException e) {

                    throw errorRegla(
                            "comunas.txt",
                            i + 1,
                            e
                    );
                }
            }

            for (int i = 0; i < lineasEstaciones.length; i++) {

                String[] campos;

                String codigo;
                String nombre;

                float longitud;
                float latitud;
                float altitud;

                int codigoRegion;
                int codigoComuna;

                try {
                    campos =separarCampos(lineasEstaciones[i],7);

                    codigo = campoTexto(campos[0]);

                    nombre = campoTexto(campos[1]);

                    longitud =Float.parseFloat(campos[2].trim().replace(',', '.')
                    );

                    latitud =
                            Float.parseFloat(
                                    campos[3].trim().replace(',', '.')
                            );

                    altitud =Float.parseFloat(campos[4].trim().replace(',', '.'));

                    codigoRegion =Integer.parseInt(campos[5].trim());

                    codigoComuna =Integer.parseInt(campos[6].trim());

                    validarFloat(longitud);
                    validarFloat(latitud);
                    validarFloat(altitud);

                } catch (RuntimeException e) {

                    throw errorFormato(
                            "estaciones.txt",
                            i + 1
                    );
                }

                try {
                    creaEstacion(
                            codigo,
                            nombre,
                            longitud,
                            latitud,
                            altitud,
                            codigoRegion,
                            codigoComuna
                    );

                } catch (MeteorologiaException e) {

                    throw errorRegla(
                            "estaciones.txt",
                            i + 1,
                            e
                    );
                }
            }

            String[][] datosMediciones =
                    new String[lineasMediciones.length][4];

            LocalDateTime[] fechasMediciones =
                    new LocalDateTime[lineasMediciones.length];

            float[] valoresMediciones =
                    new float[lineasMediciones.length];

            boolean[] medicionesProcesadas =
                    new boolean[lineasMediciones.length];

            DateTimeFormatter formatoFecha =
                    DateTimeFormatter
                            .ofPattern("dd/MM/uuuu HH:mm")
                            .withResolverStyle(
                                    ResolverStyle.STRICT
                            );

            for (int i = 0; i < lineasMediciones.length; i++) {

                try {
                    String[] campos =
                            separarCampos(
                                    lineasMediciones[i],
                                    4
                            );

                    datosMediciones[i][0] =
                            campoTexto(campos[0]);

                    datosMediciones[i][1] =
                            campoTexto(campos[1]);

                    datosMediciones[i][2] =
                            campoTexto(campos[2]);

                    datosMediciones[i][3] =
                            campoTexto(campos[3]);

                    fechasMediciones[i] =
                            LocalDateTime.parse(
                                    datosMediciones[i][2],
                                    formatoFecha
                            );

                    valoresMediciones[i] =
                            Float.parseFloat(
                                    datosMediciones[i][3]
                                            .replace(',', '.')
                            );

                    validarFloat(valoresMediciones[i]);

                } catch (RuntimeException e) {

                    throw errorFormato(
                            "mediciones.txt",
                            i + 1
                    );
                }
            }

            String[][] datosSensores =
                    new String[lineasSensores.length][6];

            TipoSensor[] tiposSensores =
                    new TipoSensor[lineasSensores.length];

            Estado[] estadosSensores =
                    new Estado[lineasSensores.length];

            for (int i = 0; i < lineasSensores.length; i++) {

                try {
                    String[] campos =
                            separarCampos(
                                    lineasSensores[i],
                                    6
                            );

                    for (int j = 0; j < campos.length; j++) {
                        datosSensores[i][j] =
                                campoTexto(campos[j]);
                    }

                    tiposSensores[i] =
                            TipoSensor.valueOf(
                                    datosSensores[i][2].toUpperCase()
                            );

                    estadosSensores[i] =
                            Estado.valueOf(
                                    datosSensores[i][5].toUpperCase()
                            );

                } catch (RuntimeException e) {

                    throw errorFormato(
                            "sensores.txt",
                            i + 1
                    );
                }
            }

            for (int i = 0; i < datosSensores.length; i++) {

                String codigoEstacion =
                        datosSensores[i][0];

                String codigoSensor =
                        datosSensores[i][1];

                TipoSensor tipo =
                        tiposSensores[i];

                String marca =
                        datosSensores[i][3];

                String modelo =
                        datosSensores[i][4];

                try {
                    instalaSensor(
                            codigoSensor,
                            marca,
                            modelo,
                            tipo,
                            codigoEstacion
                    );

                } catch (MeteorologiaException e) {

                    throw errorRegla(
                            "sensores.txt",
                            i + 1,
                            e
                    );
                }

                for (int j = 0; j < datosMediciones.length; j++) {

                    if (datosMediciones[j][0].equals(codigoEstacion)
                            && datosMediciones[j][1].equals(codigoSensor)) {

                        try {
                            registraMedicion(
                                    fechasMediciones[j],
                                    valoresMediciones[j],
                                    codigoEstacion,
                                    codigoSensor
                            );

                            medicionesProcesadas[j] = true;

                        } catch (MeteorologiaException e) {

                            throw errorRegla(
                                    "mediciones.txt",
                                    j + 1,
                                    e
                            );
                        }
                    }
                }

                try {
                    cambiaEstadoSensor(codigoEstacion,codigoSensor, Estado.INACTIVO);

                } catch (MeteorologiaException e) {

                    throw errorRegla(
                            "sensores.txt",
                            i + 1,
                            e
                    );
                }
            }

            for (int i = 0; i < medicionesProcesadas.length; i++) {

                if (!medicionesProcesadas[i]) {

                    try {
                        registraMedicion(
                                fechasMediciones[i],
                                valoresMediciones[i],
                                datosMediciones[i][0],
                                datosMediciones[i][1]
                        );

                    } catch (MeteorologiaException e) {

                        throw errorRegla(
                                "mediciones.txt",
                                i + 1,
                                e
                        );
                    }
                }
            }

            for (int i = 0; i < datosSensores.length; i++) {

                if (estadosSensores[i] == Estado.ACTIVO) {

                    try {
                        cambiaEstadoSensor(datosSensores[i][0], datosSensores[i][1],Estado.ACTIVO);

                    } catch (MeteorologiaException e) {

                        throw errorRegla(
                                "sensores.txt",
                                i + 1,
                                e
                        );
                    }
                }
            }

        } catch (MeteorologiaException e) {

            regiones.clear();
            estaciones.clear();

            throw e;
        }
    }
    public void guardaDatos()
            throws MeteorologiaException {

        if (regiones.isEmpty()
                && estaciones.isEmpty()) {

            throw new MeteorologiaException(
                    "No existen datos para guardar."
            );
        }

        ArrayList<String> lineasRegiones = new ArrayList<>();
        ArrayList<String> lineasComunas = new ArrayList<>();
        ArrayList<String> lineasEstaciones = new ArrayList<>();
        ArrayList<String> lineasSensores = new ArrayList<>();
        ArrayList<String> lineasMediciones = new ArrayList<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern( "dd/MM/yyyy HH:mm");

        String separador = PersistenciaTexto.SEPARADOR;

        for (Region region : regiones) {

            lineasRegiones.add(
                    region.getCodigo()
                            + separador
                            + region.getNombre()
            );

            for (Comuna comuna : region.getComunas()) {

                lineasComunas.add(region.getCodigo()
                        + separador
                        + comuna.getCodigo()
                        + separador
                        + comuna.getNombre()
                );
            }
        }

        for (EstacionMeteorologica estacion : estaciones) {

            Comuna comuna =
                    estacion.getComuna();

            lineasEstaciones.add(
                    estacion.getCodigo()
                            + separador
                            + estacion.getNombre()
                            + separador
                            + posicionPersistencia(
                            estacion,
                            separador
                    )
                            + separador
                            + comuna.getRegion().getCodigo()
                            + separador
                            + comuna.getCodigo()
            );

            for (Sensor sensor : estacion.getSensores()) {

                lineasSensores.add(
                        estacion.getCodigo()
                                + separador
                                + sensor.getCodigo()
                                + separador
                                + sensor.getTipo()
                                + separador
                                + sensor.getMarca()
                                + separador
                                + sensor.getModelo()
                                + separador
                                + sensor.getEstado()
                );

                for (Medicion medicion :
                        sensor.getMediciones()) {

                    lineasMediciones.add(
                            estacion.getCodigo()
                                    + separador
                                    + sensor.getCodigo()
                                    + separador
                                    + medicion.getFechaHora()
                                    .format(formato)
                                    + separador
                                    + Float.toString(
                                    medicion.getValor()
                            )
                    );
                }
            }
        }

        PersistenciaTexto.escribeRegistros( "regiones.txt",lineasRegiones.toArray(new String[0]));

        PersistenciaTexto.escribeRegistros( "comunas.txt",
                lineasComunas.toArray(new String[0]));

        PersistenciaTexto.escribeRegistros("estaciones.txt",
                lineasEstaciones.toArray(new String[0]));

        PersistenciaTexto.escribeRegistros("sensores.txt",
                lineasSensores.toArray(new String[0]));

        PersistenciaTexto.escribeRegistros("mediciones.txt",
                lineasMediciones.toArray(new String[0]));
    }

    private String formatoValor(float valor) {
        return String.format(Locale.US, "%.1f", valor);
    }
    private String[] separarCampos(String linea, int cantidadEsperada) {

        String[] campos = linea.split(Pattern.quote(PersistenciaTexto.SEPARADOR),-1);

        if (campos.length != cantidadEsperada) {
            throw new IllegalArgumentException();
        }
        return campos;
    }

    private String campoTexto(String campo) {

        String texto = campo.trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return texto;
    }
    private void validarFloat(float valor) {

        if (!Float.isFinite(valor)) {
            throw new IllegalArgumentException();
        }
    }

    private void validarFecha(LocalDateTime inicio, LocalDateTime fin)
        throws MeteorologiaException{
        if (fin.isBefore(inicio)){
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            throw new MeteorologiaException(
                    "La fecha y hora de término ("
                            + fin.format(formato)
                            + ") es anterior a la de inicio ("
                            + inicio.format(formato)
                            + ")."
            );

        }
    }
    private MeteorologiaException errorFormato( String archivo, int linea) {

        return new MeteorologiaException(
                "Línea " + linea
                        + " del archivo "
                        + archivo
                        + ": formato inválido. "
                        + "No se cargó ningún dato."
        );
    }

    private MeteorologiaException errorRegla(String archivo, int linea, MeteorologiaException original) {
        return new MeteorologiaException(
                "Línea " + linea
                        + " del archivo "
                        + archivo
                        + ": "
                        + original.getMessage()
                        + " No se cargó ningún dato."
        );
    }

    private String posicionPersistencia(
            EstacionMeteorologica estacion,
            String separador) {

        String posicion =estacion.getPosicion().trim();

        posicion =
                posicion.replace("(", "")
                        .replace(")", "")
                        .replace("m", "")
                        .trim();

        String[] partes =posicion.split(";");

        String latitud =partes[0].trim();
        String longitud =partes[1].trim();
        String altitud =partes[2].trim();

        return longitud
                + separador
                + latitud
                + separador
                + altitud;
    }

}
