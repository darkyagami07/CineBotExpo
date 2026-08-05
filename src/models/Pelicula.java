package models; 
import java.util.Arrays;

public class Pelicula {
    private int id;
    private String titulo;
    private String genero;
    private String[] palabrasClave;
    
    // 1. AÑADIMOS EL NUEVO ATRIBUTO
    private String mensajeBot; 

    // 2. ACTUALIZAMOS EL CONSTRUCTOR (nota que ahora recibe 5 parámetros)
    public Pelicula(int id, String titulo, String genero, String[] palabrasClave, String mensajeBot) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.palabrasClave = palabrasClave;
        this.mensajeBot = mensajeBot; 
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public String[] getPalabrasClave() { return palabrasClave; }
    
    // 3. AÑADIMOS EL MÉTODO QUE TE ESTÁ PIDIENDO EL MOTOR
    public String getMensajeBot() { 
        return mensajeBot; 
    } 

    @Override
    public String toString() {
        return "ID: " + id + " | Título: " + titulo + " | Género: " + genero +
               " | Claves: " + Arrays.toString(palabrasClave);
    }
}