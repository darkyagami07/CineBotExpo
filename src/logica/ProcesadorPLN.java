package logica;

import datos.GestorCatalogo;
import java.util.Map;

public class ProcesadorPLN {

    /**
     * Limpia la entrada del usuario y reemplaza sinónimos por la palabra clave oficial.
     */
    public static String procesadorTexto(String entrada) {
        if (entrada == null || entrada.trim().isEmpty()) {
            return "";
        }

        // 1. Convertir a minúsculas y eliminar signos de puntuación / caracteres especiales
        // Permitir operadores matemáticos (+ - * /) para detectar preguntas como "1+1"
        String textoLimpio = entrada.toLowerCase().replaceAll("[^a-záéíóúñ0-9\\s+*/-]", "").trim();
        
        // 2. Obtener el diccionario dinámico cargado desde GestorCatalogo
        Map<String, String> diccionarioPLN = GestorCatalogo.getInstancia().getDiccionarioSinonimos();
        
        String[] palabras = textoLimpio.split("\\s+");
        StringBuilder textoTraducido = new StringBuilder();

        // 3. Traducir cada palabra según el mapa de sinónimos
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