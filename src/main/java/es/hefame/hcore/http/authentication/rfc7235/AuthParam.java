package es.hefame.hcore.http.authentication.rfc7235;

/**
 * Representación de la sintaxis 'auth-param' según rfc7235 sección 2.1
 * 
 * <pre>
 * 		auth-param     = token BWS "=" BWS ( token / quoted-string )
 * 		token          = 1*tchar
 *      tchar          = "!" / "#" / "$" / "%" / "&amp;" / "'" / "*" / "+" / "-" / "." / "^" / "_" / "`" / "|" / "~" / DIGIT / ALPHA
 * </pre>
 * 
 * @author Alejandro_AC
 *
 */
public class AuthParam
{
	public final String	key;
	public final String	value;

	/**
	 * Crea un auth-param con la clave y el valor indicados.
	 * El parámetro <i>parsing</i> indica que los valores han sido extraidos de una cadena proporcionada por el cliente,
	 * y que por lo tanto, deben eliminarse las comillas dobles al inicio y final del valor, si existen.
	 * 
	 * @param key La clave del parámetro
	 * @param value El valor del parámetro
	 * @param parsing Indica si estamos pasando el valor extraido de una petición de cliente. Si es false, no se analiza el valor de <i>value</i>
	 */
	public AuthParam(String key, String value, boolean parsing)
	{
		this.key = key;
		if (parsing)
		{
			if (value.startsWith("\"") && value.endsWith("\"")) value = value.substring(1, value.length() - 1);
			this.value = value;
		}
		else
		{
			this.value = value.replaceAll("\"", "\\\"");
		}

	}

	/**
	 * Devuelve una representación válida para usar en una cadena 'Challenge' o 'Credentials' según el rfc7235.
	 * Por ejemplo: realm="my realm"
	 * 
	 * @return Representación del auth-param válida para el rfc7235.
	 */
	public String toString()
	{
		return key + '=' + '"' + value + '"';
	}
}
