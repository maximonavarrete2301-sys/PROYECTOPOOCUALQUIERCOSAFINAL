//Maximo Navarrete Fernandez
public class InterfazUsuario {
    private Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

    public static void main(String[] args){
        InterfazUsuario interfaz = new InterfazUsuario();
        interfaz.menuPrincipal();
    }
    public void menuPrincipal(){
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

}
