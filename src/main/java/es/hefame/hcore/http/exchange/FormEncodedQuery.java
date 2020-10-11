package es.hefame.hcore.http.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import es.hefame.hcore.JsonEncodable;

/**
 * Clase que permite analizar y acceder facilmente a los parámetros en formato
 * Form-Encoded de una cadena. El tratamiento es similar al que hace PHP 7 al
 * parsear los valores GET y POST. Por ejemplo para la cadena
 * <i>color=red&amp;size=2&amp;like[]=dogs&amp;like[]=cats</i>, se genera una
 * estructura de la forma:
 * 
 * <pre>
 * {
 *    "color": "red",
 *    "size": "2",
 *    "like": ["dogs", "cats"]
 * }
 * </pre>
 * 
 * Nótese que se eliminan el indicador de array '[]' al final de los nombres de
 * las claves y que estas serán convertidas a minúsculas.
 * 
 * @author Alejandro_AC
 *
 */
public class FormEncodedQuery implements JsonEncodable {

	private Map<String, List<String>> mappedValues;

	/**
	 * Descompone la cadena pasada en formato form-encoded en un mapa de valores tal
	 * que por ejemplo para la cadena
	 * <i>color=red&amp;size=2&amp;like[]=dogs&amp;like[]=cats</i>, se genera una
	 * estructura de la forma:
	 * 
	 * <pre>
	 * {
	 *    "color": "red",
	 *    "size": "2",
	 *    "like": ["dogs", "cats"]
	 * }
	 * </pre>
	 *
	 * Nótese que se eliminan el indicador de array '[]' al final de los nombres de
	 * las claves y que estas serán convertidas a minúsculas.
	 * 
	 * @param queryString La cadena form-encoded de entrada
	 */
	public FormEncodedQuery(String queryString) {
		mappedValues = new HashMap<>();

		if (queryString == null)
			return;

		String[] pairs = queryString.split("&");

		if (pairs.length == 0)
			return;

		for (String param : pairs) {
			String[] pair = param.split("=");

			if (pair.length > 1 && pair[0].length() > 0) {

				if (pair[0].endsWith("[]"))
					pair[0] = pair[0].substring(0, pair[0].length() - 2);

				pair[0] = pair[0].toLowerCase();

				List<String> values = mappedValues.get(pair[0]);

				if (values == null) {
					values = new ArrayList<>();
				}
				values.add(pair[1]);
				mappedValues.put(pair[0], values);
			} else {
				// El estandar que dice al respecto en estos casos?
			}
		}
	}

	/**
	 * Devuelve la cadena en formato form-encoded. Es posible que esta difiera de la
	 * cadena original que se usó para crear el objeto, ya que se añaden los
	 * caracteres '[]' al final de los nombres de aquellas claves que tienen
	 * múltiples valores, y estas son transformadas a minúsculas.
	 * 
	 * @return Devuelve la cadena en formato form-encoded que representa este
	 *         objeto.
	 */
	public String getFormEncoded() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Entry<String, List<String>> param : this.mappedValues.entrySet()) {
			String key = param.getKey();
			List<String> values = param.getValue();
			boolean multivalue = (values.size() > 1);

			for (String val : values) {
				if (first)
					first = false;
				else
					sb.append('&');

				sb.append(key);
				if (multivalue)
					sb.append('[').append(']');
				sb.append('=').append(val);
			}
		}
		return sb.toString();
	}

	/**
	 * Obtiene el primer valor encontrado para una clave dada. Dada la entrada
	 * <i>color=red&amp;size=2&amp;like[]=dogs&amp;like[]=cats&amp;like[]=birds&amp;like[]=rats</i>:
	 * <ul>
	 * <li>get_first("color") -&gt; "red"</li>
	 * <li>get_first("like") -&gt; "dogs"</li>
	 * </ul>
	 * 
	 * @param key La clave cuyo valor queremos obtener
	 * @return El primer valor encontraro para la clave dada, o null si no se
	 *         encuentra.
	 */
	public String getFirst(String key) {
		List<String> values = this.mappedValues.get(key);
		if (values != null)
			return values.get(0);
		return null;
	}

	/**
	 * Obtiene el último valor encontrado para una clave dada. Dada la entrada
	 * <i>ccolor=red&amp;size=2&amp;like[]=dogs&amp;like[]=cats&amp;like[]=birds&amp;like[]=rats</i>:
	 * <ul>
	 * <li>get_last("color") -&gt; "red"</li>
	 * <li>get_last("like") -&gt; "rats"</li>
	 * </ul>
	 * 
	 * @param key La clave cuyo valor queremos obtener
	 * @return El último valor encontraro para la clave dada, o null si no se
	 *         encuentra.
	 */
	public String getLast(String key) {
		List<String> values = this.mappedValues.get(key);
		if (values != null)
			return values.get(values.size() - 1);
		return null;
	}

	/**
	 * Obtiene el valor encontrado en la posicion N para una clave dada. Dada la
	 * entrada
	 * <i>color=red&amp;size=2&amp;like[]=dogs&amp;like[]=cats&amp;like[]=birds&amp;like[]=rats</i>:
	 * <ul>
	 * <li>get_at("color") -&gt; "red"</li>
	 * <li>get_at("like",0) -&gt; "dogs"</li>
	 * <li>get_at("like",2) -&gt; "birds"</li>
	 * <li>get_at("like",3) -&gt; "rats"</li>
	 * </ul>
	 * 
	 * @param key   La clave cuyo valor queremos obtener
	 * @param index La posición dentro de la lista de valores que queremos recuperar
	 * @return El valor encontraro para la clave dada, o null si no se encuentra la
	 *         clave o esta contiene menos de N elementos
	 */
	public String getAt(String key, int index) {
		List<String> values = this.mappedValues.get(key);
		if (values != null && values.size() < index)
			return values.get(index);
		return null;
	}

	/**
	 * Obtiene la lista de valores para una clave dada.
	 * 
	 * @param key La clave cuyo valor queremos obtener
	 * @return La lista de valores para una clave dada.
	 */
	public List<String> get(String key) {
		List<String> values = this.mappedValues.get(key);
		if (values != null)
			return new ArrayList<>(values);
		return null;
	}

	/**
	 * Devuelve la cadena en formato form-encoded. Igual que llamar a
	 * <i>get_form_encoded()</i>.
	 */
	@Override
	public String toString() {
		return this.getFormEncoded();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject jsonEncode() {

		Iterator<Entry<String, List<String>>> it = this.mappedValues.entrySet().iterator();
		JSONObject obj = new JSONObject();

		while(it.hasNext()) {
			Entry<String, List<String>> entry = it.next();
			obj.put(entry.getKey(), entry.getValue().toArray(new String[0]));
		}
		
		return obj;
	}

}
