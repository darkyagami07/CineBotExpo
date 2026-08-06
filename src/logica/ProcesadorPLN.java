package logica;

import datos.GestorCatalogo;
import java.util.Map;

public class ProcesadorPLN {

    public String procesadorTexto(String entrada) {
        if (entrada == null || entrada.trim().isEmpty()) return "";

        // Limpieza de texto (quitar puntos, comas y signos)
        String textoLimpio = entrada.toLowerCase().replaceAll("[^a-záéíóúñ0-9\\s]", "");
        
        // Cargar diccionario dinámico desde el CSV a través de GestorCatalogo
        Map<String, String> diccionarioPLN = GestorCatalogo.getInstancia().getDiccionarioSinonimos();
        
        String[] palabras = textoLimpio.split("\\s+");
        StringBuilder textoTraducido = new StringBuilder();

        for (String palabra : palabras) {
            if (diccionarioPLN != null && diccionarioPLN.containsKey(palabra)) {
                textoTraducido.append(diccionarioPLN.get(palabra)).append(" ");
            } else {
                textoTraducido.append(palabra).append(" ");
            }
        }
        return textoTraducido.toString().trim();
    }
}