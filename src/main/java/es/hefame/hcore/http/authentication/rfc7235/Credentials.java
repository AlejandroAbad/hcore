package es.hefame.hcore.http.authentication.rfc7235;

/**
 * Clase que representa una cadena de credenciales 'Credentials' tal como se
 * especifica en rfc7235 sección 2.1:
 * 
 * <pre>
 * 		credentials    = auth-scheme [ 1*SP ( token68 / #auth-param ) ]
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
 * Entrada: Digest username="pepe", realm="api", nonce="9c5867aed9bd6efe79353bc5867aed9", uri="/authenticate", response="bd6efe79353bc5867aed9237c9351151", opaque=""
 * Salida:
 * {
 * 		"schema": "Basic",
 * 		"auth_params": { 
 * 			"username": "pepe", 
 * 			"realm": "api", 
 * 			"nonce": "9c5867aed9bd6efe79353bc5867aed9", 
 * 			"uri": "/authenticate", 
 * 			"response": "bd6efe79353bc5867aed9237c9351151", 
 * 			"opaque": ""
 * 		}
 * }
 * </pre>
 * 
 * @author Alejandro_AC
 *
 */
public class Credentials extends Challenge {

	/**
	 * Una instancia de credenciales con el esquema y los auth-params indicados.
	 * 
	 * @param schema      El esquema utilizado
	 * @param auth_params Una colección de AuthParams
	 */
	public Credentials(String schema, AuthParam... auth_params) {
		super(schema, auth_params);
	}

	/**
	 * Una instancia de credenciales con el esquema y el token68 indicado.
	 * 
	 * @param schema  El esquema utilizado
	 * @param token68 Un token68
	 */
	public Credentials(String schema, String token68) {
		super(schema, token68);
	}

	/**
	 * Una instancia de credenciales con el esquema indicado.
	 * 
	 * @param schema El esquema utilizado
	 */
	public Credentials(String schema) {
		super(schema);
	}

}
