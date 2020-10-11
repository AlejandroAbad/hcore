package es.hefame.hcore.prtg;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * Representa un canal PRTG.
 * https://prtg.paessler.com/api.htm?tabid=7&amp;username=demo&amp;password=demodemo
 * 
 * @author Alejandro_AC
 *
 */
public class PrtgChannelResult implements PrtgResult {

	private String channelName = null;
	private String value = null;
	private boolean isFloat = false;
	private String unit = null;
	private String customUnit = null;
	private PrtgThresshold thressholds = null;
	private boolean showInChart = true;
	private boolean showInTable = true;
	private String valueLookup = null;

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name        El nombre del canal.
	 * @param value       El valor del canal.
	 * @param unit        La unidad del canal.
	 * @param thressholds Los umbrales del canal.
	 */
	public PrtgChannelResult(String name, String value, DefinedUnit unit, PrtgThresshold thressholds) {
		this.channelName = name;
		this.value = value;
		this.unit = unit.unitName;
		this.thressholds = thressholds;
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name        El nombre del canal.
	 * @param value       El valor del canal.
	 * @param unit        La unidad del canal.
	 * @param thressholds Los umbrales del canal.
	 */
	public PrtgChannelResult(String name, String value, String unit, PrtgThresshold thressholds) {
		this.channelName = name;
		this.value = value;

		DefinedUnit definedUnit = DefinedUnit.fromName(unit);

		if (definedUnit == null) {
			this.unit = DefinedUnit.CUSTOM.unitName;
			this.customUnit = unit;
		} else {
			this.unit = definedUnit.unitName;
		}

		this.thressholds = thressholds;
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name        El nombre del canal.
	 * @param value       El valor del canal.
	 * @param unit        La unidad del canal.
	 * @param thressholds Los umbrales del canal.
	 */
	public PrtgChannelResult(String name, long value, DefinedUnit unit, PrtgThresshold thressholds) {
		this(name, Long.toString(value), unit, thressholds);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name        El nombre del canal.
	 * @param value       El valor del canal.
	 * @param unit        La unidad del canal.
	 * @param thressholds Los umbrales del canal.
	 */
	public PrtgChannelResult(String name, long value, String unit, PrtgThresshold thressholds) {
		this(name, Long.toString(value), unit, thressholds);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name        El nombre del canal.
	 * @param value       El valor del canal.
	 * @param unit        La unidad del canal.
	 * @param thressholds Los umbrales del canal.
	 */
	public PrtgChannelResult(String name, double value, DefinedUnit unit, PrtgThresshold thressholds) {
		this(name, Double.toString(value), unit, thressholds);
		this.isFloat = true;
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name        El nombre del canal.
	 * @param value       El valor del canal.
	 * @param unit        La unidad del canal.
	 * @param thressholds Los umbrales del canal.
	 */
	public PrtgChannelResult(String name, double value, String unit, PrtgThresshold thressholds) {
		this(name, Double.toString(value), unit, thressholds);
		this.isFloat = true;
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name  El nombre del canal.
	 * @param value El valor del canal.
	 * @param unit  La unidad del canal.
	 */
	public PrtgChannelResult(String name, String value, DefinedUnit unit) {
		this(name, value, unit, null);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name  El nombre del canal.
	 * @param value El valor del canal.
	 * @param unit  La unidad del canal.
	 */
	public PrtgChannelResult(String name, String value, String unit) {
		this(name, value, unit, null);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name  El nombre del canal.
	 * @param value El valor del canal.
	 * @param unit  La unidad del canal.
	 */
	public PrtgChannelResult(String name, long value, DefinedUnit unit) {
		this(name, value, unit, null);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name  El nombre del canal.
	 * @param value El valor del canal.
	 * @param unit  La unidad del canal.
	 */
	public PrtgChannelResult(String name, long value, String unit) {
		this(name, value, unit, null);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name  El nombre del canal.
	 * @param value El valor del canal.
	 * @param unit  La unidad del canal.
	 */
	public PrtgChannelResult(String name, double value, DefinedUnit unit) {
		this(name, value, unit, null);
	}

	/**
	 * Instancia un canal PRTG con los valores indicados.
	 * 
	 * @param name  El nombre del canal.
	 * @param value El valor del canal.
	 * @param unit  La unidad del canal.
	 */
	public PrtgChannelResult(String name, double value, String unit) {
		this(name, value, unit, null);
	}

	/**
	 * Establece los umbrales del canal.
	 * 
	 * @param thressholds Los umbrales del canal.
	 */
	public void setThressholds(PrtgThresshold thressholds) {
		this.thressholds = thressholds;
	}

	/**
	 * Establece el valor 'ShowTable' del canal. Indica a PRTG si este canal debe
	 * mostrarse en la tabla de resultados.
	 * 
	 * @param flag El valor a establecer.
	 */
	public void setShowInTable(boolean flag) {
		this.showInTable = flag;
	}

	/**
	 * Establece el valor 'ShowChart' del canal. Indica a PRTG si este canal debe
	 * mostrarse en las gráficas.
	 * 
	 * @param flag El valor a establecer.
	 */
	public void setShowInChart(boolean flag) {
		this.showInChart = flag;
	}

	/**
	 * Establece el valor 'ValueLookup' del canal.
	 * 
	 * @param valueLookup El valor del atributo.
	 */
	public void setValueLookup(String valueLookup) {
		this.valueLookup = valueLookup;
	}

	/**
	 * Devuelve un objeto JSON que representa el canal tal y como se especifica en
	 * el API de PRTG.
	 * https://prtg.paessler.com/api.htm?tabid=7&amp;username=demo&amp;password=demodemo
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONAware jsonEncode() {
		JSONObject root = new JSONObject();

		root.put("Channel", this.channelName);
		root.put("Value", this.value);
		root.put("Unit", this.unit);
		if (customUnit != null)
			root.put("CustomUnit", this.customUnit);
		if (this.isFloat)
			root.put("Float", 1);
		if (this.thressholds != null)
			root.putAll(thressholds.get_nodes());
		if (!this.showInChart)
			root.put("ShowChart", 0);
		if (!this.showInTable)
			root.put("ShowTable", 0);
		if (this.valueLookup != null)
			root.put("ValueLookup", this.valueLookup);

		return root;
	}

}
