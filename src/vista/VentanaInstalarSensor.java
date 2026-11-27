package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import modelo.TipoSensor;

import javax.swing.*;
import java.awt.*;

public class VentanaInstalarSensor extends JDialog {
    private JPanel contentPane;
    private JComboBox<String> comboEstaciones;
    private JTextField campoCodigo;
    private JTextField campoMarca;
    private JTextField campoModelo;
    private JPanel panelTipos;
    private JButton instalarButton;
    private JButton cancelarButton;

    private final InstitutoMeteorologia instituto;
    private TipoSensor tipoSeleccionado;

    public VentanaInstalarSensor(JFrame owner) {
        super(owner, "Instalar sensor", true);
        instituto = InstitutoMeteorologia.getInstance();
        setContentPane(contentPane);
        comboEstaciones.setPrototypeDisplayValue("EST-NUBLE-01");
        cargarEstaciones();
        cargarTipos();
        registrarEventos();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(instalarButton);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void cargarEstaciones() {
        comboEstaciones.removeAllItems();
        for (String codigo : instituto.getCodigosEstaciones()) {
            comboEstaciones.addItem(codigo);
        }
    }

    private void cargarTipos() {
        panelTipos.setLayout(new GridLayout(0, 3, 6, 4));
        ButtonGroup grupoTipos = new ButtonGroup();
        for (TipoSensor tipo : TipoSensor.values()) {
            JRadioButton boton = new JRadioButton(tipo.name());
            grupoTipos.add(boton);
            panelTipos.add(boton);
            boton.addActionListener(e -> tipoSeleccionado = tipo);
            if (tipoSeleccionado == null || tipo == TipoSensor.TEMPERATURA) {
                boton.setSelected(true);
                tipoSeleccionado = tipo;
            }
        }
    }

    private void registrarEventos() {
        instalarButton.addActionListener(e -> instalarSensor());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void instalarSensor() {
        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String codigo = campoCodigo.getText().trim();
        String marca = campoMarca.getText().trim();
        String modelo = campoModelo.getText().trim();

        if (codEstacion == null) {
            mostrarError("No existen estaciones registradas.");
            return;
        }
        if (codigo.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
            mostrarError("Debe completar todos los campos.");
            return;
        }
        if (tipoSeleccionado == null) {
            mostrarError("Debe seleccionar el tipo de sensor.");
            return;
        }

        try {
            instituto.instalaSensor(codigo, marca, modelo, tipoSeleccionado, codEstacion);
            JOptionPane.showMessageDialog(this,
                    "Sensor " + codigo + " instalado en la estación " + codEstacion + ".",
                    "Instalar sensor", JOptionPane.INFORMATION_MESSAGE);
            campoCodigo.setText("");
            campoMarca.setText("");
            campoModelo.setText("");
            campoCodigo.requestFocusInWindow();
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
        componente4.setText("Código del sensor:");
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 1;
        gbc4.anchor = GridBagConstraints.WEST;
        gbc4.insets = new Insets(4, 7, 4, 7);
        contentPane.add(componente4, gbc4);
        campoCodigo = new JTextField();
        campoCodigo.setColumns(16);
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 1;
        gbc5.gridy = 1;
        gbc5.anchor = GridBagConstraints.WEST;
        gbc5.fill = GridBagConstraints.HORIZONTAL;
        gbc5.weightx = 1.0;
        gbc5.insets = new Insets(4, 7, 4, 7);
        contentPane.add(campoCodigo, gbc5);
        final JLabel componente6 = new JLabel();
        componente6.setText("Marca:");
        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 0;
        gbc6.gridy = 2;
        gbc6.anchor = GridBagConstraints.WEST;
        gbc6.insets = new Insets(4, 7, 4, 7);
        contentPane.add(componente6, gbc6);
        campoMarca = new JTextField();
        campoMarca.setColumns(16);
        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 1;
        gbc7.gridy = 2;
        gbc7.anchor = GridBagConstraints.WEST;
        gbc7.fill = GridBagConstraints.HORIZONTAL;
        gbc7.weightx = 1.0;
        gbc7.insets = new Insets(4, 7, 4, 7);
        contentPane.add(campoMarca, gbc7);
        final JLabel componente8 = new JLabel();
        componente8.setText("Modelo:");
        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 0;
        gbc8.gridy = 3;
        gbc8.anchor = GridBagConstraints.WEST;
        gbc8.insets = new Insets(4, 7, 4, 7);
        contentPane.add(componente8, gbc8);
        campoModelo = new JTextField();
        campoModelo.setColumns(16);
        GridBagConstraints gbc9 = new GridBagConstraints();
        gbc9.gridx = 1;
        gbc9.gridy = 3;
        gbc9.anchor = GridBagConstraints.WEST;
        gbc9.fill = GridBagConstraints.HORIZONTAL;
        gbc9.weightx = 1.0;
        gbc9.insets = new Insets(4, 7, 4, 7);
        contentPane.add(campoModelo, gbc9);
        panelTipos = new JPanel();
        panelTipos.setLayout(new GridBagLayout());
        panelTipos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tipo de sensor"));
        GridBagConstraints gbc10 = new GridBagConstraints();
        gbc10.gridx = 0;
        gbc10.gridy = 4;
        gbc10.gridwidth = 2;
        gbc10.anchor = GridBagConstraints.WEST;
        gbc10.fill = GridBagConstraints.HORIZONTAL;
        gbc10.weightx = 1.0;
        gbc10.insets = new Insets(4, 7, 4, 7);
        contentPane.add(panelTipos, gbc10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new FlowLayout(2, 5, 3));
        GridBagConstraints gbc11 = new GridBagConstraints();
        gbc11.gridx = 0;
        gbc11.gridy = 5;
        gbc11.gridwidth = 2;
        gbc11.anchor = GridBagConstraints.WEST;
        gbc11.fill = GridBagConstraints.HORIZONTAL;
        gbc11.weightx = 1.0;
        gbc11.insets = new Insets(4, 7, 4, 7);
        contentPane.add(panel11, gbc11);
        instalarButton = new JButton();
        instalarButton.setText("Instalar");
        panel11.add(instalarButton);
        cancelarButton = new JButton();
        cancelarButton.setText("Cancelar");
        panel11.add(cancelarButton);
    }

    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
