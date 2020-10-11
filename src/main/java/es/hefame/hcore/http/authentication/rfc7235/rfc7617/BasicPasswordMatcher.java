package es.hefame.hcore.http.authentication.rfc7235.rfc7617;

import es.hefame.hcore.http.exchange.IHttpRequest;

/**
 * Interfaz para la comunicación con la fuente de datos que alberga las credenciales de los usuarios.
 * Permite comprobar las credenciales extraídas del método HTTP Basic Authentication (RFC7617) que son:
 * <ul>
 * <li>realm</li>
 * <li>username</li>
 * <li>password</li>
 * </ul>
 * 
 * https://tools.ietf.org/html/rfc7617
 * 
 * @author Alejandro_AC
 *
 */
public interface BasicPasswordMatcher
{
	/**
	 * Este método debe realizar la comprobación de las credenciales del usuario
	 * utilizando la fuente de datos que cada implementación decida.
	 * 
	 * @param realm El realm del autenticador.
	 * @param username El nombre del usuario.
	 * @param password La contraseña del usuario.
	 * @params request La petición HTTP que se está autenticando.
	 * @return true si las credenciales son correctas, false de lo contrario.
	 */
	public boolean matchPassword(String realm, String username, String password, IHttpRequest request);
}
