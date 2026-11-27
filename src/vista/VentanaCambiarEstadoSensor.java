package vista ;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import modelo.Estado;

import javax.swing.*;
import java.awt.*;

public class VentanaCambiarEstadoSensor extends JDialog {
    private JPanel contentPane;
    private JComboBox<String> comboEstaciones;
    private JComboBox<String> comboSensores;
    private JLabel labelEstadoActual;
    private JPanel panelEstados;
    private JButton cambiarEstadoButton;
    private JButton cancelarButton;

    private final InstitutoMeteorologia instituto;
    private Estado estadoSeleccionado;

    public VentanaCambiarEstadoSensor(JFrame owner) {
        super(owner, "Cambiar estado de sensor", true);
        instituto = InstitutoMeteorologia.getInstance();
        setContentPane(contentPane);
        comboEstaciones.setPrototypeDisplayValue("EST-NUBLE-01");
        comboSensores.setPrototypeDisplayValue("ST-01");
        cargarEstados();
        cargarEstaciones();
        registrarEventos();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(cambiarEstadoButton);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void cargarEstados() {
        panelEstados.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        ButtonGroup grupoEstados = new ButtonGroup();
        for (Estado estado : Estado.values()) {
            JRadioButton boton = new JRadioButton(estado.name());
            grupoEstados.add(boton);
            panelEstados.add(boton);
            boton.addActionListener(e -> estadoSeleccionado = estado);
            if (estadoSeleccionado == null || estado == Estado.INACTIVO) {
                boton.setSelected(true);
                estadoSeleccionado = estado;
            }
        }
    }

    private void registrarEventos() {
        comboEstaciones.addActionListener(e -> cargarSensores());
        comboSensores.addActionListener(e -> actualizarEstadoActual());
        cambiarEstadoButton.addActionListener(e -> cambiarEstadoSensor());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void cargarEstaciones() {
        comboEstaciones.removeAllItems();
        for (String codigo : instituto.getCodigosEstaciones()) {
            comboEstaciones.addItem(codigo);
        }
        cargarSensores();
    }

    private void cargarSensores() {
        comboSensores.removeAllItems();
        labelEstadoActual.setText("-");
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        if (codEstacion == null) {
            return;
        }

        try {
            for (String codigo : instituto.getCodigosSensores(codEstacion)) {
                comboSensores.addItem(codigo);
            }
            actualizarEstadoActual();
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void actualizarEstadoActual() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String codSensor = (String) comboSensores.getSelectedItem();
        if (codEstacion == null || codSensor == null) {
            labelEstadoActual.setText("-");
            return;
        }
        try {
            String[] datos = instituto.getDatosSensor(codEstacion, codSensor);
            labelEstadoActual.setText(datos[2]);
        } catch (MeteorologiaException e) {
            labelEstadoActual.setText("-");
            mostrarError(e.getMessage());
        }
    }

    private void cambiarEstadoSensor() {
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
        if (estadoSeleccionado == null) {
            mostrarError("Debe seleccionar el nuevo estado.");
            return;
        }
        try {
            instituto.cambiaEstadoSensor(codEstacion, codSensor, estadoSeleccionado);
            actualizarEstadoActual();
            JOptionPane.showMessageDialog(this,
                    "Estado del sensor " + codSensor + " actualizado a " + estadoSeleccionado + ".",
                    "Cambiar estado de sensor", JOptionPane.INFORMATION_MESSAGE);
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    {
        $$$setupUI$$$();
    }

    /**
     * Inicialización de componentes correspondiente al archivo .form.
     * GUI Designer puede regenerar este bloque al guardar el formulario.
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JLabel componente2 = new JLabel();
        componente2.setText("Estación:");
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.insets = new Insets(4, 7, 4, 7);
        contentPane.add(componente2, gbc2);
        comboEstaciones = new JComboBox<>();
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 1;
        gbc3.gridy = 0;
        gbc3.anchor = GridBagConstraints.WEST;
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.weightx = 1.0;
        gbc3.insets = new Insets(4, 7, 4, 7);
        contentPane.add(comboEstaciones, gbc3);
        final JLabel componente4 = new JLabel();
        componente4.setText("Sensor:");
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 1;
        gbc4.anchor = GridBagConstraints.WEST;
        gbc4.insets = new Insets(4, 7, 4, 7);
        contentPane.add(componente4, gbc4);
        comboSensores = new JComboBox<>();
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 1;
        gbc5.gridy = 1;
        gbc5.anchor = GridBagConstraints.WEST;
        gbc5.fill = GridBagConstraints.HORIZONTAL;
        gbc5.weightx = 1.0;
        gbc5.insets = new Insets(4, 7, 4, 7);
        contentPane.add(comboSensores, gbc5);
        final JLabel componente6 = new JLabel();
        componente6.setText("Estado actual:");
        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 0;
        gbc6.gridy = 2;
        gbc6.anchor = GridBagConstraints.WEST;
        gbc6.insets = new Insets(4, 7, 4, 7);
        contentPane.add(componente6, gbc6);
        labelEstadoActual = new JLabel();…