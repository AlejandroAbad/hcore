package es.hefame.hcore.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import es.hefame.hcore.HException;

/**
 * Clase que provee métodos estáticos para acceso a bases de datos Oracle. TODO:
 * Permitir múltiples conexiónes simultaneas a distintos Oracles.
 * 
 * @author Alejandro_AC
 */
public class DBConnection {

	private DBConnection () {

	}

	// private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

	private static Connection connection = null;

	private static String dbTns = null;
	private static String dbUser = null;
	private static String dbPass = null;
	private static OracleLogonType internalLogon = null;

	/**
	 * Establece los parámetros de conexión con la base de datos Oracle.
	 * 
	 * @param tns  Cadena de conexión Oracle TNS.
	 * @param user Usuario de la base de datos.
	 * @param pass Contraseña del usuario.
	 */
	public static void setConnectionParameters(String tns, String user, String pass) {
		dbTns = tns;
		dbUser = user;
		dbPass = pass;

		String[] parts = dbUser.toLowerCase().split(" as ");
		if (parts.length == 2) {
			internalLogon = OracleLogonType.forName(parts[1]);
			dbUser = parts[0];
		} else {
			internalLogon = null;
		}
	}

	/**
	 * Establece los parámetros de conexión con la base de datos Oracle.
	 * 
	 * @param tns  Cadena de conexión Oracle TNS.
	 * @param user Usuario de la base de datos.
	 * @param pass Contraseña del usuario.
	 * @param logonType El tipo de login contra la base de datos (AS SYSDBA, SYSASM, ...)
	 */
	public static void setConnectionParameters(String tns, String user, String pass, OracleLogonType logonType) {
		dbTns = tns;
		dbUser = user;
		dbPass = pass;
		internalLogon = logonType;
	}

	/**
	 * Comprueba si la conexión con la base de datos está viva o no. Realiza un ping
	 * a nivel de aplicación con un timeout de 1 segundo.
	 * 
	 * @return true si la base de datos responde correctamente, false de lo
	 *         contrario.
	 */
	public static boolean isConnectionAlive() {
		if (connection != null) {
			try {
				return connection.isValid(1);
			} catch (Exception e) {

			}
		}

		return false;
	}

	/**
	 * Obtiene el objeto de conexión con la base de datos. Comprueba si la conexión
	 * está viva, y en caso contrario intenta conectar con la misma.
	 * 
	 * @return El objeto Connection para acceso a la base de datos.
	 * @throws APIException Si falla la conexión a la base de datos.
	 */
	public static Connection get() throws HException {
		if (connection != null && DBConnection.isConnectionAlive()) {
			return connection;
		} else {
			if (dbTns == null || dbUser == null || dbPass == null) {
				throw new IllegalStateException(
						"No se puede conectar a la base de datos sin antes haber establecido los parametros de conexion");
			}
			DBConnection.clearResources(connection);
		}

		/*
		 * try { Class.forName(DB_DRIVER); } catch (ClassNotFoundException e) { throw
		 * new
		 * APIException("Imposible conectar con la base de datos. Driver no encontrado",
		 * e); }
		 */

		try {
			String dbConnectionString = "jdbc:oracle:thin:@" + dbTns;

			if (internalLogon == null) {
				connection = DriverManager.getConnection(dbConnectionString, dbUser, dbPass);
			} else {
				Properties props = new Properties();
				props.put("user", dbUser);
				props.put("password", dbPass);
				props.put("internal_logon", internalLogon.name());
				connection = DriverManager.getConnection(dbConnectionString, props);
			}

			return connection;
		} catch (SQLException e) {
			throw new OracleException(e);
		}
	}

	/**
	 * Cierra todos los elementos de la colección que se pase.
	 * 
	 * @param res Una colección de objetos que implemententen AutoCloseable.
	 */
	public static void clearResources(AutoCloseable... res) {
		for (AutoCloseable r : res) {
			if (r != null) {
				try {
					r.close();
				} catch (Exception e) {

				}
			}
		}
	}
}
