package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import controlador.MeteorologiaException;

public abstract class Sensor {
    private String codigo;
    private String marca;
    private String modelo;
    private Estado estado;
    private EstacionMeteorologica estacion;
    private List<Medicion> mediciones;
//estacion meteorologica deja de dar error -  ignorar

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

        if (estado != Estado.ACTIVO) {

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

        for (Medicion medicion : mediciones) {

            if (medicion.getFechaHora()
                    .equals(fechaHora)) {

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
        }

        Medicion nueva =
                new Medicion(fechaHora, valor);

        int posicion =
                mediciones.size();

        while (posicion > 0
                && mediciones
                .get(posicion - 1)
                .getFechaHora()
                .isAfter(fechaHora)) {

            posicion--;
        }

        mediciones.add(
                posicion,
                nueva
        );
    }
    public Medicion[] getMediciones() {
        return mediciones.toArray(
                new Medicion[0]
        );
    }

    public Medicion getLastMedicion(){
        if (mediciones.isEmpty()) {
            return null;
        }
        Medicion ultima = mediciones.get(0);
        for (Medicion medicion : mediciones){
            if (medicion.getFechaHora().isAfter(ultima.getFechaHora())){
                ultima = medicion;
            }
        }
        return ultima;
    }

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin){
        List<Medicion> resultado = new ArrayList<>();
        for (Medicion m : mediciones){
            LocalDateTime fh = m.getFechaHora();
            if (!fh.isBefore(inicio) && !fh.isAfter(fin)){
                resultado.add(m);
            }
        }
        return resultado.toArray(new Medicion[0]);
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