package es.hefame.hcore.http;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import es.hefame.hcore.HException;

/**
 * Subclase de APIException que añade el atributo 'http_code'. Está pensada para
 * poder ser enviada directamente como respuesta de una petición HTTP, en la
 * cual el código de respuesta HTTP se extrae de la misma excepción.
 * 
 * @author Alejandro_AC
 *
 */
public class HttpException extends HException {
	private static final long serialVersionUID = -3236410160241401158L;

	/**
	 * Código HTTP asociado a la excepción
	 */
	private final int httpCode;

	/**
	 * Crea la APIHttpException con el código de respuesta HTTP indicado. Llama al
	 * constructor de la clase Exception.
	 * 
	 * @param httpCode El código de respuesta HTTP asociado a la excepción.
	 * @param message  El mensaje de la excepción.
	 * @param cause    La causa de esta excepción si existe.
	 */
	public HttpException(int httpCode, String message, Throwable cause) {
		super(message, cause);
		this.httpCode = httpCode;
	}

	/**
	 * Crea la APIHttpException con el código de respuesta HTTP indicado. Llama al
	 * constructor de la clase Exception.
	 * 
	 * @param httpCode El código de respuesta HTTP asociado a la excepción.
	 * @param message  El mensaje de la excepción.
	 */
	public HttpException(int httpCode, String message) {
		super(message);
		this.httpCode = httpCode;
	}

	/**
	 * Crea la HttpException con el código de respuesta HTTP indicado, a partir de
	 * otra APIException.
	 * 
	 * @param httpCode El código de respuesta HTTP asociado a la excepción.
	 * @param e        La HException original.
	 */
	public HttpException(int httpCode, HException e) {
		super(e.getMessage());
		this.httpCode = httpCode;
	}

	/**
	 * Devuelve el código de respuesta HTTP de la excepción.
	 * 
	 * @return El código de respuesta HTTP de la excepción.
	 */
	public int getHttpStatusCode() {
		return this.httpCode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONAware jsonEncode() {
		JSONObject root = (JSONObject) super.jsonEncode();
		root.put("httpcode", this.getHttpStatusCode());
		return root;
	}

	@Override
	public String toString() {
		return this.jsonEncode().toJSONString();
	}
}
