package models;

public class Usuario {
    public String nombre;
    public String genero;
    public int anoNacimiento;
    public String peliculaRecomendada;

    public Usuario(String nombre, String genero, int anoNacimiento) {
        this.nombre = nombre;
        this.genero = genero;
        this.anoNacimiento = anoNacimiento;
        this.peliculaRecomendada = "";
    }

    public String getNombre() { return nombre; }
    public String getGenero() { return genero; }
    public int getanoNacimiento() { return anoNacimiento; }
    public String getPeliculaRecomendada() { return peliculaRecomendada; }
    
    public void setPeliculaRecomendada(String peliculaRecomendada) {
        this.peliculaRecomendada = peliculaRecomendada;
    }

    public int getEdad(int anoActual) {
        return anoActual - this.anoNacimiento;
    }
}
