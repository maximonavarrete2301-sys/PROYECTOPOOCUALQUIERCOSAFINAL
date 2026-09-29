import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Medicion {
    private final LocalDateTime fechaHora;
    private final float valor;

    //construcor kawai
    public Medicion(LocalDateTime fechaHora, float valor){
        this.fechaHora = fechaHora;
        this.valor = valor;
    }

    public LocalDateTime getFechaHora(){
        return fechaHora;
    }

    public float getValor(){
        return valor;
    }

    @Override
    public boolean equals (Object obj){
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Medicion medicion = (Medicion) obj;
        return Objects.equals(fechaHora, medicion.fechaHora);
    }

    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formatter) + "; " + valor;
    }
}
