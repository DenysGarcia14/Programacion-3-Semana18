package com.uped.proyecto.semana18.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorVisitas {
    private static int visitas = 0;
    private static final AtomicInteger visitasSeguras = new AtomicInteger(0);
    private static final RepositorioSocios repo = new RepositorioSocios();

    private static synchronized void contarVisita() {
        visitas++;
    }

    private static void responder(
            HttpExchange exchange,
            int codigo,
            String tipo,
            String cuerpo
    ) throws IOException {
        byte[] datos = cuerpo.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set("Content-Type", tipo + "; charset=utf-8");
        exchange.sendResponseHeaders(codigo, datos.length);

        try (OutputStream salida = exchange.getResponseBody()) {
            salida.write(datos);
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer servidor = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        servidor.createContext("/visita", exchange -> {
            contarVisita();
            responder(exchange, 200, "application/json", "{\"ok\":true}");
        });

        servidor.createContext("/visita-segura", exchange -> {
            visitasSeguras.incrementAndGet();
            responder(exchange, 200, "application/json", "{\"ok\":true}");
        });

        servidor.createContext("/conteo", exchange -> {
            String json = "{\"visitas\":" + visitas
                    + ",\"visitas_seguras\":" + visitasSeguras.get() + "}";

            responder(exchange, 200, "application/json", json);
        });

        servidor.createContext("/socios", exchange -> {
            String json = repo.listarJson();
            responder(exchange, 200, "application/json", json);
        });

        servidor.createContext("/", exchange -> {
            String html = Files.readString(Path.of("index.html"));
            responder(exchange, 200, "text/html", html);
        });

        servidor.setExecutor(Executors.newFixedThreadPool(8));
        servidor.start();

        System.out.println("Servidor en http://localhost:8080");
    }
}