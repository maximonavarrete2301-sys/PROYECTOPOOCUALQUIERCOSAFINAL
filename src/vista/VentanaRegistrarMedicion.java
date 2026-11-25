package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class VentanaRegistrarMedicion extends JDialog {
    private JPanel contentPane;
    private JComboBox<String> comboEstaciones;
    private JComboBox<String> comboSensores;
    private JLabel labelTipoSensor;
    private JTextField campoFechaHora;
    private JTextField campoValor;
    private JLabel labelUnidad;
    private JButton botonRegistrar;
    private JButton botonCancelar;

    private final InstitutoMeteorologia instituto;

    public VentanaRegistrarMedicion(JFrame owner) {
        super(owner, "Registrar medición", true);

        instituto = InstitutoMeteorologia.getInstance();

        setContentPane(contentPane);
        cargarEstaciones();
        registrarEventos();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }


    private void registrarEventos() {
        comboEstaciones.addActionListener(e -> cargarSensores());
        comboSensores.addActionListener(e -> actualizarDatosSensor());
        botonRegistrar.addActionListener(e -> registrarMedicion());
        botonCancelar.addActionListener(e -> dispose());
    }

    private void cargarEstaciones() {
        comboEstaciones.removeAllItems();

        String[] estaciones = instituto.getCodigosEstaciones();

        for (String codigo : estaciones) {
            comboEstaciones.addItem(codigo);
        }

        if (comboEstaciones.getItemCount() > 0) {
            comboEstaciones.setSelectedIndex(0);
            cargarSensores();
        } else {
            comboSensores.removeAllItems();
            limpiarDatosSensor();
        }
    }

    private void cargarSensores() {
        comboSensores.removeAllItems();
        limpiarDatosSensor();

        String codEstacion = (String) comboEstaciones.getSelectedItem();

        if (codEstacion == null) {
            return;
        }

        try {
            String[] sensores = instituto.getCodigosSensores(codEstacion);

            for (String codigo : sensores) {
                comboSensores.addItem(codigo);
            }

            if (comboSensores.getItemCount() > 0) {
                comboSensores.setSelectedIndex(0);
                actualizarDatosSensor();
            }

        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void actualizarDatosSensor() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String codSensor = (String) comboSensores.getSelectedItem();

        if (codEstacion == null || codSensor == null) {
            limpiarDatosSensor();
            return;
        }

        try {
            String[] datos = instituto.getDatosSensor(codEstacion, codSensor);

            labelTipoSensor.setText(datos[0]);
            labelUnidad.setText(datos[1]);

        } catch (MeteorologiaException e) {
            limpiarDatosSensor();
            mostrarError(e.getMessage());
        }
    }

    private void registrarMedicion() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String codSensor = (String) comboSensores.getSelectedItem();

        if (codEstacion == null) {
            mostrarError("No existen estaciones registradas.");
            return;
        }

        if (codSensor == null) {
            mostrarError("La estación seleccionada no tiene sensores.");
            return;
        }

        String textoFecha = campoFechaHora.getText().trim();
        String textoValor = campoValor.getText().trim();

        if (textoFecha.isEmpty()) {
            mostrarError("Debe ingresar la fecha y hora.");
            return;
        }

        if (textoValor.isEmpty()) {
            mostrarError("Debe ingresar el valor de la medición.");
            return;
        }

        LocalDateTime fechaHora;

        try {
            DateTimeFormatter formato = DateTimeFormatter
                    .ofPattern("dd/MM/uuuu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

            fechaHora = LocalDateTime.parse(textoFecha, formato);

        } catch (DateTimeParseException e) {
            mostrarError("La fecha y hora debe tener el formato dd/MM/yyyy HH:mm.");
            return;
        }

        float valor;

        try {
            valor = Float.parseFloat(textoValor);

        } catch (NumberFormatException e) {
            mostrarError("El valor debe ser numérico.");
            return;
        }

        try {
            instituto.registraMedicion(
                    fechaHora,
                    valor,
                    codEstacion,
                    codSensor
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Medición registrada correctamente.",
                    "Registrar medición",
                    JOptionPane.INFORMATION_MESSAGE
            );

            campoValor.setText("");
            campoValor.requestFocusInWindow();

        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarDatosSensor() {
        labelTipoSensor.setText("-");
        labelUnidad.setText("-");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
