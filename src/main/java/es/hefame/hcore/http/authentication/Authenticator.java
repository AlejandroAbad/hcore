package es.hefame.hcore.http.authentication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.hefame.hcore.JsonEncodable;
import es.hefame.hcore.http.exchange.IHttpRequest;

/**
 * Interfaz para clases que se encargan de autenticar peticiones HTTP.
 * Las implementaciones disponen del objeto HttpConnection con todos los datos de la petición
 * que pueden utilizar para autenticar la misma.
 * 
 * @author Alejandro_AC
 *
 */
public abstract class Authenticator
{
	
	/**
	 * Nombre del atributo que se usa para pasar la respueta JSON que el controlador debe enviar en caso de fallo de la autenticación.
	 */
	public static final String	ATTRIBUTE_RESPONSE		= "auth_response";

	/**
	 * Nombre del atributo que se usa para pasar el código de respuesta HTTP que el controlador debe enviar en caso de fallo de la autenticación.
	 */
	public static final String	ATTRIBUTE_RETURN_CODE	= "auth_http_code";

	/**
	 * Nombre del atributo que se usa para pasar cabeceras HTTP que el controlador debe enviar en caso de fallo de la autenticación.
	 */
	public static final String	ATTRIBUTE_HEADERS		= "auth_headers";

	/**
	 * Realiza la autenticación de la petición del usuario.
	 * 
	 * Un valor de retorno true indica que el usuario está autenticado y que el controlador puede continuar
	 * su ejecución. Un valor de retorno false indica al controlador que debe abortar su ejecución.
	 * 
	 * Adicionalmente, el autenticador puede utilizar el método set_attribute de la clase IHttpRequest para
	 * pasar información al controlador. Existen varios atributos que tienen un significado especial para el controlador,
	 * pero solo se utilizán cuando el resultado de la autenticación es false:
	 * <ul>
	 * <li><b>Authenticator.ATTRIBUTE_RESPONSE</b>: Un objeto de tipo JsonEncodable que será codificado y enviado al cliente. Adicionalmente, si este objeto es un tipo de APIHttpExcepion, el código de retorno será el retornado por su método <i>get_http_code()</i>. Si no se especifica este objeto o no es de tipo adecuado, no se envía cuerpo al cliente.</li>
	 * <li><b>Authenticator.ATTRIBUTE_RETURN_CODE</b>: Código HTTP de la respuesta. Si el objeto pasado en el parámetro <i>auth_exception</i> es de tipo APIHttpException, se ignora en pos del que indique la excepción. Por defecto, se asume el valor 401.</li>
	 * <li><b>Authenticator.ATTRIBUTE_HEADERS</b>: una implementación de Map&lt;String, Set&lt;String&gt;&gt; con las cabeceras que deberán adjuntarse en la respuesta.</li>
	 * </ul>
	 * 
	 * Existen métodos para facilitar esta labor: <i>set_auth_response</i>, <i>set_auth_http_code</i> y <i>add_auth_header</i>
	 * 
	 * @param request Petición HTTP entrante.
	 * @return true si el controlador debe continuar con su ejecución, false de lo contrario.
	 */
	public abstract boolean authenticateRequest(IHttpRequest request);

	/**
	 * Establece el objeto JsonEncodable que será enviado por el controlador en caso de que la autenticación del usuario fracase.
	 * El método guarda el objeto JsonEncodabe en los atributos "out-of-band" de la petición HTTP (ver método set_attribute(String, Object) de la clase HttpRequest)
	 * con la clave Authenticator.ATTRIBUTE_RESPONSE.
	 * 
	 * @param jsonObject Un objeto JsonEncodable.
	 * @param request El objeto HttpRequest sobre el que guardar el atributo.
	 */
	public static void setAuthResponse(JsonEncodable jsonObject, IHttpRequest request)
	{
		request.setInternalValue(Authenticator.ATTRIBUTE_RESPONSE, jsonObject);
	}

	/**
	 * Establece el valor de retorno de la petición HTTP que será enviado por el controlador en caso de que la autenticación del usuario fracase.
	 * El método guarda el valor en los atributos "out-of-band" de la petición HTTP (ver método set_attribute(String, Object) de la clase HttpRequest)
	 * con la clave Authenticator.ATTRIBUTE_RETURN_CODE.
	 * 
	 * <b>Nota:</b> Si no se establece este parámetro, en caso de que falle la autenticación se usará por defecto el valor 401.
	 * <b>Nota:</b> Si se establece un objeto que sea subclase de APIHttpException, con el método set_auth_response, este parámetro se ignora en pos de lo que indica la excepción.
	 * 
	 * @param code El código HTTP de retorno.
	 * @param request El objeto HttpRequest sobre el que guardar el atributo.
	 */
	public static void setAuthHttpCode(int code, IHttpRequest request)
	{
		request.setInternalValue(Authenticator.ATTRIBUTE_RETURN_CODE, code);
	}

	/**
	 * Añade una cabecera para ser enviada con la petición HTTP en caso de que la autenticación del usuario fracase.
	 * El método guarda el valor en los atributos "out-of-band" de la petición HTTP (ver método set_attribute(String, Object) de la clase HttpRequest)
	 * con la clave Authenticator.ATTRIBUTE_HEADERS.
	 * 
	 * Si ya existe la cabecera en los atributos de la petición HTTP, el nuevo valor se añade al conjunto de valores de la cabecera.
	 * 
	 * @param key En nombre de la cabecera.
	 * @param value El valor a añadir a la cabecera indicada.
	 * @param request El objeto HttpRequest sobre el que guardar el atributo.
	 */
	@SuppressWarnings("unchecked")
	public static void addAuthHeader(String key, String value, IHttpRequest request)
	{

		
		Map<String, Set<String>> headers = request.getInternalValue(Authenticator.ATTRIBUTE_HEADERS, Map.class);

		if (headers == null)
		{
			headers = new HashMap<>();
			Set<String> values = new HashSet<>(2);
			values.add(value);
			headers.put(key, values);
			request.setInternalValue(Authenticator.ATTRIBUTE_HEADERS, headers);
		}
		else
		{
			Set<String> values = headers.get(key);
			if (values == null)
			{
				values = new HashSet<>(1);
				values.add(value);
				headers.put(key, values);
			}
			else
			{
				values.add(value);
			}
		}
	}
}
