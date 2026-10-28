package modelo;

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

            float suma = 0.0f;

            Medicion minTemp = mediciones[0];
            Medicion maxTemp = mediciones[0];

            for (Medicion medicion : mediciones) {

                suma += medicion.getValor();

                if (medicion.getValor() < minTemp.getValor()) {
                    minTemp = medicion;
                }

                if (medicion.getValor() > maxTemp.getValor()) {
                    maxTemp = medicion;
                }
            }

            this.promedio = suma / cantidad;
            this.minimo = minTemp;
            this.maximo = maxTemp;
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


}