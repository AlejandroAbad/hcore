package es.hefame.hcore.http.authentication.rfc7235.rfc7617;

import java.nio.charset.Charset;

import es.hefame.hcore.converter.ByteArrayConverter;
import es.hefame.hcore.http.authentication.Authenticator;
import es.hefame.hcore.http.authentication.rfc7235.AuthParam;
import es.hefame.hcore.http.authentication.rfc7235.Challenge;
import es.hefame.hcore.http.authentication.rfc7235.Credentials;
import es.hefame.hcore.http.authentication.rfc7235.HeaderTokenizer;
import es.hefame.hcore.http.exchange.IHttpRequest;

/**
 * Implementación del método de autenticación HTTP Basic según el rfc7617. Es
 * importante conocer el protocolo para comprender el funcionamiento de esta
 * clase.
 * 
 * https://tools.ietf.org/html/rfc7617 https://tools.ietf.org/html/rfc7235
 * 
 * @author Alejandro_AC
 *
 */
public class BasicAuthenticator extends Authenticator {

	protected static final String AUTHORIZATION_HEADER = "Authorization";
	protected static final String AUTHENTICATE_HEADER = "WWW-Authenticate";

	private String realm;
	private String charset;
	private Challenge challenge;
	private BasicPasswordMatcher passwordMatcher;

	/**
	 * Inicia el autenticador con el realm y el charset indicados. También necesita
	 * un objeto que implemente PasswordMatcher, que compruebe las credenciales del
	 * usuario.
	 * 
	 * @param realm           El campo realm indicado en la cabecera 'Challenge'
	 * @param charset         El charset indicado en la cabecera 'Challenge'. Se
	 *                        recomienda utilizar siempre UTF-8.
	 * @param passwordMatcher Un objeto que implemente PasswordMatcher, para
	 *                        comprobar las credenciales del usuario.
	 */
	public BasicAuthenticator(String realm, String charset, BasicPasswordMatcher passwordMatcher) {
		this.realm = realm;
		this.charset = charset;
		this.passwordMatcher = passwordMatcher;
		this.challenge = new Challenge("Basic", new AuthParam("realm", this.realm, false),
				new AuthParam("charset", this.charset, false));
	}

	/**
	 * Inicia el autenticador con el realm indicado y el charset UTF-8. También
	 * necesita un objeto que implemente PasswordMatcher, que compruebe las
	 * credenciales del usuario.
	 * 
	 * @param realm           El campo realm indicado en la cabecera 'Challenge'
	 * @param passwordMatcher Un objeto que implemente PasswordMatcher, para
	 *                        comprobar las credenciales del usuario.
	 */
	public BasicAuthenticator(String realm, BasicPasswordMatcher passwordMatcher) {
		// UTF-8 es el único soportado en rfc7617
		this(realm, "UTF-8", passwordMatcher);
	}

	/**
	 * Este método es el que llama el controlador con la petición del usuario a
	 * autenticar
	 * 
	 * @param request La petición HTTP a autenticar.
	 */
	@Override
	public boolean authenticateRequest(IHttpRequest request) {
		request.setInternalValue("auth.authenticator", this.getClass());

		if (parseAuthorizationHeader(request))
			return true;

		this.addChallenge(request);
		return false;
	}

	/**
	 * Establece los atributos que el controlador debe responder al cliente en caso
	 * de autenticación fallida. En concreto, establece la cabecera WWW-Authenticate
	 * y el código de retorno HTTP 401.
	 * 
	 * @param request La petición HTTP donde añadir los atributos.
	 */
	private void addChallenge(IHttpRequest request) {
		Authenticator.setAuthHttpCode(401, request);
		Authenticator.addAuthHeader(AUTHENTICATE_HEADER, this.challenge.toString(), request);
	}

	/**
	 * Analiza las cabeceras de la petición HTTP para extraer el nombre del usuario
	 * y la contraseña y utiliza el PasswordMatcher de la clase para comprobar si
	 * son correctos o no.
	 * 
	 * @param request La petición HTTP a autenticar.
	 * @return true si las credenciales son validadas por el PasswordMatcher, false
	 *         de lo contrario.
	 */
	private boolean parseAuthorizationHeader(IHttpRequest request) // throws APIHttpException
	{
		String x = request.getHeader(AUTHORIZATION_HEADER);

		if (x == null) {
			return false;
		}

		Credentials credentials = HeaderTokenizer.extractCredentials(x);
		if (credentials == null) {
			return false;
		}

		String authnToken = null;
		if (credentials.getSchema().equalsIgnoreCase("basic")) {
			authnToken = credentials.getToken68();
			if (authnToken == null) {
				// Cabecera 'Authorization' mal formada
				return false;
			}
		} else {
			// Las credenciales no indican el esquema de autenticacion RFC7235 Basic
			return false;
		}

		byte[] decodedb = ByteArrayConverter.fromBase64(authnToken);
		String decoded = new String(decodedb, Charset.forName(this.charset));

		String username;
		String password;

		String[] userPass = decoded.split(":");
		if (userPass.length < 2) {
			if (decoded.endsWith(":")) {
				username = userPass[0];
				password = "";
			} else {
				// El token68 de la cabecera no es de la forma 'username:password'
				return false;
			}
		} else {
			username = userPass[0];
			password = userPass[1];

			// Si hay mas de 2 tokens, es porque la password contiene el caracter ':'.
			// Esto se contempla en el RFC, tenemos que unir las piezas.
			if (userPass.length > 2) {
				for (int i = 2; i < userPass.length; i++) {
					password += ':' + userPass[i];
				}
			}
		}

		return passwordMatcher.matchPassword(this.realm, username, password, request);

	}

}
