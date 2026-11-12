import javax.swing.*;
import java.awt.*;

public class form1 extends JFrame {

    private static final int TICK_MS = 20;      // Cada cuántos ms se actualiza la barra
    private static final int ESCALA = 1000;     // Resolución de la barra (0..1000)

    private  JProgressBar barra = new JProgressBar(0, ESCALA);
    private  JSpinner objetivoSegundos = new JSpinner(new SpinnerNumberModel(5, 1, 600, 1));
    private  JButton botonMantener = new JButton("Mantén pulsado");
    private  JButton botonReiniciar = new JButton("Reiniciar");
    private  JLabel etiquetaEstado = new JLabel("Mantén pulsado el botón para llenar la barra");

    private final Timer timer;
    private double progreso = 0.0;              // Valor entre 0.0 y 1.0
    private boolean completado = false;
    private JSpinner spinner1;

    public form1() {
        super("Barra de progreso por pulsación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        barra.setStringPainted(true);

        // Panel del objetivo
        JPanel panelObjetivo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelObjetivo.add(new JLabel("Objetivo (segundos):"));
        panelObjetivo.add(objetivoSegundos);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.add(botonMantener);
        panelBotones.add(botonReiniciar);

        JPanel centro = new JPanel(new GridLayout(0, 1, 5, 5));
        centro.add(panelObjetivo);
        centro.add(barra);
        centro.add(panelBotones);
        centro.add(etiquetaEstado);
        etiquetaEstado.setHorizontalAlignment(SwingConstants.CENTER);

        centro.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        add(centro);

        // Timer que actualiza la barra cada TICK_MS
        timer = new Timer(TICK_MS, e -> actualizar());

        // El modelo del botón informa de si está pulsado (ratón o teclado)
        botonMantener.getModel().addChangeListener(e -> {
            if (botonMantener.getModel().isPressed() && !completado && !timer.isRunning()) {
                timer.start();
            }
        });

        botonReiniciar.addActionListener(e -> reiniciar());

        setSize(420, 260);
        setLocationRelativeTo(null);
    }


    private void actualizar() {
        boolean pulsado = botonMantener.getModel().isPressed();
        int segundos = (Integer) objetivoSegundos.getValue();

        // Lo que sube (o baja) en cada tick para que mantener pulsado
        // 'segundos' segundos llene exactamente la barra
        double paso = TICK_MS / (segundos * 1000.0);

        if (pulsado) {
            progreso += paso;
        } else {
            progreso -= paso;
        }

        // Limitar entre 0 y 1
        progreso = Math.max(0.0, Math.min(1.0, progreso));
        barra.setValue((int) Math.round(progreso * ESCALA));
        barra.setString((int) Math.round(progreso * 100) + " %");

        if (progreso >= 1.0) {
            completar();
        } else if (progreso <= 0.0 && !pulsado) {
            timer.stop(); // No hace falta seguir ejecutando el timer
        }
    }

    private void completar() {
        completado = true;
        timer.stop();
        botonMantener.setEnabled(false);
        etiquetaEstado.setText("¡Objetivo completado!");
        JOptionPane.showMessageDialog(this, "¡Completado! Has llenado la barra en el objetivo marcado.");
    }

    private void reiniciar() {
        timer.stop();
        progreso = 0.0;
        completado = false;
        barra.setValue(0);
        barra.setString("0 %");
        objetivoSegundos.setValue(5);
        botonMantener.setEnabled(true);
        etiquetaEstado.setText("Mantén pulsado el botón para llenar la barra");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new form1().setVisible(true));
    }


}