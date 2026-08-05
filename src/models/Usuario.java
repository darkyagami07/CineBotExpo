package models;

public class Usuario {
    // Encapsulamiento: Atributos privados
    private String nombre;
    private String genero;
    private int anioNacimiento;
    private String peliculaRecomendada;

    private String generoPreferido;
    // Constructor con validaciones básicas
  public Usuario(String nombre, String genero, int anioNacimiento, String generoPreferido) {
        setNombre(nombre);
        setGenero(genero);
        setAnioNacimiento(anioNacimiento);
        this.generoPreferido = generoPreferido;
        this.peliculaRecomendada = "Sin recomendación";
    }

    // --- GETTERS Y SETTERS ---

    public String getNombre() { 
        return nombre; 
    
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getGenero() { 
        return genero; 
    }

    public void setGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            this.genero = "No especificado";
        } else {
            this.genero = genero.trim();
        }
    }

    public int getAnioNacimiento() { 
        return anioNacimiento; 
    }

    public void setAnioNacimiento(int anioNacimiento) {
        int anioActual = 2026;
        if (anioNacimiento < 1900 || anioNacimiento > anioActual) {
            throw new IllegalArgumentException("El año de nacimiento no es válido: " + anioNacimiento);
        }
        this.anioNacimiento = anioNacimiento;
    }

    public String getPeliculaRecomendada() { 
        return peliculaRecomendada; 
    }
    
    public void setPeliculaRecomendada(String peliculaRecomendada) {
        if (peliculaRecomendada == null || peliculaRecomendada.trim().isEmpty()) {
            this.peliculaRecomendada = "Sin recomendación";
        } else {
            this.peliculaRecomendada = peliculaRecomendada.trim();
        }
    }

    // --- MÉTODOS DE UTILIDAD ---

    public int getEdad(int anioActual) {
        return anioActual - this.anioNacimiento;
    }
  
    // Método para LEER el género preferido (esto quita la advertencia amarilla)
    public String getGeneroPreferido() {
        return generoPreferido;
    }

    // Método para ACTUALIZAR el género preferido (lo usaremos desde Registro.java)
    public void setGeneroPreferido(String generoPreferido) {
        this.generoPreferido = generoPreferido;
    }
    @Override
    public String toString() {
        return String.format("Usuario: %s | Género: %s | Año: %d | Recomendación: %s", 
                nombre, genero, anioNacimiento, peliculaRecomendada);
    }
}