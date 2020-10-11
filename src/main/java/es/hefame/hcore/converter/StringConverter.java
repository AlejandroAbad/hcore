package es.hefame.hcore.converter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Clase con métodos estáticos que ayudan a la conversión de los formatos de
 * datos.
 * <h2>Strings conversion</h2>
 * 
 * @author Alejandro_AC
 *
 */
public class StringConverter {

	private StringConverter() {

	}

	/**
	 * Une elementos de una colección de String en un único String, unidos por el
	 * valor de <i>glue</i>.
	 * 
	 * <pre>
	 * 		String[] pieces = {"Ola", "q", "ase"};
	 * 		String glue = "-";
	 * 		Converter.implode(glue, pieces) -&gt; "Ola-q-ase"
	 * </pre>
	 * 
	 * @param glue   Un string que se inserta entre cada par de piezas en la
	 *               colección de piezas
	 * @param pieces La colección de piezas a unir.
	 * @return Una cadena con los elementos de <i>pieces</i> concatenados con
	 *         <i>glue</i> entre ellos.
	 */
	public static String implode(String glue, String... pieces) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pieces.length - 1; i++) {
			sb.append(pieces[i]);
			sb.append(glue);
		}
		sb.append(pieces[pieces.length - 1].trim());
		return sb.toString();
	}

	/**
	 * Convierte una cadena con un nombre de clase no estándard a un nombre de clase
	 * estándard. Realiza lo siguiente:
	 * <ul>
	 * <li>Fuerza que el nombre de la clase comienze con una letra mayúscula</li>
	 * <li>Cada vez que encuentra el carácter '_', lo elimina y hace que el
	 * siguiente carácter sea una mayúscula</li>
	 * </ul>
	 * 
	 * Algunos ejemplos:
	 * 
	 * <pre>
	 * 	Converter.stringToClassname("mi_nombre_de_clase") -&gt; "MiNombreDeClase"
	 *  Converter.stringToClassname("chupiclase") -&gt; "Chupiclase"
	 * </pre>
	 * 
	 * @param s El nombre de la clase no estándard.
	 * @return El nombre de la clase estándard.
	 */
	public static String toClassName(String s) {
		s = s.toLowerCase();
		StringBuilder sb = new StringBuilder();

		boolean toUpper = true;
		for (char c : s.toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				if (toUpper) {
					toUpper = false;
					c = Character.toUpperCase(c);
				}
				sb.append(c);
			}

			if (c == '_') {
				toUpper = true;
			}
		}

		return sb.toString();
	}

	/**
	 * Convierte el primer caracter de una cadena a mayúsculas.
	 * 
	 * Devuelve una cadena con el primer caracter <i>input</i> en máyusculas, si el
	 * caracter es alfabético.
	 * 
	 * @param input La cadena de entrada.
	 * @return Devuelve la cadena resultante.
	 */
	public static String upperCaseFirst(String input) {
		if (input != null && input.length() > 0) {
			return input.substring(0, 1).toUpperCase() + input.substring(1, input.length()).toLowerCase();
		}
		return input;
	}

	/**
	 * Convierte una cadena para que pueda ser utilizada de forma segura como parte
	 * de una URI.
	 * https://docs.oracle.com/javase/7/docs/api/java/net/URLEncoder.html
	 * 
	 * @param uri La cadena a convertir.
	 * @return La cadena convertida.
	 */
	public static String encodeUri(String uri) {
		try {
			return URLEncoder.encode(uri, StandardCharsets.US_ASCII.displayName());
		} catch (UnsupportedEncodingException e) {
			return uri;
		}
	}

}
