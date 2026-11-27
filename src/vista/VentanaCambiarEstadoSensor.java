package vista;

import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import modelo.Estado;

public class VentanaCambiarEstadoSensor extends JDialog {
    private static final String TITULO = "Cambiar estado de sensor";

    private JPanel contentPane;
    private JComboBox<String> comboEstaciones;
    private JComboBox<String> comboSensores;
    private JLabel labelEstadoActual;
    private JPanel panelEstados;
    private JButton botonCambiarEstado;
    private JButton botonCancelar;

    private final InstitutoMeteorologia instituto;
    private final ButtonGroup grupoEstados;
    private final ArrayList<JRadioButton> radiosEstado;

    public VentanaCambiarEstadoSensor(JFrame owner) {
        super(owner, TITULO, true);

        instituto = InstitutoMeteorologia.getInstance();
        grupoEstados = new ButtonGroup();
        radiosEstado = new ArrayList<>();

        setContentPane(contentPane);
        getRootPane().setDefaultButton(botonCambiarEstado);
        cargarEstaciones();
        crearRadiosEstado();
        cargarSensores();
        registrarEventos();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void cargarEstaciones() {
        comboEstaciones.setModel(new DefaultComboBoxModel<>(instituto.getCodigosEstaciones()));
    }

    private void crearRadiosEstado() {
        panelEstados.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

        for (Estado estado : Estado.values()) {
            JRadioButton radio = new JRadioButton(estado.toString());
            radio.setActionCommand(estado.name());
            radio.setSelected(grupoEstados.getButtonCount() == 0);
            grupoEstados.add(radio);
            radiosEstado.add(radio);
            panelEstados.add(radio);
        }
    }

    private void registrarEventos() {
        comboEstaciones.addActionListener(e -> cargarSensores());
        comboSensores.addActionListener(e -> actualizarEstadoActual());
        botonCambiarEstado.addActionListener(e -> cambiarEstado());
        botonCancelar.addActionListener(e -> dispose());
    }

    private void cargarSensores() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();

        if (codEstacion == null) {
            comboSensores.setModel(new DefaultComboBoxModel<>());
        } else {
            try {
                comboSensores.setModel(new DefaultComboBoxModel<>(instituto.getCodigosSensores(codEstacion)));
            } catch (MeteorologiaException e) {
                comboSensores.setModel(new DefaultComboBoxModel<>());
                mostrarError(e.getMessage());
            }
        }

        actualizarEstadoActual();
    }

    private void actualizarEstadoActual() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String codSensor = (String) comboSensores.getSelectedItem();

        if (codEstacion == null || codSensor == null) {
            labelEstadoActual.setText("-");
            return;
        }

        try {
            String estadoActual = instituto.getDatosSensor(codEstacion, codSensor)[2];
            labelEstadoActual.setText(estadoActual);

            radiosEstado.stream()
                    .filter(r -> !r.getActionCommand().equals(estadoActual))
                    .findFirst()
                    .ifPresent(r -> r.setSelected(true));

        } catch (MeteorologiaException e) {
            labelEstadoActual.setText("-");
            mostrarError(e.getMessage());
        }
    }

    private void cambiarEstado() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String codSensor = (String) comboSensores.getSelectedItem();

        if (codEstacion == null) {
            mostrarError("No existen estaciones registradas.");
            return;
        }

        if (codSensor == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "La estación " + codEstacion + " no tiene sensores registrados.",
                    TITULO,
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        Estado nuevoEstado = Estado.valueOf(grupoEstados.getSelection().getActionCommand());

        try {
            instituto.cambiaEstadoSensor(codEstacion, codSensor, nuevoEstado);

            JOptionPane.showMessageDialog(
                    this,
                    "Estado del sensor " + codSensor + " actualizado a " + nuevoEstado + ".",
                    TITULO,
                    JOptionPane.INFORMATION_MESSAGE
            );

            actualizarEstadoActual();

        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
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
