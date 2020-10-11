package es.hefame.hcore.prtg;

/**
 * Enumerado de unidades definidas por PRTG. Si se especifica una unidad que NO
 * esté en esta lista, el canal indicará el valor de unidad 'Custom' y se
 * indicará en el campo 'customUnit' la unidad indicada.
 */
public enum DefinedUnit {

	BYTES_BANDWIDTH("BytesBandwidth"), BYTES_MEMORY("BytesMemory"), BYTES_DISK("BytesDisk"),
	TEMPERATURE("Temperature"), PERCENT("Percent"), TIME_RESPONSE("TimeResponse"), TIME_SECONDS("TimeSeconds"),
	CUSTOM("Custom"), COUNT("Count"), CPU("CPU"), BYTES_FILE("BytesFile"), SPEED_DISK("SpeedDisk"),
	SPEED_NET("SpeedNet"), TIME_HOURS("TimeHours");

	public final String unitName;
	public final String unitNameLowerCase;

	private DefinedUnit(String unitName) {
		this.unitName = unitName;
		this.unitNameLowerCase = this.unitName.toLowerCase();
	}

	public static DefinedUnit fromName(String unitName) {
		unitName = unitName.toLowerCase();
		for (DefinedUnit unit : DefinedUnit.values()) {
			if (unitName.equals(unit.unitNameLowerCase))
				return unit;
		}
		return null;
	}

}