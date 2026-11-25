package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class VentanaListadoAlertas extends JDialog {
    private JPanel contentPane;
    private JComboBox<String> comboEstaciones;
    private JTextField campoInicio;
    private JTextField campoFin;
    private JButton botonListar;
    private JTable tablaAlertas;
    private JLabel labelResumen;
    private JButton botonCerrar;

    private final InstitutoMeteorologia instituto;
    private DefaultTableModel modeloTabla;

    public VentanaListadoAlertas(JFrame owner) {
        super(owner, "Alertas de una estación", true);

        instituto = InstitutoMeteorologia.getInstance();

        setContentPane(contentPane);
        configurarTabla();
        cargarEstaciones();
        registrarEventos();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    private void configurarTabla() {
        String[] columnas = {"Fecha", "Hora", "Sensor", "Fenómeno", "Valor", "Nivel"};

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaAlertas.setModel(modeloTabla);
        tablaAlertas.getTableHeader().setReorderingAllowed(false);
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(95);
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(55);
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaAlertas.getColumnModel().getColumn(3).setPreferredWidth(170);
        tablaAlertas.getColumnModel().getColumn(4).setPreferredWidth(95);
        tablaAlertas.getColumnModel().getColumn(5).setPreferredWidth(75);
    }

    private void cargarEstaciones() {
        String[] estaciones = instituto.getCodigosEstaciones();

        for (String codigo : estaciones) {
            comboEstaciones.addItem(codigo);
        }
    }

    private void registrarEventos() {
        botonListar.addActionListener(e -> listarAlertas());
        botonCerrar.addActionListener(e -> dispose());
    }

    private void listarAlertas() {
        modeloTabla.setRowCount(0);
        labelResumen.setText(" ");

        String codEstacion = (String) comboEstaciones.getSelectedItem();
        String textoInicio = campoInicio.getText().trim();
        String textoFin = campoFin.getText().trim();

        if (codEstacion == null) {
            mostrarError("No existen estaciones registradas.");
            return;
        }

        if (textoInicio.isEmpty() || textoFin.isEmpty()) {
            mostrarError("Debe ingresar la fecha y hora de inicio y de término.");
            return;
        }

        DateTimeFormatter formato = DateTimeFormatter
                .ofPattern("dd/MM/uuuu HH:mm")
                .withResolverStyle(ResolverStyle.STRICT);

        LocalDateTime inicio;
        LocalDateTime fin;

        try {
            inicio = LocalDateTime.parse(textoInicio, formato);
            fin = LocalDateTime.parse(textoFin, formato);

        } catch (DateTimeParseException e) {
            mostrarError("Las fechas y horas deben tener el formato dd/MM/yyyy HH:mm.");
            return;
        }

        try {
            String[][] alertas = instituto.listaAlertas(codEstacion, inicio, fin);

            for (String[] alerta : alertas) {
                modeloTabla.addRow(new String[]{
                        alerta[0],
                        alerta[1],
                        alerta[2],
                        alerta[3],
                        alerta[4] + " " + alerta[5],
                        alerta[6]
                });
            }

            labelResumen.setText("Período: " + inicio.format(formato)
                    + " a " + fin.format(formato) + " — "
                    + alertas.length + (alertas.length == 1 ? " alerta." : " alertas."));

            if (alertas.length == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "La estación " + codEstacion + " no registra alertas en el período indicado.",
                        "Alertas de una estación",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

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