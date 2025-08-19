package com.hyrule.Frontend;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.hyrule.Backend.archiveprocessor.ProcessorArchive;

/**
 * Interfaz para mostrar el procesamiento de archivos
 */
public class ShowProcessArchive extends JInternalFrame {

    private JTextArea fileContentArea;
    private JTextArea logArea;
    private JButton stopButton;
    private JButton backButton;

    private ProcessorArchive processor;
    private AdminModule adminView;
    private Path filePath;
    private Path logFolderPath;
    private int delay;

    public ShowProcessArchive(Path filePath, Path logFolderPath, int delay, AdminModule adminView) {
        super("Procesar Archivo", true, true, true, true);
        this.adminView = adminView;
        this.filePath = filePath;
        this.logFolderPath = logFolderPath;
        this.delay = delay;

        adminView.setTitle("Procesar Archivo");

        initComponents();
        startFileProcessing();
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica.
     */
    private void initComponents() {
        setSize(1000, 750);
        setLayout(new BorderLayout());

        // Panel de contenidos
        fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);
        JScrollPane scrollFile = new JScrollPane(fileContentArea);
        scrollFile.setBorder(BorderFactory.createTitledBorder("Contenido del archivo"));

        // Panel de logs
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Logs"));

        // Divisor para mostrar lado a lado
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollFile, scrollLog);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Botón detener
        stopButton = new JButton("Detener Procesamiento");
        stopButton.addActionListener(e -> stopProcessing());
        buttonPanel.add(stopButton);

        // Botón regresar al inicio
        backButton = new JButton("Regresar al Inicio");
        backButton.addActionListener(e -> returnToMainMenu());
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Inicializar procesador
        processor = new ProcessorArchive();
    }

    /**
     * Detiene el procesamiento del archivo.
     */
    private void stopProcessing() {
        if (processor != null && processor.isProcessing()) {
            processor.stopProcessing();
            logArea.append(">>> Procesamiento detenido por el usuario.\n");
        }
    }

    /**
     * Regresa al menú principal del sistema.
     */
    private void returnToMainMenu() {

        if (processor != null && processor.isProcessing()) {
            processor.stopProcessing();
        }

        adminView.cerrarVentanas();
        // Mostrar mensaje de confirmación
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(adminView,
                    "Regresando al menú principal...",
                    "Navegación",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Inicia el procesamiento del archivo en hilos separados.
     */
    private void processFile() {

        new Thread(() -> {
            try {

                new Thread(() -> {
                    startFileReading(filePath);
                }).start();

                processor.processWithThread(filePath, logFolderPath, delay,
                        () -> SwingUtilities.invokeLater(() -> onProcessingComplete()));
                startLogReading(processor.getFileLogGenerado());
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(adminView,
                            "Error al procesar el archivo: " + e.getMessage(),
                            "Error de Procesamiento",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    /**
     * Inicia el procesamiento del archivo en hilos separados.
     */
    private void startFileProcessing() {

        new Thread(() -> {
            try {
                // Iniciar el procesamiento del archivo
                processFile();

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(adminView,
                            "Error al procesar el archivo: " + e.getMessage(),
                            "Error de Procesamiento",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        // startFileReading();
    }

    /**
     * Inicia la lectura del archivo para mostrar su contenido en tiempo real.
     */
    private void startFileReading(Path filePath) {
        new Thread(() -> {
            try (BufferedReader br = Files.newBufferedReader(filePath)) {
                String linea;

                while ((linea = br.readLine()) != null) {
                    String finalLinea = linea;
                    SwingUtilities.invokeLater(() -> fileContentArea.append(finalLinea + "\n"));
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }

                }
                JOptionPane.showMessageDialog(adminView,
                        "Lectura del archivo completada.",
                        "Lectura Completa",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(adminView,
                            "Error al leer el archivo: " + e.getMessage(),
                            "Error de Lectura",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void startLogReading(Path logFilePath) {

        new Thread(() -> {
            try (BufferedReader br = Files.newBufferedReader(logFilePath)) {
                String linea;

                while (true) {
                    linea = br.readLine();
                    if (linea != null) {
                        String finalLinea = linea;
                        SwingUtilities.invokeLater(() -> logArea.append(finalLinea + "\n"));
                    } else {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(adminView,
                            "Error al leer el log: " + e.getMessage(),
                            "Error de Lectura",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

    }

    private void onProcessingComplete() {
        SwingUtilities.invokeLater(() -> {
            logArea.append(">>> Procesamiento completado.\n");
        });
    }
}
