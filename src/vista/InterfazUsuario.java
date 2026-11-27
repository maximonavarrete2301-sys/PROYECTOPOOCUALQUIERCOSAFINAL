package vista;

import controlador.InstitutoMeteorologia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Scanner;
import controlador.MeteorologiaException;
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

public void crearRegion() {
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

public void crearComuna() {
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

public void crearEstacion() {
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

public void listarRegiones() {
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

public void listarComunas() {
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

    public void listarEstaciones() {
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

    public void listarSensores() {
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

    public void listarMediciones() {
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
    public void listarResumenMediciones() {
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
}
