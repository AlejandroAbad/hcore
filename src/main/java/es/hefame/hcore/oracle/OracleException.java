package es.hefame.hcore.oracle;

import java.sql.SQLException;

import org.json.simple.JSONObject;

import es.hefame.hcore.HException;

/**
 * Subclase de APIHttpException que se crea a partir de una SQLException
 * producida por el driver de oracle. Pretende aunar las características de una
 * APIHttpException (depuración, serialización) con la información de una
 * SQLException (consulta SQL, servidor, ...)
 * 
 * @author Alejandro_AC
 *
 */
public class OracleException extends HException {
	private static final long serialVersionUID = -4110131431365149305L;

	/**
	 * La SQLException de esta excepción.
	 */
	private final SQLException sqlException;

	/**
	 * Crea la clase con código de error HTTP 500 con la SQLException.
	 * 
	 * @param cause La SQLException causante de esta excepción.
	 */
	public OracleException(SQLException cause) {
		super(cause.getMessage(), cause);
		this.sqlException = cause;
	}

	@Override
	public String toString() {
		return this.jsonEncode().toJSONString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject jsonEncode() {
		JSONObject jsonRoot = (JSONObject) super.jsonEncode();
		jsonRoot.put("oracle_code", this.sqlException.getErrorCode());
		return jsonRoot;
	}

}
