import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
//Maximo Navarrete Fernandez
public class InterfazUsuario {
    private Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

    public static void main(String[] args) {
        InterfazUsuario interfaz = new InterfazUsuario();
        interfaz.menuPrincipal();
    }

    private void menuPrincipal() {
        instituto = new InstitutoMeteorologia();
        int opcion = 0;

        while (opcion != 7) {
            System.out.println("");
            System.out.println("SISTEMA DE INFORMACIÓN METEOROLÓGICA");
            System.out.println("-------------------------------------");
            System.out.println("1. Crear región");
            System.out.println("2. Crear comuna");
            System.out.println("3. Crear estación meteorológica");
            System.out.println("4. Instalar sensor");
            System.out.println("5. Registrar medición");
            System.out.println("6. Generar listados");
            System.out.println("7. Salir");
            System.out.print("Opcion: ");

            opcion = sc.nextInt();

            while (opcion < 1 || opcion > 7) {
                System.out.print("Opción invalida. Ingrese opción entre 1 y 7");
                System.out.print("Ingrese opcion:");
                opcion = sc.nextInt();
            }
            sc.nextLine();

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
                    menuListados();
                    break;

                case 7:
                    System.out.println("Finalizando programa");
                    break;
            }
        }
    }

    private void crearRegion() {
        System.out.println();
        System.out.println("CREAR REGIÓN");
        System.out.print("Código de región: ");
        int codigo = sc.nextInt();
        sc.nextLine();


        System.out.print("Nombre: ");
        String nombre = sc.nextLine();


        if (instituto.creaRegion(codigo, nombre)) {
            System.out.println("Región creada correctamente");
        } else {
            System.out.println("No se pudo crear la región");
        }
    }

    private void crearComuna() {
        System.out.println();
        System.out.println("CREAR COMUNA");
        System.out.print("Codigo de región: ");
        int codigoRegion = sc.nextInt();


        System.out.print("Codigo de comuna: ");
        int codigo = sc.nextInt();
        sc.nextLine();

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        if (instituto.creaComuna(codigo, nombre, codigoRegion)) {
            System.out.println("Comuna creada correctamente");
        } else {
            System.out.println("No se pudo crear la comuna");
        }
    }

    private void crearEstacionMeteorologica() {
        System.out.println();
        System.out.println("CREAR ESTACIÓN METEOROLOGICA");
        System.out.println("-------------------------------");

        System.out.print("Código de estación: ");
        String codigo = sc.nextLine();

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();


        System.out.print("Longitud: ");
        float longitud = sc.nextFloat();

        System.out.print("Latitud: ");
        float latitud = sc.nextFloat();

        System.out.print("Altitud (m): ");
        float altitud = sc.nextFloat();

        System.out.print("Codigo de región: ");
        int codigoRegion = sc.nextInt();

        System.out.print("Código de comuna: ");
        int codigoComuna = sc.nextInt();
        sc.nextLine();

        if (instituto.creaEstacion(codigo, nombre, longitud,
                latitud, altitud, codigoRegion, codigoComuna)) {
            System.out.println("> Estación meteorologica creada correctamente");
        } else {
            System.out.println("> No se pudo crear la estación meteorologica");
        }
    }

    private void instalarSensor() {
        System.out.println();
        System.out.println("INSTALAR SENSOR");
        System.out.println("-----------------");
        System.out.print("Código de estación: ");
        String codigoEstacion = sc.nextLine();

        System.out.print("Tipo [1 Temp. 2 Hum. 3 Presión. 4 Viento. 5 Precip.] : ");
        int opcionTipo = sc.nextInt();
        while (opcionTipo < 1 || opcionTipo > 5) {
            System.out.print("Tipo invalido. Ingrese un número entre 1 y 5");
            opcionTipo = sc.nextInt();
        }
        sc.nextLine();

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
        String codigo = sc.nextLine();

        System.out.print("Marca: ");
        String marca = sc.nextLine();

        System.out.print("Modelo: ");
        String modelo = sc.nextLine();

        if (instituto.instalaSensor(codigo, marca, modelo, tipo, codigoEstacion)) {
            System.out.println(">Sensor instalado correctamente");
        } else {
            System.out.println("No se pudo instalar el sensor");
        }
    }

    private void registrarMedicion() {
        System.out.println();
        System.out.println("REGISTRAR MEDICIÓN");
        System.out.println("------------------");
        System.out.print("Código de estación: ");
        String codigoEstacion = sc.nextLine();

        System.out.print("Código de sensor: ");
        String codigoSensor = sc.nextLine();

        System.out.print("Fecha y hora [dd/MM/yyyy HH:mm]: ");
        String fechaTexto = sc.nextLine();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fechaHora = LocalDateTime.parse(fechaTexto, formato);

        String unidad = "";
        String[][] sensores = instituto.listaSensores(codigoEstacion);
        for (String[] sensor : sensores) {
            if (sensor[0].equals(codigoSensor)) {
                unidad = sensor[4];
            }
        }
        if (unidad.isEmpty()) {
            System.out.print("Valor: ");
        } else {
            System.out.print("Valor [" + unidad + "] :");
        }
        float valor = sc.nextFloat();
        sc.nextLine();

        if (instituto.registraMedicion(fechaHora, valor, codigoEstacion, codigoSensor)) {
            System.out.println(">Medición registrada correctamente");
        } else {
            System.out.println(">No se pudo registrar la medición");
        }
    }

    private void menuListados() {
        int opcion = 0;

        while (opcion != 6) {
            System.out.println();
            System.out.println("GENERAR LISTADOS");
            System.out.println("----------------");
            System.out.println("1. Listar regiones");
            System.out.println("2. Listar comunas");
            System.out.println("3. Listar estaciones de comuna");
            System.out.println("4. Listar sensores de estacion");
            System.out.println("5. Listar mediciones de un sensor");
            System.out.println("6. Salir del menu de listados");
            System.out.print("Ingrese opcion: ");
            opcion = sc.nextInt();

            while (opcion < 1 || opcion > 6) {
                System.out.println("Opción invalida. Ingrese una opcion entre 1 y 6");
                System.out.print("Ingrese opcion:");
                opcion = sc.nextInt();
            }
            sc.nextLine();
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
                    break;
            }
        }
    }

    private void listarRegiones() {
        String[][] datos = instituto.listaRegiones();

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
        String[][] datos = instituto.listaComunas();

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
        int codigoRegion = sc.nextInt();

        System.out.print("Ingrese el codigo de la comuna: ");
        int codigoComuna = sc.nextInt();

        sc.nextLine();

        String[][] datos = instituto.listaEstaciones(codigoRegion, codigoComuna);
        String nombreRegion = "";
        String nombreComuna = "";

        String[][] regiones = instituto.listaRegiones();
        for (String[] region : regiones) {
            if (region[0].equals(String.valueOf(codigoRegion))){
                nombreRegion = region[1];
            }
        }

        String[][] comunas = instituto.listaComunas();
        for (String[] comuna : comunas) {
            if (comuna[0].equals(String.valueOf(codigoComuna))
                    && comuna[2].equals((nombreRegion))) {
                nombreComuna = comuna[1];
            }
        }
        if (nombreComuna.isEmpty()) {
            System.out.println();
            System.out.println("ESTACIONES DE LA COMUNA " + codigoComuna);
        } else {
            System.out.println();
            System.out.println("ESTACIONES DE LA COMUNA " + nombreComuna.toUpperCase());
        }
        if (datos.length == 0) {
            System.out.printf(" No existen estaciones con los datos indicados");
            return;
        }
        System.out.printf("%-18s %-20s %-32s %-12s %-20s%n",
                "CODIGO", "NOMBRE", "UBICACION", "ESTADO", "SENSORES OPERATIVOS");
        for (String[] fila : datos) {
            System.out.printf("%-18s %-20s %-32s %-12s %-20s%n",
                    fila[0], fila[1], fila[2], fila[3], fila[4]);
        }
    }

    private void listarSensores() {
        System.out.println();
        System.out.print("Ingrese codigo de la estación: ");
        String codigoEstacion = sc.nextLine();

        String[][] datos = instituto.listaSensores(codigoEstacion);

        System.out.println();
        System.out.println("SENSORES DE " + codigoEstacion.toUpperCase());
        System.out.println();
        if (datos.length == 0) {
            System.out.println("No hay sensores en la estacion indicada");
            return;
        }
        System.out.printf("%-12s %-15s %-15s %-15s %-10s %-12s %-32s%n" ,
                "CODIGO", "TIPO", "MARCA", "MODELO", "UNIDAD", "ESTADO", "ULTIMA MEDICION");
        for (String[] fila : datos) {
            System.out.printf("%-12s %-15s %-15s %-15s %-10s %-12s %-32s%n",
                    fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6]);
        }
    }

    private void listarMediciones() {
        System.out.println();
        System.out.print("Ingrese el codigo de estación: ");
        String codigoEstacion = sc.nextLine();

        System.out.print("Ingrese codigo del sensor: ");
        String codigoSensor = sc.nextLine();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.print("Fecha y hora de inicio: [dd/MM/yyyy HH:mm]: ");
        String inicioTexto = sc.nextLine();
        LocalDateTime inicio = LocalDateTime.parse(inicioTexto, formato);

        System.out.print("Fecha y hora de fin: [dd/MM/yyyy HH:mm] ");
        String finTexto = sc.nextLine();
        LocalDateTime fin = LocalDateTime.parse(finTexto, formato);

        String[][] datos = instituto.listaMediciones(codigoEstacion, codigoSensor, inicio, fin);

        System.out.println();
        System.out.println("MEDICIONES DEL SENSOR " + codigoSensor.toUpperCase());
        System.out.println();
        System.out.println("PERIODO: " + inicio.format(formato) + " a " + fin.format(formato));

        if (datos.length == 0) {
            System.out.println("No hay mediciones para los datos entregados");
            return;
        }
        System.out.printf("%-15s %-10s %-12s %-10s%n",
                "FECHA", "HORA", "VALOR", "UNIDAD");
        for (String[] fila : datos) {
            System.out.printf("%-15s %-10s %-12s %-10s%n",
                    fila[0], fila[1], fila[2], fila[3]);
        }
    }
}
