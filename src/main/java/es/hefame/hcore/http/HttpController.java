package es.hefame.hcore.http;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;

import es.hefame.hcore.HException;
import es.hefame.hcore.JsonEncodable;
import es.hefame.hcore.http.authentication.Authenticator;
import es.hefame.hcore.http.exchange.HttpConnection;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Superclase para los controladores HTTP. Un controlador HTTP es una clase que
 * procesa las peticiones HTTP entrantes en el servidor. Es responsabilidad de
 * cada controlador sobreescribir los métodos get/post/put/delete para realizar
 * las acciones propias de cada controlador.
 * 
 * @author Alejandro_AC
 *
 */
public abstract class HttpController implements HttpHandler {


	private static final String OPERATION_NOT_IMPLEMENTED = "Operación no implementada";
	private static final String METHOD_NOT_ALLOWED = "Método no permitido";
	private static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";

	/**
	 * Autenticador por defecto que se ejecuta con cada petición HTTP. Cada
	 * instancia puede establecer su autenticador llamando al método
	 * <i>set_authenticator(Authenticator authn)</i>
	 */
	protected static Authenticator defaultAuthenticator = null;

	/**
	 * Autenticador que se ejecuta con cada petición HTTP.
	 */
	protected Authenticator authenticator = null;

	/**
	 * Establece el autenticador por defecto para todos los controladores. El
	 * autenticador es llamado en cada petición que se recibe y decide si la
	 * petición debe ejecutarse o no.
	 * 
	 * Un valor null indica que no se autenticará a los usuarios y que por lo tanto,
	 * todas las peticiones serán procesadas por el controlador. Adicionalmente,
	 * cada controlador puede establecer su propio authenticador con el método
	 * <i>setAuthenticator(Authenticator)</i>.
	 * 
	 * @param authenticator cualquier implementación de HttpAuthenticator que vaya a
	 *                      usarse por defecto en todas las peticiones
	 */
	public static void setDefaultAuthenticator(Authenticator authenticator) {
		defaultAuthenticator = authenticator;
	}

	/**
	 * Esta función es la que llama el servidor HTTP cuando recibe una petición que
	 * debe atenderse en este controlador. Este método construye un objeto
	 * HttpConnection a partir del HttpExchange que el servidor le pasa y llama al
	 * método <i>handle(HttpConnection)</i>
	 * 
	 * @param exchange Datos de la petición HTTP pasados por el servidor
	 */
	@Override
	public void handle(HttpExchange exchange) {
		HttpConnection httpConnection = new HttpConnection(exchange);
		this.handle(httpConnection);
	}

	/**
	 * Esta función autentica la petición entrante con el autenticador establecido,
	 * si lo hubiera y en caso de autenticación positiva, llama al método
	 * get/post/put/delete que corresponda en función del método HTTP. En caso de
	 * autenticación negativa, se intenta enviar la respuesta indicada por el
	 * autenticador.
	 * 
	 * @param exchange Datos de la petición HTTP pasados por el servidor
	 */
	public void handle(HttpConnection exchange) {

		try {
			// AUTENTICACION
			if (!this.authenticate(exchange)) {
				this.onAuthenticationFailure(exchange);
				return;
			}

			// LLAMADA AL METODO ESPECIFICO DEL CONTROLADOR

			switch (exchange.request.getMethod()) {
				case "get":
					this.get(exchange);
					return;
				case "post":
					this.post(exchange);
					return;
				case "put":
					this.put(exchange);
					return;
				case "delete":
					this.delete(exchange);
					return;
				default:
					exchange.response.send(new HttpException(405, METHOD_NOT_ALLOWED));
			}
		} catch (HException he) {
			try {
				exchange.response.send(he);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (Exception e) {
			try {
				exchange.response.send(new HttpException(500, INTERNAL_SERVER_ERROR));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} finally {
			exchange.request.close();
			exchange.response.close();
		}
	}

	/**
	 * Implementación por defecto del método HTTP GET. Responde un mensaje HTTP con
	 * el mensaje 501 - Operación no implementada.
	 * 
	 * @param exchange Datos de la petición HTTP pasados por el servidor
	 * @throws APIException Si falla el procesamiento de la petición.
	 * @throws IOException  Si falla en envío de la respuesta.
	 */
	protected void get(HttpConnection exchange) throws HException, IOException {
		exchange.response.send(new HttpException(501, OPERATION_NOT_IMPLEMENTED));
	}

	/**
	 * Implementación por defecto del método HTTP POST. Responde un mensaje HTTP con
	 * el mensaje 501 - Operación no implementada.
	 * 
	 * @param exchange Datos de la petición HTTP pasados por el servidor
	 * @throws APIException Si falla el procesamiento de la petición.
	 * @throws IOException  Si falla en envío de la respuesta.
	 */
	protected void post(HttpConnection exchange) throws HException, IOException {
		exchange.response.send(new HttpException(501, OPERATION_NOT_IMPLEMENTED));
	}

	/**
	 * Implementación por defecto del método HTTP PUT. Responde un mensaje HTTP con
	 * el mensaje 501 - Operación no implementada.
	 * 
	 * @param exchange Datos de la petición HTTP pasados por el servidor
	 * @throws APIException Si falla el procesamiento de la petición.
	 * @throws IOException  Si falla en envío de la respuesta.
	 */
	protected void put(HttpConnection exchange) throws HException, IOException {
		exchange.response.send(new HttpException(501, OPERATION_NOT_IMPLEMENTED));
	}

	/**
	 * Implementación por defecto del método HTTP DELETE. Responde un mensaje HTTP
	 * con el mensaje 501 - Operación no implementada.
	 * 
	 * @param exchange Datos de la petición HTTP pasados por el servidor
	 * @throws APIException Si falla el procesamiento de la petición.
	 * @throws IOException  Si falla en envío de la respuesta.
	 */
	protected void delete(HttpConnection exchange) throws HException, IOException {
		exchange.response.send(new HttpException(501, OPERATION_NOT_IMPLEMENTED));
	}

	/**
	 * Establece el autenticador para este controlador en concreto. El autenticador
	 * es llamado en cada petición que se recibe y decide si la petición debe
	 * ejecutarse o no.
	 * 
	 * Un valor null indica que no se autenticará a los usuarios y que por lo tanto,
	 * todas las peticiones serán procesadas por el controlador.
	 * 
	 * @param authenticator cualquier implementación de HttpAuthenticator que vaya a
	 *                      usarse por defecto en todas las peticiones
	 */
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * Esta función es llamada al inicio del procesamiento de cada petición
	 * entrante. Esta selecciona el autenticador adecuado y lo ejecuta.
	 * 
	 * @param exchange El objeto con los datos de la petición HTTP
	 * @return true si la petición debe continuar, false de lo contrario
	 */
	protected boolean authenticate(HttpConnection exchange) {
		Authenticator selectedAuthMethod = this.authenticator != null ? authenticator : defaultAuthenticator;

		if (selectedAuthMethod != null) {
			return selectedAuthMethod.authenticateRequest(exchange.request);
		}

		return true;
	}

	/**
	 * Cuando la autenticación falla, se llama a este método para que compruebe si
	 * debe enviar una respuesta de error al cliente ante el fallo en la
	 * autenticación. Para mas información, ver la clase Authenticator.
	 * 
	 * @param exchange El objeto de comunicación HTTP con el cliente
	 * @throws IOException Si falla el envío de la respuesta al cliente.
	 */
	@SuppressWarnings("unchecked")
	private void onAuthenticationFailure(HttpConnection exchange) throws IOException {

		if (!exchange.response.isResponseSent()) {
			Object elObjeto = exchange.request.getInternalValue(Authenticator.ATTRIBUTE_RESPONSE);
			Integer rcode = exchange.request.getInternalValue(Authenticator.ATTRIBUTE_RETURN_CODE, Integer.class);

			Map<String, Set<String>> headers = null;
			try {
				headers = (Map<String, Set<String>>) exchange.request.getInternalValue(Authenticator.ATTRIBUTE_HEADERS);
			} catch (ClassCastException e) {

			}

			if (headers != null) {
				for (Entry<String, Set<String>> header : headers.entrySet()) {
					String key = header.getKey();
					Set<String> values = header.getValue();

					if (key == null || values == null)
						continue;

					for (Object value : values) {
						exchange.response.addHeader(key, value.toString());
					}

				}
			}

			if (elObjeto != null) {

				if (elObjeto instanceof HttpException) {
					exchange.response.send((HttpException) elObjeto);
				} else if (elObjeto instanceof JsonEncodable) {
					exchange.response.send((JsonEncodable) elObjeto, rcode);
				} else {
					if (rcode == null)
						rcode = 401;
					exchange.response.send(elObjeto.toString().getBytes(), rcode, "text/plain");
				}
			} else {
				if (rcode == null)
					rcode = 401;
				exchange.response.send(rcode);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public String toString() {
		JSONObject o = new JSONObject();
		o.put("class", this.getClass().getName());
		if (this.authenticator != null)
			o.put("authenticator", this.authenticator.toString());
		return o.toJSONString();
	}

}
