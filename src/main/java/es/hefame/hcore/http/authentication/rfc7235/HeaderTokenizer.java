package es.hefame.hcore.http.authentication.rfc7235;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * Analiza un string en formato Response según rfc7235 sección 2.1 y extrae sus
 * valores en un objeto de la clase Credentials.
 * 
 * <pre>
 * 		credentials = auth-scheme [ 1*SP ( token68 / #auth-param ) ]
 * 		auth-scheme    = token
 * 		auth-param     = token BWS "=" BWS ( token / quoted-string )
 * 		token          = 1*tchar
 *      tchar          = "!" / "#" / "$" / "%" / "&amp;" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~" / DIGIT / ALPHA
 *      token68        = 1 * (ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/") * "="
 * </pre>
 * 
 * Por ejemplo, dada la cabecera: Basic 789ab824bed8db7da11b2= realm="reino" se
 * trocea en [Basic, 789ab824bed8db7da11b2=, realm="reino"]
 * 
 * @author Alejandro_AC
 */
public class HeaderTokenizer {

	private enum ReadStatus {
		BEGIN, SCHEMA, TOKEN68, AUTHPARAMS
	};

	public static final String TOKEN_REGEX = "[a-zA-Z0-9\\$%&'\\*\\+\\-\\.\\^`\\|~_!#]+";
	public static final String TOKEN68_REGEX = "[a-zA-Z0-9\\+\\-\\.~_]+=*";
	public static final String AUTHPARAM_REGEX = TOKEN_REGEX + "=" + TOKEN_REGEX + "|" + TOKEN_REGEX + "=\"[^\"]*\"";
	public static final String SPLIT_REGEX = "(" + AUTHPARAM_REGEX + "|" + TOKEN68_REGEX + "|" + TOKEN_REGEX + ")";
	private static Pattern pattern = Pattern.compile(SPLIT_REGEX);

	/**
	 * Analiza un string en formato Response según rfc7235 sección 2.1 y extrae sus
	 * valores en un objeto de la clase Credentials.
	 * 
	 * <pre>
	 * 		credentials = auth-scheme [ 1*SP ( token68 / #auth-param ) ]
	 * 		auth-scheme    = token
	 * 		auth-param     = token BWS "=" BWS ( token / quoted-string )
	 * 		token          = 1*tchar
	 *      tchar          = "!" / "#" / "$" / "%" / "&amp;" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~" / DIGIT / ALPHA
	 *      token68        = 1 * (ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/") * "="
	 * </pre>
	 * 
	 * Por ejemplo, dada la cabecera: Basic 789ab824bed8db7da11b2= realm="reino" se
	 * trocea en [Basic, 789ab824bed8db7da11b2=, realm="reino"]
	 * 
	 * @param string La cadena en formato Credentials.
	 * @return El objeto credentials representando la cadena pasada como parámetro.
	 */
	public static Credentials extractCredentials(String string) {
		Credentials credentials = null;

		List<String> tokens = HeaderTokenizer.tokenize(string);
		ReadStatus status = ReadStatus.BEGIN;

		for (String s : tokens) {
			if (s == null)
				continue;

			switch (status) {
				case BEGIN:
					if (HeaderTokenizer.isValidToken(s)) {
						credentials = new Credentials(s);
						status = ReadStatus.SCHEMA;
					} else {
						return null;
					}
					break;
				case SCHEMA:
					if (HeaderTokenizer.isValidToken68(s)) {
						if (credentials == null || !credentials.setToken68(s))
							return null;
						status = ReadStatus.TOKEN68;
					} else {
						AuthParam authParam = HeaderTokenizer.extractAuthparam(s);
						if (authParam != null) {
							if (credentials != null)
								credentials.addAuthParam(authParam);
							status = ReadStatus.AUTHPARAMS;
						} else {
							return null;
						}
					}
					break;
				case TOKEN68:
					return null;
				case AUTHPARAMS:
					AuthParam auth_param = HeaderTokenizer.extractAuthparam(s);
					if (credentials != null && auth_param != null) {
						credentials.addAuthParam(auth_param);
					} else {
						return null;
					}
					break;
			}
		}

		return credentials;
	}

	/**
	 * Trocea un string en formato Response según rfc7235 sección 2.1 en tokens de
	 * menor tamaño. Por ejemplo: Basic 789ab824bed8db7da11b2= realm="reino" se
	 * trocea en [Basic, 789ab824bed8db7da11b2=, realm="reino"]
	 * 
	 * @param string El string a trocear.
	 * @return
	 */
	private static List<String> tokenize(String string) {
		List<String> list = new ArrayList<>();
		Matcher m = pattern.matcher(" " + string);
		while (m.find()) {
			// First non-null match.
			for (int i = 1; i <= m.groupCount(); i++) {
				if (m.group(i) != null) {
					list.add(m.group(i).trim());
					break;
				}
			}
		}
		return list;
	}

	/**
	 * Indica si un valor dado es un auth-param válido según el rfc7235 sección 2.1:
	 * 
	 * <pre>
	 *      auth-param     = token BWS "=" BWS ( token / quoted-string )
	 * </pre>
	 * 
	 * Los auth-param son por lo general parámetros de la autenticación, como por
	 * ejemplo 'realm="mi reino por una patata"'.
	 * 
	 * @param token El token a comprobar
	 * @return true si el auth-token es válido, false de lo contrario
	 */
	private static AuthParam extractAuthparam(String token) {
		if (token.matches(AUTHPARAM_REGEX)) {
			int pos = token.indexOf('=');
			String key = token.substring(0, pos);
			String value = token.substring(pos + 1, token.length());
			return new AuthParam(key, value, true);
		} else {
			return null;
		}
	}

	/**
	 * Indica si un valor dado es un token68 válido según el rfc7235 sección 2.1:
	 * 
	 * <pre>
	 * token68 = 1 * (ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/") * "="
	 * </pre>
	 * 
	 * El token68 es generalmente el string en base64 que lleva las credenciales del
	 * usuario en el esquema Basic.
	 * 
	 * @param token El token a comprobar
	 * @return true si el token68 es válido, false de lo contrario
	 */
	private static boolean isValidToken68(String token) {
		return token.matches(TOKEN68_REGEX);
	}

	/**
	 * Indica si un valor dado es un token válido según el rfc7230 sección 3.2.6:
	 * 
	 * <pre>
	 *      token          = 1*tchar
	 *      tchar          = "!" / "#" / "$" / "%" / "&" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~" / DIGIT / ALPHA
	 * </pre>
	 * 
	 * Un ejemplo de token es el esquema de autenticación (Basic, Digest, Bearer
	 * ...)
	 * 
	 * @param token El token a comprobar
	 * @return true si el token es válido, false de lo contrario
	 */
	private static boolean isValidToken(String token) {
		return token.matches(TOKEN_REGEX);
	}

}
