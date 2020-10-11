package es.hefame.hcore.prtg;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Representa un resultado de error en PRTG.
 * 
 * @author Alejandro_AC
 *
 */
public class PrtgErrorResult implements PrtgResult
{
	private String errorMessage = null;

	/**
	 * Instancia el resultado con el mensaje de error indicado .
	 * 
	 * @param errorMessage El mensaje de error.
	 */
	public PrtgErrorResult(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * No es muy util...
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONAware jsonEncode()
	{
		JSONObject root = new JSONObject();
		root.put("error", 1);
		root.put("text", this.errorMessage);
		return root;
	}

	/**
	 * Obtiene el mensaje de error del resultado.
	 * 
	 * @return El mensaje de error del resultado.
	 */
	public String getErrorMessage()
	{
		return this.errorMessage;
	}

}
