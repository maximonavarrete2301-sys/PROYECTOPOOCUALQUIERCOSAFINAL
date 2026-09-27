import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Maximo Navarrete Fernandez
public class InterfazUsuario {
    private Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

    public static void main(String[] args){
        InterfazUsuario interfaz = new InterfazUsuario();
        interfaz.menuPrincipal();
    }
    private void menuPrincipal(){
        instituto = new InstitutoMeteorologia();
        int opcion = 0;

        while (opcion!= 7){
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
                opcion= sc.nextInt();
            }
            sc.nextLine();

            switch (opcion) {
                case 1: crearRegion();
                break;

                case 2: crearComuna();
                break;

                case 3: crearEstacionMeteorologica();
                break;

                case 4: instalarSensor();
                break;

                case 5: registrarMedicion();
                break;

                case 6: menuListados();
                break;

                case 7: System.out.println("Finalizando programa");
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
        sc.nextLine();

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


        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        sc.nextLine();

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
            System.out.println ("> No se pudo crear la estación meteorologica");
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
        String nombreTipo = "";
        switch (opcionTipo) {
            case 1:
                tipo = TipoSensor.TEMPERATURA;
                nombreTipo = "temperatura";
                break;

            case 2:
                tipo = TipoSensor.HUMEDAD;
                nombreTipo = "humedad";
                break;
            case 3:
                tipo = TipoSensor.PRESION;
                nombreTipo = "presion";
                break;


            case 4:
                tipo = TipoSensor.VIENTO;
                nombreTipo = "viento";
                break;
            case 5:
                tipo = TipoSensor.PRECIPITACION;
                nombreTipo = "precipitacion";
                break;
        }

        System.out.print("Código de sensor: ");
        String codigo = sc.nextLine();

        System.out.print("Marca: ");
        String marca = sc.nextLine();

        System.out.print("Modelo: ");
        String modelo = sc.nextLine();

        if (instituto.instalaSensor(codigo, marca, modelo, tipo, codigoEstacion)) {
            System.out.println(">Sensor de temperatura instalado correctamente");
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
            System.out.print("Valor: ")
        } else {
            System.out.print("Valor [" + unidad + "] :");
        }
        float valor = sc.nextFloat();
        sc.nextLine();

        if (instituto.registraMedicion(fechaHora, valor, codigoEstacion, codigoSensor)) {
            System.out.print(">Medición registrada correctamente");
        } else {
            System.out.print(">No se pudo registrar la medición");
        }
    }
    private void menuListados() {
        int opcion = 0;

        while (opcion!=6) {
            System.out.println();
            System.out.println("GENERAR LISTADOS");
            System.out.println("----------------");
            System.out.println("1. Listar regiones");
            System.out.println("2. Listar comunas");
            System.out.println("3. Listar estaciones de comuna");
            System.out.println("4. Listar sensores de estacion");
            System.out.println("5. Listar mediciones de un sensor");
            System.out.println("6. Salir del menu de listados");
            opcion = sc.nextInt();

            while (opcion < 1 || opcion > 6) {
                System.out.println("Error. Ingrese una opcion entre 1 y 6");
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
    }
    private void listarComunas() {
    }
    private void listarEstaciones() {
    }
    private void listarSensores() {
    }
    private void listarMediciones() {
    }











    }

}
