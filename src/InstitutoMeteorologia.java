import java.time.LocalDateTime;

public class InstitutoMeteorologia {
    public boolean creaRegion(int codigo , String nombre) {

    }
    public boolean creaComuna(int codigo , String nombre, int codigoRegion) {

    }
    public boolean creaEstacion(String cod, String nombre, float lon , float lat, float alt, int codRegion, int codComuna) {

    }
    public boolean instalaSensor(String cod , String marca , String modelo , TipoSensor tipo,
                                 String codigoEstacion) {

    }
    public boolean registraMedicion (LocalDateTime fechaHora , float valor , String codEstacion , String codSensor) {

    }



}
