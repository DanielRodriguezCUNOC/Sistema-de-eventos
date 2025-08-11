package com.hyrule.Frontend;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyrule.Frontend.event.EventRegisterForm;

public class AdminModule extends JFrame {

    private JDesktopPane desktopPane;
    private boolean subirArchivo = false;
    private JPanel panelFondo;
    private JPanel panelInicio;

    public AdminModule() {
        setTitle("Módulo Administrador");
        setSize(1000, 740);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(240, 242, 245));
            }
        };

        setContentPane(desktopPane);
        subirArchivo();

        setVisible(true);
    }

    private void subirArchivo() {
        panelInicio = new JPanel();
        panelInicio.setLayout(new GridBagLayout());
        panelInicio.setBackground(new Color(245, 247, 250));
        panelInicio.setBounds(0, 0, 1000, 740);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel labelTitulo = new JLabel("Bienvenido al Módulo Administrador");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        labelTitulo.setForeground(new Color(40, 40, 40));
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel labelSubtitulo = new JLabel("Por favor, sube un archivo para continuar.");
        labelSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        labelSubtitulo.setForeground(new Color(90, 90, 90));
        labelSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JButton buttonCargarArchivo = new JButton("📂 Cargar Archivo");
        buttonCargarArchivo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        buttonCargarArchivo.setBackground(new Color(33, 141, 230));
        buttonCargarArchivo.setForeground(Color.WHITE);
        buttonCargarArchivo.setFocusPainted(false);
        buttonCargarArchivo.setBorder(new EmptyBorder(12, 25, 12, 25));
        buttonCargarArchivo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonCargarArchivo.addActionListener(e -> {
            desktopPane.remove(panelInicio);
            pedirArchivo();
            subirArchivo = true;
            if (subirArchivo) {
                panelFondo();
                desktopPane.revalidate();
                desktopPane.repaint();
                setJMenuBar(crearMenuBar());
            } else {
                System.out.println("Archivo no leído.");
            }
        });

        JPanel panelTextos = new JPanel();
        panelTextos.setLayout(new BoxLayout(panelTextos, BoxLayout.Y_AXIS));
        panelTextos.setBackground(new Color(245, 247, 250));
        panelTextos.add(labelTitulo);
        panelTextos.add(Box.createVerticalStrut(10));
        panelTextos.add(labelSubtitulo);

        // *Añadimos elementos al panel principal */
        panelInicio.add(panelTextos, gbc);
        panelInicio.add(buttonCargarArchivo, gbc);

        desktopPane.add(panelInicio);
        desktopPane.revalidate();
        desktopPane.repaint();
    }

    private JMenu crearMenu(String titulo) {
        JMenu menu = new JMenu(titulo);
        menu.setFont(new Font("SansSerif", Font.BOLD, 15));
        menu.setBackground(new Color(33, 141, 230));
        menu.setBorder(new EmptyBorder(5, 20, 5, 20));
        menu.setForeground(new Color(255, 255, 255));
        menu.setOpaque(true);

        menu.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                menu.setBackground(new Color(33, 141, 230));
                menu.setCursor(new Cursor(Cursor.HAND_CURSOR));
                menu.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                menu.setBackground(new Color(33, 141, 230));
                menu.setSelected(false);
                menu.repaint();
            }
        });

        return menu;
    }

    private JMenuItem crearMenuItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setBackground(new Color(33, 141, 230));
        item.setBorder(new EmptyBorder(8, 20, 8, 20));
        item.setForeground(new Color(255, 255, 255));
        item.setOpaque(true);

        // Deshabilitar el foco visual por defecto
        item.setFocusPainted(false);
        item.setBorderPainted(false);

        // Efectos hover mejorados
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(33, 141, 230));
                item.setForeground(Color.WHITE);
                item.setCursor(new Cursor(Cursor.HAND_CURSOR));
                item.setSelected(false);
                item.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(33, 141, 230));
                item.setForeground(Color.WHITE);
                item.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                item.setSelected(false);
                item.repaint();
            }
        });

        return item;
    }

    private JMenuBar crearMenuBar() {
        // *Creamos la barra de menú */
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(33, 37, 41));
        menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));

        // *Creamos el menú de Eventos */
        JMenu menuEventos = crearMenu("Eventos");

        // * Creamos el ítem de menú para registrar eventos */
        JMenuItem itemRegistrarEvento = crearMenuItem("Registrar Evento");
        itemRegistrarEvento.addActionListener(e -> {
            cerrarVentanas();
            mostrarEventRegisterModule();

            // * Quitar la selección del menú después de hacer clic */
            menuEventos.setSelected(false);
            menuBar.repaint();
        });
        menuEventos.add(itemRegistrarEvento);

        menuBar.add(menuEventos);
        return menuBar;
    }

    public void mostrarEventRegisterModule() {
        EventRegisterForm registerModule = new EventRegisterForm(this);
        desktopPane.add(registerModule);
        centrarInternalFrame(registerModule);
        registerModule.setVisible(true);
    }

    public void cerrarVentanas() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
    }

    private void centrarInternalFrame(JInternalFrame frame) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension jInternalFrameSize = frame.getSize();
        frame.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
                (desktopSize.height - jInternalFrameSize.height) / 2);
    }

    // *Metodo para pedir al usuario que suba un archivo */
    public void pedirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Aquí puedes manejar el archivo seleccionado
            System.out.println("Archivo seleccionado: " + fileChooser.getSelectedFile().getAbsolutePath());
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }
    }

    private void panelFondo() {
        InputStream imgStream = getClass().getResourceAsStream("/com/hyrule/resources/images/fondo.png");
        panelFondo = new JPanel(new BorderLayout());
        panelFondo.setBackground(new Color(245, 247, 250));

        if (imgStream == null) {
            System.err.println("No se pudo cargar la imagen de fondo.");
            return;

        }
        try {
            Image originalImage = ImageIO.read(imgStream);
            panelFondo.setOpaque(false);
            JLabel lblImage = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    int altura = desktopPane.getHeight();
                    int ancho = desktopPane.getWidth();
                    double imgRatio = (double) originalImage.getWidth(null) / originalImage.getHeight(null);
                    int nuevaAnchura = ancho;
                    int nuevaAltura = (int) (nuevaAnchura / imgRatio);

                    if (nuevaAltura > altura) {
                        nuevaAltura = altura;
                        nuevaAnchura = (int) (nuevaAltura * imgRatio);

                    }
                    int x = (ancho - nuevaAnchura) / 2;

                    g.drawImage(originalImage, x, 0, nuevaAnchura, nuevaAltura, this);
                }
            };
            panelFondo.add(lblImage, BorderLayout.CENTER);
            panelFondo.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
            desktopPane.add(panelFondo);

            desktopPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    panelFondo.setSize(desktopPane.getSize());
                    lblImage.repaint();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
