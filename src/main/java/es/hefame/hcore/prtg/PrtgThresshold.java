package es.hefame.hcore.prtg;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un umbral de un canal PRTG.
 * Aplicado a un canal, establece los valores 'LimitMaxError', 'LimitMaxWarning', 'LimitMinWarning',
 * 'LimitMinError', 'LimitErrorMsg', 'LimitWarningMsg', 'LimitMode' y si es necesario 'Float'.
 * 
 * @author Alejandro_AC
 *
 */
public class PrtgThresshold
{

	private String	min_error			= null;
	private String	min_warning			= null;
	private String	max_warning			= null;
	private String	max_error			= null;

	private boolean	is_decimal			= false;

	private String	limit_warning_msg	= null;
	private String	limit_error_msg		= null;

	/**
	 * Instancia el objeto con los umbrales indicados.
	 * Los umbrales que se establezcan a null se consideran desactivados.
	 * 
	 * @param min_error Umbral por debajo del cual el canal debe ponerse en error.
	 * @param min_warning Umbral por debajo del cual el canal debe ponerse en warning.
	 * @param max_warning Umbral por encima del cual el canal debe ponerse en warning.
	 * @param max_error Umbral por debajo del cual el canal debe ponerse en error.
	 * @param limit_warning_msg Mensaje de texto a mostrar en caso de warning.
	 * @param limit_error_msg Mensaje de texto a mostrar en caso de error.
	 */
	public PrtgThresshold(String min_error, String min_warning, String max_warning, String max_error, String limit_warning_msg, String limit_error_msg)
	{
		this.min_error = min_error != null ? min_error.toString() : null;
		this.min_warning = min_warning != null ? min_warning.toString() : null;
		this.max_warning = max_warning != null ? max_warning.toString() : null;
		this.max_error = max_error != null ? max_error.toString() : null;
		this.limit_warning_msg = limit_warning_msg;
		this.limit_error_msg = limit_error_msg;

		this.is_decimal = false;
		this.is_decimal |= min_error != null && min_error.indexOf(".") > -1;
		this.is_decimal |= min_warning != null && min_warning.indexOf(".") > -1;
		this.is_decimal |= max_error != null && max_error.indexOf(".") > -1;
		this.is_decimal |= max_warning != null && max_warning.indexOf(".") > -1;
	}

	/**
	 * Instancia el objeto con los umbrales indicados.
	 * Los umbrales que se establezcan a null se consideran desactivados.
	 * 
	 * @param min_error Umbral por debajo del cual el canal debe ponerse en error.
	 * @param min_warning Umbral por debajo del cual el canal debe ponerse en warning.
	 * @param max_warning Umbral por encima del cual el canal debe ponerse en warning.
	 * @param max_error Umbral por debajo del cual el canal debe ponerse en error.
	 */
	public PrtgThresshold(String min_error, String min_warning, String max_warning, String max_error)
	{
		this(min_error, min_warning, max_warning, max_error, null, null);
	}

	/**
	 * Instancia el objeto con los umbrales indicados.
	 * Los umbrales que se establezcan a null se consideran desactivados.
	 * 
	 * @param min_error Umbral por debajo del cual el canal debe ponerse en error.
	 * @param min_warning Umbral por debajo del cual el canal debe ponerse en warning.
	 * @param max_warning Umbral por encima del cual el canal debe ponerse en warning.
	 * @param max_error Umbral por debajo del cual el canal debe ponerse en error.
	 * @param limit_warning_msg Mensaje de texto a mostrar en caso de warning.
	 * @param limit_error_msg Mensaje de texto a mostrar en caso de error.
	 */
	public PrtgThresshold(Number min_error, Number min_warning, Number max_warning, Number max_error, String limit_warning_msg, String limit_error_msg)
	{
		this(min_error != null ? min_error.toString() : null, min_warning != null ? min_warning.toString() : null, max_warning != null ? max_warning.toString() : null, max_error != null ? max_error.toString() : null, limit_warning_msg, limit_error_msg);
	}

	/**
	 * Instancia el objeto con los umbrales indicados.
	 * Los umbrales que se establezcan a null se consideran desactivados.
	 * 
	 * @param min_error Umbral por debajo del cual el canal debe ponerse en error.
	 * @param min_warning Umbral por debajo del cual el canal debe ponerse en warning.
	 * @param max_warning Umbral por encima del cual el canal debe ponerse en warning.
	 * @param max_error Umbral por debajo del cual el canal debe ponerse en error.
	 */
	public PrtgThresshold(Number min_error, Number min_warning, Number max_warning, Number max_error)
	{
		this(min_error, min_warning, max_warning, max_error, null, null);
	}

	/**
	 * Indica si alguno de los umbrales tiene un valor decimal
	 * 
	 * @return true si algún umbral es decimal, false de lo contrario.
	 */
	public boolean has_float_value()
	{
		return this.is_decimal;
	}

	/**
	 * Devuelve un mapa con los nodos que deben añadirse a un canal PRTG para que se le apliquen los
	 * umbrales representados por esta clase.
	 * 
	 * @return El mapa de nodos.
	 */
	public Map<String, Object> get_nodes()
	{
		Map<String, Object> rt = new HashMap<String, Object>();

		rt.put("LimitMode", 1);
		if (this.is_decimal) rt.put("Float", 1);
		if (this.max_error != null) rt.put("LimitMaxError", this.max_error);
		if (this.max_warning != null) rt.put("LimitMaxWarning", this.max_warning);
		if (this.min_warning != null) rt.put("LimitMinWarning", this.min_warning);
		if (this.min_error != null) rt.put("LimitMinError", this.min_error);
		if (this.limit_warning_msg != null) rt.put("LimitWarningMsg", this.limit_warning_msg);
		if (this.limit_error_msg != null) rt.put("LimitErrorMsg", this.limit_error_msg);

		return rt;
	}

}
