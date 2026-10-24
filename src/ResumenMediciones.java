public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion minimo;
    private final Medicion maximo;
    //ayuda .·´¯`(>▂<)´¯`·. .·´¯`(>▂<)´¯`·

    public ResumenMediciones(Medicion[] mediciones){
        if (mediciones == null || mediciones.length == 0) {
            this.cantidad = 0;
            this.promedio = 0.0f;
            this.minimo = null;
            this.maximo = null;
            return;
        }


        this.cantidad = mediciones.length;

        float suma = 0;
        Medicion minTemp = mediciones[0];
        Medicion maxTemp = mediciones[0];

        for ( Medicion m : mediciones){
            if (m != null) {
                suma += m.getValor();
                if (m.getValor() < minTemp.getValor()){
                    minTemp = m;
                }
                if (m.getValor() > maxTemp.getValor()){
                    maxTemp = m;
                }
            }
        }

        this.promedio = suma / this.cantidad;
        this.minimo = minTemp;
        this.maximo = maxTemp;
    }

    public int getCantidad(){ return cantidad; }
    public float getPromedio(){ return promedio;}
    public Medicion getMinimo() { return minimo;}
    public Medicion getMaximo() { return maximo;}
}