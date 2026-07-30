package models;

import java.util.Arrays;

public class Pelicula {
    private int id;
    private String titulo;
    private String genero;
    private String[] palabrasClave;

    public Pelicula(int id, String titulo, String genero, String[] palabrasClave) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.palabrasClave = palabrasClave;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public String[] getPalabrasClave() { return palabrasClave; }

    @Override
    public String toString() {
        return "ID: " + id + " | Título: " + titulo + " | Género: " + genero + 
               " | Claves: " + Arrays.toString(palabrasClave);
    }
}