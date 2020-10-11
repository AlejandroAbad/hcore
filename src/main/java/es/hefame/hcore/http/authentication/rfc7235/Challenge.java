package es.hefame.hcore.http.authentication.rfc7235;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase que representa una cadena 'Challenge' tal como se especifica en rfc7235
 * sección 2.1:
 * 
 * <pre>
 * 		challenge      = auth-scheme [ 1*SP ( token68 / #auth-param ) ]
 * 		auth-scheme    = token
 * 		auth-param     = token BWS "=" BWS ( token / quoted-string )
 * 		token          = 1*tchar
 *      tchar          = "!" / "#" / "$" / "%" / "&amp;" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~" / DIGIT / ALPHA
 *      token68        = 1 * (ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/") * "="
 * </pre>
 * 
 * El rfc7235 indica que no pueden convivir el 'token68' con 'auth-params': Un
 * ejemplo con 'token68':
 * 
 * <pre>
 * Entrada: Basic 789ab824bed8db7da11b2=
 * Salida:
 * {
 * 		"schema": "Basic",
 * 		"token68": "789ab824bed8db7da11b2="
 * }
 * </pre>
 * 
 * Un ejemplo con 'auth-param':
 * 
 * <pre>
 * Entrada: Digest realm="api", nonce="9c5867aed9bd6efe79353bc5867aed9"
 * Salida:
 * {
 * 		"schema": "Digest",
 * 		"auth_params": {
 * 			"realm": "api", 
 * 			"nonce": "9c5867aed9bd6efe79353bc5867aed9"
 * 		}
 * }
 * </pre>
 * 
 * @author Alejandro_AC
 *
 */
public class Challenge {

	/**
	 * El esquema.
	 */
	private String schema;

	/**
	 * El token68 o null si no se especifica.
	 */
	private String token68 = null;

	/**
	 * La lista de 'auth-params' o null si no se especifica ninguno.
	 */
	private List<AuthParam> authParams = null;

	/**
	 * Una instancia de challenge con el esquema indicado.
	 * 
	 * @param schema El esquema utilizado
	 */
	public Challenge(String schema) {
		this.schema = schema;
	}

	/**
	 * Una instancia de challenge con el esquema y el token68 indicado.
	 * 
	 * @param schema  El esquema utilizado
	 * @param token68 Un token68
	 */
	public Challenge(String schema, String token68) {
		this.schema = schema;
		this.token68 = token68;
	}

	/**
	 * Una instancia de challenge con el esquema y los auth-params indicados.
	 * 
	 * @param schema     El esquema utilizado
	 * @param authParams Una colección de AuthParams
	 */
	public Challenge(String schema, AuthParam... authParams) {
		this.schema = schema;
		this.authParams = Arrays.asList(authParams);
	}

	/**
	 * Obtiene el token68
	 * 
	 * @return El token68 o null si no existe
	 */
	public String getToken68() {
		return token68;
	}

	/**
	 * Establece el token68. Si existen 'auth-params' en este objeto, no se modifica
	 * el mismo y se devuelve false.
	 * 
	 * @param token68 El token68 a establecer
	 * @return true si se pudo realizar el cambio o false de lo contrario.
	 */
	public boolean setToken68(String token68) {
		if (authParams == null) {
			this.token68 = token68;
			return true;
		}
		return false;
	}

	/**
	 * Obtiene una lista con los 'auth-params'
	 * 
	 * @return La lista de 'auth-params' o null si no existe
	 */
	public List<AuthParam> getAuthParams() {
		return authParams;
	}

	/**
	 * Establece los 'auth-params' especificados. Si existe un 'token68' en este
	 * objeto, no se modifica el mismo y se devuelve false.
	 * 
	 * @param authParams La colección de 'auth-params' a establecer.
	 * @return true si se pudo realizar el cambio o false de lo contrario.
	 */
	public boolean setAuthParams(AuthParam... authParams) {
		if (this.token68 == null) {
			this.authParams = Arrays.asList(authParams);
			return true;
		}
		return false;
	}

	/**
	 * Obtiene el esquema de autenticación.
	 * 
	 * @return El esquema de autenticación.
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * Añade un 'auth-param' a la lista. Si existe un 'token68' en este objeto, no
	 * se modifica el mismo y se devuelve false.
	 * 
	 * @param param El 'auth-param' a establecer.
	 * @return true si se pudo realizar el cambio o false de lo contrario.
	 */
	public boolean addAuthParam(AuthParam param) {
		if (this.token68 == null) {
			if (this.authParams == null)
				this.authParams = new LinkedList<>();
			this.authParams.add(param);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Añade un 'auth-param' a la lista. Si existe un 'token68' en este objeto, no
	 * se modifica el mismo y se devuelve false.
	 * 
	 * @param key   La clave del parámetro de authenticación.
	 * @param value El valor de la clave.
	 * @return true si se pudo realizar el cambio o false de lo contrario.
	 */
	public boolean addAuthParam(String key, String value) {
		return this.addAuthParam(new AuthParam(key, value, false));
	}

	/**
	 * Construye la cadena de la cabecera válida según el rfc7235 con la información
	 * del objeto.
	 * 
	 * @return La cadena de la cabecera válida según el rfc7235 con la información
	 *         del objeto.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(this.schema);
		if (this.token68 != null) {
			sb.append(' ').append(this.token68);
		} else if (this.authParams != null) {
			boolean first = true;
			for (AuthParam param : this.authParams) {
				if (param == null)
					continue;

				if (first)
					first = false;
				else
					sb.append(',');

				sb.append(' ').append(param.toString());
			}
		}
		return sb.toString();

	}

}
