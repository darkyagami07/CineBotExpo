package datos;

import java.util.Scanner;
import logica.MotorRecomendaciones;
import logica.ProcesadorPLN;
import models.Usuario;

public class Registro {

    String nombre;
    String genero;
    int fechaNacimiento;
    String ultimaPeliculaRecomendada = "";

    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        
        ProcesadorPLN procesador = new ProcesadorPLN();
        MotorRecomendaciones motor = new MotorRecomendaciones();

        System.out.println("==========================================");
        System.out.println("       BIENVENIDO A CINEBOT 2026          ");
        System.out.println("==========================================");

        System.out.print("Nombre: ");
        this.nombre = sc.nextLine();

        System.out.print("Genero: ");
        this.genero = sc.nextLine();

        System.out.print("Año de nacimiento: ");
        this.fechaNacimiento = Integer.parseInt(sc.nextLine().trim());

        System.out.println("\nRegistro exitoso. Hola, " + this.nombre + ".");
        System.out.println("El chat ha comenzado. (Escribe 'salir' para terminar la conversacion)\n");

        while (true) {
            System.out.print("Tu: ");
            String entradaUsuario = sc.nextLine();

            if (entradaUsuario.trim().equalsIgnoreCase("salir")) {
                System.out.println("CineBot: Ha sido un placer hablar contigo. ¡Hasta luego!");
                break;
            }

            String textoProcesado = procesador.procesadorTexto(entradaUsuario);
            String respuestaBot = motor.buscarMejorPelicula(textoProcesado);

            System.out.println("CineBot: " + respuestaBot + "\n");
        }

        // Se guarda el registro en usuarios_preferencias.csv a través de GestorPersistencia
        Usuario usuarioActual = new Usuario(this.nombre, this.genero, this.fechaNacimiento);
        usuarioActual.setPeliculaRecomendada(ultimaPeliculaRecomendada.isEmpty() ? "Atendido" : ultimaPeliculaRecomendada);
        GestorPersistencia.registrarUsuario(usuarioActual);

        sc.close();
    }
}