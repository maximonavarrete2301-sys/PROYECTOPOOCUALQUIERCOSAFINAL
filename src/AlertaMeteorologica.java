import java.time.LocalDateTime;

public class AlertaMeteorologica {

    private final LocalDateTime fechaHora;
    private final float valor;
    private final NivelAlerta nivel;
    private final String fenomeno;
    private final Sensor sensor;

    public AlertaMeteorologica(LocalDateTime fechaHora, float valor, NivelAlerta nivel, String fenomeno, Sensor sensor) {

        this.fechaHora = fechaHora;
        this.valor = valor;
        this.nivel =nivel;
        this.fenomeno = fenomeno;
        this.sensor= sensor;
    }

        //(￣(工)￣)
    public LocalDateTime getFechaHora() {return fechaHora; }
    public float getValor(){return valor; }
    public NivelAlerta getNivel(){return nivel; }
    public String getFenomeno() {return fenomeno; }
    public Sensor getSensor() {return sensor; }
}