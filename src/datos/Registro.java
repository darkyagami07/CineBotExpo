package datos;
import java.util.Scanner;
import logica.MotorRecomendaciones;
import logica.ProcesadorPLN;

public class Registro {

    String nombre;
    String apellido;
    String genero;
    int fechaNacimiento;

    public void iniciar() {

        Scanner sc = new Scanner(System.in);
        
        ProcesadorPLN procesador = new ProcesadorPLN();
        MotorRecomendaciones motor = new MotorRecomendaciones();

        System.out.println("Bienvenidos al CineBot");
        System.out.println("Por favor complete el registro inicial");

        System.out.print("Nombre: ");
        this.nombre = sc.nextLine();

        System.out.print("Apellido: ");
        this.apellido = sc.nextLine();

        System.out.print("Genero: ");
        this.genero = sc.nextLine();

        System.out.print("Ano de nacimiento: ");
        this.fechaNacimiento = sc.nextInt();

        sc.nextLine();

        System.out.println("\nRegistro exitoso. Hola, " + this.nombre + " " + this.apellido + ".");
        System.out.println("El chat ha comenzado. (Escribe 'salir' para terminar la conversacion)\n");

        String entradaUsuario = "";

        while (true) {
            System.out.print("Tu: ");
            entradaUsuario = sc.nextLine();

            if (entradaUsuario.trim().equalsIgnoreCase("salir")) {
                System.out.println("CineBot: Ha sido un placer hablar contigo. ¡Hasta luego!");
                break;
            }

            String textoProcesado = procesador.procesadorTexto(entradaUsuario);

            String respuestaBot = motor.buscarMejorPelicula(textoProcesado);

            System.out.println("CineBot: " + respuestaBot + "\n");
        }

        sc.close();
    }
}