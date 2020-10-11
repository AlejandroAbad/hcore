package es.hefame.hcore.oracle;

public enum OracleLogonType {
	SYSOPER, SYSDBA, SYSASM;

	public static OracleLogonType forName(String name) {
		switch (name.toLowerCase()) {
			case "sysoper":
				return SYSOPER;
			case "sysdba":
				return SYSDBA;
			case "sysasm":
				return SYSASM;
			default:
				return null;
		}
	}
}