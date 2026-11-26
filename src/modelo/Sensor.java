package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import controlador.MeteorologiaException;

public abstract class Sensor implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public String getCodigo(){ return codigo; }
    public String getMarca(){ return marca; }
    public String getModelo(){ return modelo; }
    public Estado getEstado(){ return estado; }

    public void setEstado (Estado estado){
        this.estado = estado;
    }


    public void addMedicion(
            LocalDateTime fechaHora,
            float valor)
            throws MeteorologiaException {

        if (estado != Estado.ACTIVO){

            throw new MeteorologiaException(
                    "El sensor "
                            + codigo
                            + " no está activo."
            );
        }

        if (!esValorAdmisible(valor)) {

            throw new MeteorologiaException(
                    "El valor "
                            + String.format(
                            Locale.US,
                            "%.1f",
                            valor
                    )
                            + " "
                            + getUnidad()
                            + " no es admisible para un sensor de tipo "
                            + getTipo()
                            + "."
            );
        }

        boolean existe = mediciones.stream()
                .anyMatch(m -> m.getFechaHora().equals(fechaHora));

        if (existe) {

            DateTimeFormatter formato =
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm"
                    );

            String fecha =
                    fechaHora.format(formato);

            throw new MeteorologiaException(
                    "El sensor "
                            + codigo
                            + " ya registra una medición el "
                            + fecha.substring(0, 10)
                            + " a las "
                            + fecha.substring(11)
                            + "."
            );
        }

        mediciones.add(
                new Medicion(fechaHora, valor)
        );
    }


    public Medicion[] getMediciones(){
        return mediciones.stream()
                .sorted(Comparator.comparing(Medicion::getFechaHora))
                .toArray(Medicion[]::new);
    }

    public Medicion getLastMedicion(){
        return mediciones.stream()
                .max(Comparator.comparing(Medicion::getFechaHora))
                .orElse(null);
    }

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin){
        return mediciones.stream()
                .filter(m -> !m.getFechaHora().isBefore(inicio)
                        && !m.getFechaHora().isAfter(fin))
                .sorted(Comparator.comparing(Medicion::getFechaHora))
                .toArray(Medicion[]::new);
    }


    // parte nueva del semsor
    public Optional<ResumenMediciones> getResumenBetween(LocalDateTime inicio, LocalDateTime fin) {
        Medicion[] filtradas = getMedicionesBetween(inicio, fin);

        if (filtradas.length == 0){
            return Optional.empty();
        }
        return Optional.of(new ResumenMediciones(filtradas));
    }

    //meto abstrac :b
    public abstract TipoSensor getTipo();
    public abstract String getUnidad();
    public abstract boolean esValorAdmisible(float valor);
}