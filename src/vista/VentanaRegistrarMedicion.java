package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;

import javax.swing.*;
import java.awt.*;
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 3, new Insets(12, 12, 12, 12), 8, 8));
        final JLabel label1 = new JLabel();
        label1.setText("Estacion:");
        contentPane.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboEstaciones = new JComboBox();
        contentPane.add(comboEstaciones, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Sensor:");
        contentPane.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboSensores = new JComboBox();
        contentPane.add(comboSensores, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Tipo de sensor:");
        contentPane.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelTipoSensor = new JLabel();
        labelTipoSensor.setText("-");
        contentPane.add(labelTipoSensor, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Fecha y hora:");
        contentPane.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoFechaHora = new JTextField();
        contentPane.add(campoFechaHora, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("dd/MM/yyyy HH:mm");
        contentPane.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Valor:");
        contentPane.add(label6, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoValor = new JTextField();
        contentPane.add(campoValor, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelUnidad = new JLabel();
        labelUnidad.setText("-");
        contentPane.add(labelUnidad, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        botonRegistrar = new JButton();
        botonRegistrar.setText("Registrar");
        panel1.add(botonRegistrar);
        botonCancelar = new JButton();
        botonCancelar.setText("Cancelar");
        panel1.add(botonCancelar);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
