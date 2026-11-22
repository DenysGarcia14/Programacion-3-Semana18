package com.uped.proyecto.semana18.swing;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class VentanaConsulta extends JFrame {
    private static final String URL = "http://localhost:8080/socios";

    private final JButton btnConsultar = new JButton("Consultar socios");
    private final JLabel lblEstado = new JLabel("Presiona el botón para consultar.");
    private final JTextArea areaDatos = new JTextArea(12, 40);

    public VentanaConsulta() {
        setTitle("Consulta de socios");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        areaDatos.setEditable(false);

        JPanelSuperior();
        add(new JScrollPane(areaDatos), BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> consultar());

        setVisible(true);
    }

    private void JPanelSuperior() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new FlowLayout());
        panel.add(btnConsultar);
        panel.add(lblEstado);
        add(panel, BorderLayout.NORTH);
    }

    private void consultar() {
        btnConsultar.setEnabled(false);
        lblEstado.setText("Consultando...");
        areaDatos.setText("");

        SwingWorker<String, Void> trabajador = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                HttpClient cliente = HttpClient.newHttpClient();

                HttpRequest peticion = HttpRequest.newBuilder()
                        .uri(URI.create(URL))
                        .GET()
                        .build();

                HttpResponse<String> respuesta = cliente.send(
                        peticion,
                        HttpResponse.BodyHandlers.ofString()
                );

                return respuesta.body();
            }

            @Override
            protected void done() {
                try {
                    String json = get();
                    areaDatos.setText(json.replace("},{", "},\n{"));
                    lblEstado.setText("Datos recibidos");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    lblEstado.setText("Consulta interrumpida");
                } catch (ExecutionException e) {
                    lblEstado.setText("Error de conexión");

                    JOptionPane.showMessageDialog(
                            VentanaConsulta.this,
                            "No se pudo conectar con el servidor.",
                            "Error de conexión",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    btnConsultar.setEnabled(true);
                }
            }
        };

        trabajador.execute();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(VentanaConsulta::new);
    }
}
