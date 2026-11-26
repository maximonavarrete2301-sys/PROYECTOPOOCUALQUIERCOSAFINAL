package modelo;

import java.util.Arrays;
import java.util.Comparator;

public class ResumenMediciones {

        private final int cantidad;
        private final float promedio;
        private final Medicion minimo;
        private final Medicion maximo;

        public ResumenMediciones(Medicion[] mediciones) {

            if (mediciones == null || mediciones.length == 0) {
                throw new IllegalArgumentException();
            }

            this.cantidad = mediciones.length;

        this.promedio = (float) Arrays.stream(mediciones)
                .mapToDouble(Medicion::getValor)
                .average()
                .orElse(0.0);

        Comparator<Medicion> comparador = Comparator
                .comparing(Medicion::getValor)
                .thenComparing(Medicion::getFechaHora);

        this.minimo = Arrays.stream(mediciones)
                .min(comparador)
                .orElseThrow();

        this.maximo = Arrays.stream(mediciones)
                .max(comparador)
                .orElseThrow();
    }

        public int getCantidad() {
            return cantidad;
        }

        public float getPromedio() {
            return promedio;
        }

        public Medicion getMinimo() {
            return minimo;
        }

        public Medicion getMaximo() {
            return maximo;
        }
    }