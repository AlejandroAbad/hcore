package es.hefame.hcore;

import org.json.simple.JSONAware;

/**
 * Las clases que implementen este interfaz son clases que pueden convertirse a
 * un elemento JSON, utilizando la librería org.json.simple (json-simple).
 * 
 * https://code.google.com/archive/p/json-simple/
 * 
 * @author Alejandro_AC
 *
 */
public interface JsonEncodable {
	/**
	 * Este método debe implementarse para convertir a un objeto JSON el objeto
	 * actual. El formato de salida, JSONAware, permite que se devuelvan tanto
	 * instancias de JSONObject como de JSONArray.
	 * 
	 * @return Una representación del objeto actual en un objeto JSONAware.
	 */
	public JSONAware json_encode();
}
