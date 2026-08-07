package logica;

import datos.GestorCatalogo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcesadorPLN {

    /**
     * Limpia la entrada del usuario y reemplaza sinónimos por la palabra clave oficial.
     * Soporta coincidencia exacta de frases/palabras ordenadas por longitud,
     * y coincidencia parcial tipo LIKE para términos restantes.
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
        if (diccionarioPLN == null || diccionarioPLN.isEmpty()) {
            return textoLimpio;
        }

        StringBuilder resultado = new StringBuilder();
        
        // 3. Ordenar las claves por longitud de forma descendente para dar prioridad a frases más largas
        List<String> clavesOrdenadas = new ArrayList<>(diccionarioPLN.keySet());
        clavesOrdenadas.sort((a, b) -> Integer.compare(b.length(), a.length()));

        String textoTemp = " " + textoLimpio + " "; // Espacios de margen para buscar límites de palabra
        List<String> terminosTraducidos = new ArrayList<>();

        // 4. Buscar primero coincidencias exactas o de frases completas (multi-palabra)
        for (String clave : clavesOrdenadas) {
            String claveConEspacios = " " + clave + " ";
            if (textoTemp.contains(claveConEspacios)) {
                String oficial = diccionarioPLN.get(clave);
                if (!terminosTraducidos.contains(oficial)) {
                    terminosTraducidos.add(oficial);
                }
                // Remover la frase/palabra emparejada para evitar re-procesarla
                textoTemp = textoTemp.replace(claveConEspacios, " ");
            }
        }

        // 5. Para las palabras restantes en el texto, buscar coincidencias parciales (tipo LIKE)
        String[] palabrasRestantes = textoTemp.trim().split("\\s+");
        for (String palabra : palabrasRestantes) {
            if (palabra.trim().isEmpty()) continue;
            
            boolean coincidenciaEncontrada = false;
            
            // Ponemos un límite mínimo de 3 caracteres para evitar falsos positivos con palabras de 1 o 2 letras
            if (palabra.length() >= 3) {
                for (String clave : clavesOrdenadas) {
                    if (clave.length() >= 3) {
                        // Caso A: La clave está contenida en la palabra (ej: clave "asustado" en "asustadísimo")
                        // Caso B: La palabra está contenida en la clave (ej: palabra "terr" en clave "terror")
                        if (palabra.contains(clave) || clave.contains(palabra)) {
                            String oficial = diccionarioPLN.get(clave);
                            if (!terminosTraducidos.contains(oficial)) {
                                terminosTraducidos.add(oficial);
                            }
                            coincidenciaEncontrada = true;
                            break; // Coincidencia parcial encontrada para esta palabra
                        }
                    }
                }
            }

            // Si no tuvo traducción ni coincidencia parcial, conservamos la palabra original
            if (!coincidenciaEncontrada) {
                terminosTraducidos.add(palabra);
            }
        }

        // 6. Ensamblar el texto traducido final
        for (String termino : terminosTraducidos) {
            resultado.append(termino).append(" ");
        }

        return resultado.toString().trim();
    }
}