package models;

import java.util.Arrays;

public class Pelicula {
    private int id;
    private String titulo;
    private String genero;
    private int anio;
    private String[] palabrasClave;
    private String mensajeBot;

    // Constructor completo con los 6 parámetros requeridos por la arquitectura del sistema
    public Pelicula(int id, String titulo, String genero, int anio, String[] palabrasClave, String mensajeBot) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.anio = anio;
        this.palabrasClave = palabrasClave;
        this.mensajeBot = mensajeBot;
    }

    // --- GETTERS ---
    public int getId() { 
        return id; 
    }

    public String getTitulo() { 
        return titulo; 
    }

    public String getGenero() { 
        return genero; 
    }

    public int getAnio() { 
        return anio; 
    }

    public String[] getPalabrasClave() { 
        return palabrasClave; 
    }

    public String getMensajeBot() { 
        return mensajeBot; 
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Título: " + titulo + " | Género: " + genero + " | Año: " + anio +
               " | Claves: " + Arrays.toString(palabrasClave) + " | Mensaje Bot: " + mensajeBot;
    }
}