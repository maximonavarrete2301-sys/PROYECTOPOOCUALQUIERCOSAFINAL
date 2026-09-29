import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Sensor {
    private String codigo;
    private String marca;
    private String modelo;
    private Estado estado;
    private EstacionMeteorologica estacion;
    private List<Medicion> mediciones;

    protected Sensor(String codigo, String marca, String modelo, EstacionMeteorologica estacion){
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.estacion = estacion;
        this.estado = Estado.ACTIVO;
        this.mediciones = new ArrayList<>();
    }

    public String getCodigo(){
        return codigo;
    }
    public String getMarca(){
        return marca;
    }
    public String getModelo(){
        return modelo;
    }
    public Estado getEstado(){
        return estado;
    }

    public void setEstado (Estado estado){
        this.estado = estado;
    }

    public boolean addMedicion(LocalDateTime fechaHora, float valor) {
        if (this.estado == Estado.ACTIVO && esValorAdmisible(valor)) {
            for (Medicion m : mediciones) {
                if (m.getFechaHora().equals(fechaHora)) {
                    return false;
                }
            }
            mediciones.add(new Medicion(fechaHora, valor));
            return true;
        }
        return false;
    }
    //nuevo getlastmedicion
    public Medicion getLastMedicion() {
        if (mediciones.isEmpty()) {
            return null;
        }

        Medicion ultima = mediciones.get(0);

        for (Medicion medicion : mediciones) {
            if (medicion.getFechaHora().isAfter(ultima.getFechaHora())) {
                ultima = medicion;
            }
        }

        return ultima;
    }

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin) {
        List<Medicion> resultado = new ArrayList<>();
        for (Medicion m : mediciones) {
            LocalDateTime fh = m.getFechaHora();
            if (!fh.isBefore(inicio) && !fh.isAfter(fin)) {
                resultado.add(m);
            }
        }
        return resultado.toArray(new Medicion[0]);
    }

    public abstract String getUnidad();
    public abstract boolean esValorAdmisible(float valor);
}
