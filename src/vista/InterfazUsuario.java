package vista;

import controlador.InstitutoMeteorologia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Scanner;
import controlador.MeteorologiaException;
import modelo.Estado;
import modelo.TipoSensor;

//Maximo Navarrete Fernandez

public class InterfazUsuario {
    private static InterfazUsuario instancia;
    private Scanner sc;

    private InterfazUsuario() {
        sc = new Scanner(System.in);
        sc.useLocale(Locale.US);
        InstitutoMeteorologia.getInstance();
    }
public static InterfazUsuario getInstance() {
    if (instancia == null) {
        instancia = new InterfazUsuario();
    }
    return instancia;
}

public void menuPrincipal() {
    int opcion = 0;
    while (opcion != 10) {
        System.out.println("");
        System.out.println("SISTEMA DE INFORMACIÓN METEOROLÓGICA");
        System.out.println("-------------------------------------");
        System.out.println("1. Crear región");
        System.out.println("2. Crear comuna");
        System.out.println("3. Crear estación meteorológica");
        System.out.println("4. Instalar sensor");
        System.out.println("5. Registrar medición");
        System.out.println("6. Cambiar estado de sensor");
        System.out.println("7. Generar listados");
        System.out.println("8. Cargar datos");
        System.out.println("9. Guardar datos");
        System.out.println("10. Salir");
        System.out.print("Opcion: ");

        opcion = leerEntero();

        while (opcion < 1 || opcion > 10) {
            System.out.print("Opción invalida. Ingrese opción entre 1 y 10");
            System.out.print("Ingrese opcion:");
            opcion = leerEntero();
        }
        switch (opcion) {
            case 1:
                crearRegion();
                break;

            case 2:
                crearComuna();
                break;

            case 3:
                crearEstacionMeteorologica();
                break;

            case 4:
                instalarSensor();
                break;

            case 5:
                registrarMedicion();
                break;

            case 6:
                cambiarEstadoSensor();
                break;

            case 7:
                menuListados();
                break;

            case 8:
                cargarDatos();
                break;

            case 9:
                guardarDatos();
                break;

            case 10:
                salir();
                opcion = 0;
                break;
        }
    }
}

private void crearRegion() {
    System.out.println();
    System.out.println("CREAR REGIÓN");
    System.out.print("Código de región: ");
    int codigo = leerEntero();

    System.out.print("Nombre: ");
    String nombre = leerTexto();
    try {
        InstitutoMeteorologia.getInstance().creaRegion(codigo, nombre);
        System.out.println("> Region creada correctamente.");
    } catch (MeteorologiaException e) {
        mostrarError(e);
    }
}

private void crearComuna() {
    System.out.println();
    System.out.println("CREAR COMUNA");
    System.out.print("Codigo de región: ");
    int codigoRegion = leerEntero();

    System.out.print("Codigo de comuna: ");
    int codigo = leerEntero();

    System.out.print("Nombre: ");
    String nombre = leerTexto();
    try {
        InstitutoMeteorologia.getInstance().creaComuna(codigo, nombre, codigoRegion);
        System.out.println("> Comuna creada correctamente.");
    } catch (MeteorologiaException e) {
        mostrarError(e);
    }
}

private void crearEstacionMeteorologica() {
    System.out.println();
    System.out.println("CREAR ESTACIÓN METEOROLOGICA");
    System.out.println("-------------------------------");

    System.out.print("Código de estación: ");
    String codigo = leerTexto();

    System.out.print("Nombre: ");
    String nombre = leerTexto();


    System.out.print("Longitud: ");
    float longitud = leerFloat();

    System.out.print("Latitud: ");
    float latitud = leerFloat();

    System.out.print("Altitud (m): ");
    float altitud = leerFloat();

    System.out.print("Codigo de región: ");
    int codigoRegion = leerEntero();

    System.out.print("Código de comuna: ");
    int codigoComuna = leerEntero();
    try {
        InstitutoMeteorologia.getInstance().creaEstacion(
                codigo, nombre, longitud, latitud,
                altitud, codigoRegion, codigoComuna);

        System.out.println("> Estación meteorologica creada correctamente.");
    } catch (MeteorologiaException e) {
        mostrarError(e);
    }
}

private void instalarSensor() {
    System.out.println();
    System.out.println("INSTALAR SENSOR");
    System.out.println("-----------------");
    System.out.print("Código de estación: ");
    String codigoEstacion = leerTexto();

    System.out.print("Tipo [1 Temp. 2 Hum. 3 Presión. 4 Viento. 5 Precip.] : ");
    int opcionTipo = leerEntero();
    while (opcionTipo < 1 || opcionTipo > 5) {
        System.out.print("Tipo invalido. Ingrese un número entre 1 y 5");
        opcionTipo = leerEntero();
    }
    TipoSensor tipo = null;

    switch (opcionTipo) {
        case 1:
            tipo = TipoSensor.TEMPERATURA;
            break;

        case 2:
            tipo = TipoSensor.HUMEDAD;
            break;
        case 3:
            tipo = TipoSensor.PRESION;
            break;


        case 4:
            tipo = TipoSensor.VIENTO;
            break;
        case 5:
            tipo = TipoSensor.PRECIPITACION;
            break;
    }

    System.out.print("Código de sensor: ");
    String codigo = leerTexto();

    System.out.print("Marca: ");
    String marca = leerTexto();

    System.out.print("Modelo: ");
    String modelo = leerTexto();
    try {
        InstitutoMeteorologia.getInstance().instalaSensor(codigo,marca,modelo,tipo,codigoEstacion);
        System.out.println("> Sensor instalado correctamente.");

    } catch (MeteorologiaException e) {
        mostrarError(e);
    }
}

private void registrarMedicion() {
    System.out.println();
    System.out.println("REGISTRAR MEDICIÓN");
    System.out.println("------------------");
    System.out.print("Código de estación: ");
    String codigoEstacion = leerTexto();

    System.out.print("Código de sensor: ");
    String codigoSensor = leerTexto();

    System.out.print("Fecha y hora [dd/MM/yyyy HH:mm]: ");
    LocalDateTime fechaHora = leerFechaHora();

    try {
        String unidad = "";

        String[][] sensores =
                InstitutoMeteorologia.getInstance()
                        .listaSensores(codigoEstacion);

        for (String[] sensor : sensores) {
            if (sensor[0].equals(codigoSensor)) {
                unidad = sensor[4];
            }
        }

        if (unidad.isEmpty()) {
            System.out.print("Valor: ");
        } else {
            System.out.print(
                    "Valor [" + unidad + "]: "
            );
        }

        float valor = leerFloat();

        InstitutoMeteorologia.getInstance().registraMedicion(
                fechaHora, valor, codigoEstacion, codigoSensor);

        System.out.println("> Medición registrada correctamente.");
    } catch (MeteorologiaException e) {
        mostrarError(e);
    }
}
    private void cambiarEstadoSensor() {
        System.out.println();
        System.out.println("CAMBIAR ESTADO DE SENSOR");
        System.out.println("-------------------------");

        System.out.print("Código de estación: ");
        String codigoEstacion = leerTexto();

        System.out.print("Código de sensor: ");
        String codigoSensor = leerTexto();

        System.out.print("Nuevo estado [1 ACTIVO 2 INACTIVO]: ");
        int opcionEstado = leerEntero();

        while (opcionEstado < 1 || opcionEstado > 2) {
            System.out.print("Estado invalido. Ingrese 1 o 2: ");
            opcionEstado = leerEntero();
        }

        Estado estado;

        if (opcionEstado == 1) {
            estado = Estado.ACTIVO;
        } else {
            estado = Estado.INACTIVO;
        }

        try {
            InstitutoMeteorologia.getInstance().cambiaEstadoSensor(codigoEstacion, codigoSensor, estado);

            System.out.println("> Estado del sensor "
                    + codigoSensor
                    + " actualizado a "
                    + estado);

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }
    private void cargarDatos() {
        System.out.println();
        System.out.println("CARGAR DATOS");
        System.out.println("------------");

        try {
            InstitutoMeteorologia.getInstance().cargaDatos();
            System.out.println("> Datos cargados correctamente desde la carpeta datos/.");

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }
    private void guardarDatos() {
        System.out.println();
        System.out.println("GUARDAR DATOS");
        System.out.println("-------------");

        try {
            InstitutoMeteorologia.getInstance().guardaDatos();

            System.out.println("> Datos guardados correctamente en la carpeta datos/.");

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }
    private void salir() {
        System.out.print(
                "¿Desea guardar los datos antes de salir? [S/N]: ");

        String respuesta = leerSiNo();
        if (respuesta.equals("S")) {

            try {
                InstitutoMeteorologia.getInstance().guardaDatos();

                System.out.println("> Datos guardados correctamente en la carpeta datos/.");
                System.out.println("> Fin del programa.");
                System.exit(0);

            } catch (MeteorologiaException e) {
                mostrarError(e);
            }

        } else {
            System.out.println("> Fin del programa");
            System.exit(0);
        }
    }


private void menuListados() {
    int opcion = 0;

    while (opcion != 8) {
        System.out.println();
        System.out.println("GENERAR LISTADOS");
        System.out.println("----------------");
        System.out.println("1. Listar regiones");
        System.out.println("2. Listar comunas");
        System.out.println("3. Listar estaciones de comuna");
        System.out.println("4. Listar sensores de estacion");
        System.out.println("5. Listar mediciones de un sensor");
        System.out.println("6. Resumen estadistico de un sensor");
        System.out.println("7. Alertas de una estación");
        System.out.println("8. Volver al menú principal");

        System.out.print("Ingrese opcion: ");
        opcion = leerEntero();

        while (opcion < 1 || opcion > 8) {
            System.out.println("Opción invalida. Ingrese una opcion entre 1 y 8");
            System.out.print("Ingrese opcion:");
            opcion = leerEntero();
        }
        switch ((opcion)) {
            case 1:
                listarRegiones();
                break;
            case 2:
                listarComunas();
                break;
            case 3:
                listarEstaciones();
                break;
            case 4:
                listarSensores();
                break;
            case 5:
                listarMediciones();
                break;
            case 6:
                listarResumenMediciones();
                break;
            case 7:
                listarAlertas();
                break;
            case 8:
                break;
        }
    }
}

private void listarRegiones() {
    String[][] datos = InstitutoMeteorologia.getInstance().listaRegiones();

    System.out.println();
    System.out.println("REGIONES:");
    System.out.println("----------");
    if (datos.length == 0) {
        System.out.println("No hay regiones registradas.");
        return;
    }
    System.out.printf("%-10s %-25s %-20s %-20s%n",
            "Codigo: ", "Nombre:", "Comunas:", "Estaciones:");

    for (String[] fila : datos) {
        System.out.printf("%-10s %-25s %-20s %-20s%n",
                fila[0], fila[1], fila[2], fila[3]);
    }
}

private void listarComunas() {
    String[][] datos = InstitutoMeteorologia.getInstance().listaComunas();

    System.out.println();
    System.out.println("COMUNAS:");
    System.out.println("-----------");
    if (datos.length == 0) {
        System.out.println("No hay comunas registradas.");
        return;
    }
    System.out.printf("%-10s %-20s %-20s %-18s %-20s%n",
            "CODIGO:", "NOMBRE:", "REGION:", "ESTACIONES:", "EST. ACTIVAS");
    for (String[] fila : datos) {
        System.out.printf("%-10s %-20s %-20s %-18s %-20s%n",
                fila[0], fila[1], fila[2], fila[3], fila[4]);
    }
}

    private void listarEstaciones() {
        System.out.println();
        System.out.print("Ingrese codigo de region: ");
        int codigoRegion = leerEntero();

        System.out.print("Ingrese el codigo de la comuna: ");
        int codigoComuna = leerEntero();

        try {
            String[][] datos =
                    InstitutoMeteorologia.getInstance()
                            .listaEstaciones(codigoRegion,codigoComuna);

        String nombreRegion = "";
        String nombreComuna = "";

        String[][] regiones =
                InstitutoMeteorologia.getInstance()
                        .listaRegiones();

        for (String[] region : regiones) {
            if (region[0].equals(
                    String.valueOf(codigoRegion))) {

                nombreRegion = region[1];
            }
        }

        String[][] comunas =
                InstitutoMeteorologia.getInstance()
                        .listaComunas();

        for (String[] comuna : comunas) {
            if (comuna[0].equals(
                    String.valueOf(codigoComuna))
                    && comuna[2].equals(nombreRegion)) {

                nombreComuna = comuna[1];
            }
        }

        System.out.println();
        System.out.println(
                "ESTACIONES DE LA COMUNA "
                        + nombreComuna.toUpperCase()
        );

        if (datos.length == 0) {
            System.out.println("No existen estaciones en la comuna indicada.");
            return;
        }

        System.out.printf(
                "%-18s %-20s %-32s %-12s %-20s%n",
                "CODIGO",
                "NOMBRE",
                "UBICACION",
                "ESTADO",
                "SENSORES OPERATIVOS"
        );

        for (String[] fila : datos) {
            System.out.printf(
                    "%-18s %-20s %-32s %-12s %-20s%n",
                    fila[0], fila[1], fila[2], fila[3],fila[4]);
        }

    } catch (MeteorologiaException e) {
        mostrarError(e);
    }
}

    private void listarSensores() {
        System.out.println();
        System.out.print("Ingrese codigo de la estación: ");
        String codigoEstacion = leerTexto();
        try {
            String[][] datos = InstitutoMeteorologia.getInstance().listaSensores(codigoEstacion);
            System.out.println();
            System.out.println("SENSORES DE " + codigoEstacion.toUpperCase());

            System.out.println();

            if (datos.length == 0) {
                System.out.println("No hay sensores en la estacion indicada.");
                return;
            }

            System.out.printf(
                    "%-12s %-15s %-15s %-15s %-10s %-12s %-32s%n",
                    "CODIGO", "TIPO", "MARCA", "MODELO", "UNIDAD", "ESTADO", "ULTIMA MEDICION");

            for (String[] fila : datos) {
                System.out.printf(
                        "%-12s %-15s %-15s %-15s %-10s %-12s %-32s%n",
                        fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6]);
            }

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }

    private void listarMediciones() {
        System.out.println();
        System.out.print("Ingrese el codigo de estación: ");
        String codigoEstacion = leerTexto();
        System.out.print("Ingrese codigo del sensor: ");
        String codigoSensor = leerTexto();
        System.out.print("Fecha y hora de inicio [dd/MM/yyyy HH:mm]: ");
        LocalDateTime inicio = leerFechaHora();
        System.out.print("Fecha y hora de fin [dd/MM/yyyy HH:mm]: ");
        LocalDateTime fin = leerFechaHora();

        try {
            String[][] datos = InstitutoMeteorologia.getInstance()
                            .listaMediciones(codigoEstacion,codigoSensor,inicio,fin);
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.println();
            System.out.println("MEDICIONES DEL SENSOR " + codigoSensor.toUpperCase());
            System.out.println();

            System.out.println("PERIODO: "+ inicio.format(formato) + " a "+ fin.format(formato));

            if (datos.length == 0) {
                System.out.println(
                        "No hay mediciones para los datos entregados."
                );
                return;
            }

            System.out.printf(
                    "%-15s %-10s %-12s %-10s%n",
                    "FECHA", "HORA", "VALOR", "UNIDAD");

            for (String[] fila : datos) {
                System.out.printf(
                        "%-15s %-10s %-12s %-10s%n",
                        fila[0], fila[1], fila[2], fila[3]);
            }

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }
    private void listarResumenMediciones() {
        System.out.println();
        System.out.print("Codigo de estación: ");
        String codigoEstacion = leerTexto();

        System.out.print("Código de sensor: ");
        String codigoSensor = leerTexto();

        System.out.print("Fecha y hora de inicio [dd/MM/yyyy HH:mm]: ");
        LocalDateTime inicio = leerFechaHora();

        System.out.print("Fecha y hora de fin [dd/MM/yyyy HH:mm]: ");
        LocalDateTime fin = leerFechaHora();

        try {
            String[] datos =InstitutoMeteorologia.getInstance()
                    .listaResumenMediciones(codigoEstacion,codigoSensor,inicio,fin);
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            System.out.println();
            System.out.println("RESUMEN ESTADÍSTICO DEL SENSOR "
                    + codigoSensor.toUpperCase()
                    + " ("+ codigoEstacion.toUpperCase()+ ")");

            System.out.println("PERIODO: "+ inicio.format(formato)+ " a "+ fin.format(formato)
            );

            if (datos.length == 0) {
                System.out.println(
                        "No hay mediciones para el periodo indicado."
                );
                return;
            }

            System.out.println("Tipo de sensor: "+ datos[0]);
            System.out.println("Cantidad de mediciones: "+ datos[2]);
            System.out.println(
                    "Mínimo: "
                            + datos[3]
                            + " "
                            + datos[1]
                            + " ("
                            + datos[4]
                            + ")"
            );

            System.out.println("Máximo: "
                    + datos[5]
                    + " "
                    + datos[1]
                    + " ("
                    + datos[6]
                    + ")"
            );

            System.out.println("Promedio: "
                    + datos[7]
                    + " "
                    + datos[1]
            );

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }
    private void listarAlertas() {
        System.out.println();

        System.out.print("Código de estación: ");
        String codigoEstacion = leerTexto();

        System.out.print(
                "Fecha y hora de inicio [dd/MM/yyyy HH:mm]: "
        );
        LocalDateTime inicio = leerFechaHora();

        System.out.print("Fecha y hora de fin [dd/MM/yyyy HH:mm]: ");
        LocalDateTime fin = leerFechaHora();

        try {
            String[][] datos =InstitutoMeteorologia.getInstance()
                    .listaAlertas(codigoEstacion,inicio,fin);

            DateTimeFormatter formato =DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            System.out.println();
            System.out.println("ALERTAS DE LA ESTACIÓN "+ codigoEstacion.toUpperCase());

            System.out.println("PERIODO: "
                    + inicio.format(formato)
                    + " a "
                    + fin.format(formato));

            if (datos.length == 0) {
                System.out.println("La estación "
                        + codigoEstacion
                        + " no registra alertas en el período indicado.");
                return;
            }

            System.out.printf(
                    "%-12s %-8s %-12s %-25s %-12s %-10s %-10s%n",
                    "FECHA", "HORA", "SENSOR", "FENOMENO", "VALOR", "UNIDAD", "NIVEL");

            for (String[] fila : datos) {
                System.out.printf(
                        "%-12s %-8s %-12s %-25s %-12s %-10s %-10s%n",
                        fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6]);
            }

        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }

private int leerEntero() {
    while (!sc.hasNextInt()) {
        System.out.print("Error. Ingrese un número entero: ");
        sc.nextLine();
    }
    int valor = sc.nextInt();
    sc.nextLine();
    return valor;
}
private float leerFloat() {
    while (!sc.hasNextFloat()) {System.out.print("Error. Ingrese un número válido: ");
        sc.nextLine();
    }

    float valor = sc.nextFloat();
    sc.nextLine();
    return valor;
}

private String leerTexto() {
    String texto = sc.nextLine().trim();
    while (texto.isEmpty()) {
        System.out.print("Error. No puede estar vacío: ");
        texto = sc.nextLine().trim();
    }
    return texto;
}

private LocalDateTime leerFechaHora() {
    DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    LocalDateTime fechaHora = null;

    while (fechaHora == null) {
        String fechaTexto =
                sc.nextLine().trim();

        try {
            fechaHora =
                    LocalDateTime.parse(
                            fechaTexto,
                            formato
                    );

        } catch (DateTimeParseException e) {
            System.out.print(
                    "Fecha invalida. Ingrese nuevamente en formato: [dd/MM/yyyy HH:mm]: "
            );
        }
    }

    return fechaHora;
}
    private void mostrarError(MeteorologiaException e) {
        System.out.println(
                "> Error: " + e.getMessage()
        );
    }
    private String leerSiNo() {
        String respuesta = sc.nextLine().trim().toUpperCase();

        while (!respuesta.equals("S") && !respuesta.equals("N")) {

            System.out.print("Respuesta invalida. Ingrese S o N: ");

            respuesta = sc.nextLine().trim().toUpperCase();
        }
        return respuesta;
    }
}
