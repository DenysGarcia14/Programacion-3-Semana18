package com.uped.proyecto.semana18.web;

import java.util.List;

public class RepositorioSocios {
    private final List<Socio> socios = List.of(
            new Socio(1, "Ana", 150),
            new Socio(2, "Luis", 200),
            new Socio(3, "Marta", 155)
    );

    public String listarJson() {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < socios.size(); i++) {
            Socio socio = socios.get(i);

            json.append("{")
                    .append("\"id\":").append(socio.getId()).append(",")
                    .append("\"nombre\":\"").append(escapar(socio.getNombre())).append("\",")
                    .append("\"saldo\":").append(socio.getSaldo())
                    .append("}");

            if (i < socios.size() - 1) {
                json.append(",");
            }
        }

        return json.append("]").toString();
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}