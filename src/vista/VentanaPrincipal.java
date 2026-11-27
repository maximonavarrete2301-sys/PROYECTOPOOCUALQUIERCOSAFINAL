package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class VentanaPrincipal extends JFrame {
    private JPanel contentPane;
    private JLabel SistemaInformacionMeteorologica;
    private JPanel PanelRegistro;
    private JPanel PanelDatos;
    private JButton crearRegionButton;
    private JButton crearComunaButton;
    private JButton crearEstaciónMeteorologicaButton;
    private JButton instalarSensorButton;
    private JButton registrarMediciónButton;
    private JButton cambiarEstadoSensorButton;
    private JButton cargarDatosButton;
    private JButton importarDatosDeTextoButton;
    private JButton guardarDatosButton;
    private JComboBox<String> comboListados;
    private JButton generarListadoButton;
    private JButton salirButton;
    private JLabel labelEstado;

    private final InstitutoMeteorologia instituto;
    private final InterfazUsuario interfazUsuario;

    public VentanaPrincipal() {
        super("Sistema de Información Meteorológica");

        instituto = InstitutoMeteorologia.getInstance();
        interfazUsuario = InterfazUsuario.getInstance();

        setContentPane(contentPane);

        cargarListados();
        registrarEventos();
        actualizarEstado();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void registrarEventos() {
        crearRegionButton.addActionListener(e -> {
            setVisible(false);

            try {
                interfazUsuario.crearRegion();
            } finally {
                actualizarEstado();
                setVisible(true);
            }
        });

        crearComunaButton.addActionListener(e -> {
            setVisible(false);

            try {
                interfazUsuario.crearComuna();
            } finally {
                actualizarEstado();
                setVisible(true);
            }
        });

        crearEstaciónMeteorologicaButton.addActionListener(e -> {
            setVisible(false);

            try {
                interfazUsuario.crearEstacion();
            } finally {
                actualizarEstado();
                setVisible(true);
            }
        });

        instalarSensorButton.addActionListener(e -> instalarSensor());

        registrarMediciónButton.addActionListener(e -> registrarMedicion());

        cambiarEstadoSensorButton.addActionListener(e -> cambiarEstadoSensor());

        cargarDatosButton.addActionListener(e -> cargarDatos());

        importarDatosDeTextoButton.addActionListener(e -> importarDatosTexto());

        guardarDatosButton.addActionListener(e -> guardarDatos());

        generarListadoButton.addActionListener(e -> generarListado());

        salirButton.addActionListener(e -> salir());
    }

    private void actualizarEstado() {
        if (instituto.existenDatos()) {
            labelEstado.setText("Estado: el sistema tiene datos registrados.");
        } else {
            labelEstado.setText("Estado: el sistema no tiene datos registrados.");
        }
    }

    private void cargarListados() {
        comboListados.removeAllItems();

        comboListados.addItem("Regiones");
        comboListados.addItem("Comunas");
        comboListados.addItem("Estaciones de una comuna");
        comboListados.addItem("Sensores de una estación");
        comboListados.addItem("Mediciones de un sensor");
        comboListados.addItem("Resumen estadístico de un sensor");
        comboListados.addItem("Alertas de una estación");
    }

    private boolean existenEstaciones() {
        if (instituto.getCodigosEstaciones().length == 0) {
            mostrarError("No existen estaciones registradas.");
            return false;
        }

        return true;
    }

    private void instalarSensor() {
        if (existenEstaciones()) {
            VentanaInstalarSensor ventana = new VentanaInstalarSensor(this);
            ventana.setVisible(true);
        }

        actualizarEstado();
    }

    private void registrarMedicion() {
        if (existenEstaciones()) {
            VentanaRegistrarMedicion ventana = new VentanaRegistrarMedicion(this);
            ventana.setVisible(true);
        }

        actualizarEstado();
    }

    private void cambiarEstadoSensor() {
        if (existenEstaciones()) {
            VentanaCambiarEstadoSensor ventana = new VentanaCambiarEstadoSensor(this);
            ventana.setVisible(true);
        }

        actualizarEstado();
    }

    private void generarListado() {
        int opcion = comboListados.getSelectedIndex();

        if (opcion == 6) {
            VentanaListadoAlertas ventana = new VentanaListadoAlertas(this);
            ventana.setVisible(true);

        } else {
            setVisible(false);

            try {
                switch (opcion) {
                    case 0:
                        interfazUsuario.listarRegiones();
                        break;

                    case 1:
                        interfazUsuario.listarComunas();
                        break;

                    case 2:
                        interfazUsuario.listarEstaciones();
                        break;

                    case 3:
                        interfazUsuario.listarSensores();
                        break;

                    case 4:
                        interfazUsuario.listarMediciones();
                        break;

                    case 5:
                        interfazUsuario.listarResumenMediciones();
                        break;
                }

            } finally {
                actualizarEstado();
                setVisible(true);
            }
        }

        actualizarEstado();
    }

    private void cargarDatos() {
        try {
            instituto.cargaDatos();

            JOptionPane.showMessageDialog(
                    this,
                    "Datos cargados correctamente desde el archivo datos/meteorologia.dat.",
                    "Cargar datos",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }

        actualizarEstado();
    }

    private void importarDatosTexto() {
        try {
            instituto.importaDatosTexto();

            JOptionPane.showMessageDialog(
                    this,
                    "Datos de texto importados correctamente.",
                    "Importar datos de texto",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }

        actualizarEstado();
    }

    private boolean guardarDatos() {
        try {
            instituto.guardaDatos();

            JOptionPane.showMessageDialog(
                    this,
                    "Datos guardados correctamente en el archivo datos/meteorologia.dat.",
                    "Guardar datos",
                    JOptionPane.INFORMATION_MESSAGE
            );

            actualizarEstado();
            return true;

        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());

            actualizarEstado();
            return false;
        }
    }

    private void salir() {
        if (!instituto.existenDatos()) {
            System.exit(0);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea guardar los datos antes de salir?",
                "Salir",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            if (guardarDatos()) {
                System.exit(0);
            }

        } else if (opcion == JOptionPane.NO_OPTION) {
            System.exit(0);
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
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(12, 12, 12, 12), 10, 10));
        SistemaInformacionMeteorologica = new JLabel();
        Font SistemaInformacionMeteorologicaFont = this.$$$getFont$$$(null, Font.BOLD, 16, SistemaInformacionMeteorologica.getFont());
        if (SistemaInformacionMeteorologicaFont != null)
            SistemaInformacionMeteorologica.setFont(SistemaInformacionMeteorologicaFont);
        SistemaInformacionMeteorologica.setHorizontalAlignment(0);
        SistemaInformacionMeteorologica.setText("Sistema de Información Meteorologica");
        contentPane.add(SistemaInformacionMeteorologica, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        PanelDatos = new JPanel();
        PanelDatos.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(PanelDatos, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelDatos.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Datos", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        cargarDatosButton = new JButton();
        cargarDatosButton.setText("Cargar datos");
        panel2.add(cargarDatosButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        guardarDatosButton = new JButton();
        guardarDatosButton.setText("Guardar datos");
        panel2.add(guardarDatosButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        importarDatosDeTextoButton = new JButton();
        importarDatosDeTextoButton.setText("Importar datos de texto");
        panel2.add(importarDatosDeTextoButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        PanelDatos.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Listados", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("Seleccione el listado:");
        panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboListados = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboListados.setModel(defaultComboBoxModel1);
        panel3.add(comboListados, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generarListadoButton = new JButton();
        generarListadoButton.setText("Generar Listado");
        panel3.add(generarListadoButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        PanelRegistro = new JPanel();
        PanelRegistro.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(PanelRegistro, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        PanelRegistro.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Registro", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        crearRegionButton = new JButton();
        crearRegionButton.setText("Crear región");
        PanelRegistro.add(crearRegionButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        crearComunaButton = new JButton();
        crearComunaButton.setText("Crear comuna");
        PanelRegistro.add(crearComunaButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        crearEstaciónMeteorologicaButton = new JButton();
        crearEstaciónMeteorologicaButton.setText("Crear estación meteorologica");
        PanelRegistro.add(crearEstaciónMeteorologicaButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        instalarSensorButton = new JButton();
        instalarSensorButton.setText("Instalar Sensor");
        PanelRegistro.add(instalarSensorButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registrarMediciónButton = new JButton();
        registrarMediciónButton.setText("Registrar Medición");
        PanelRegistro.add(registrarMediciónButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cambiarEstadoSensorButton = new JButton();
        cambiarEstadoSensorButton.setText("Cambiar estado sensor");
        PanelRegistro.add(cambiarEstadoSensorButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        salirButton = new JButton();
        salirButton.setText("Salir");
        contentPane.add(salirButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelEstado = new JLabel();
        labelEstado.setText("Estado: El sistema no tiene datos registrados");
        contentPane.add(labelEstado, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
