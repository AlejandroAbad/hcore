package es.hefame.hcore;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import es.hefame.hcore.converter.ByteArrayConverter;

/**
 * Tipo de excepción personalizada para utilizar en las APIs. Es una Exception a
 * a que se le añade un código único de escepción que no es descriptivo para el
 * usuario final, pero permite al desarrollador identificad donde se lanzó la
 * misma.
 * 
 * Este código se genera obteniendo el nombre de la clase y la línea desde donde
 * se lanzó la excepción mas un contador incremental. El código se genera de la
 * siguiente forma:
 * <ul>
 * <li>nombre_clase = Nombre de la clase donde se instanció la excecpión,
 * codificado en hexadecimal.</li>
 * <li>linea = Número de línea donde se instanció la excepción.</li>
 * <li>secuencia = Un número secuencial, único en cada ejecución de la
 * aplicación.</li>
 * </ul>
 * 
 * <pre>
 * code = nombre_clase | '-' | linea | '-' | secuencia
 * </pre>
 * 
 * La clase es JSONEncodable, lo que permite imprimirla o mandarla como
 * respuesta HTTP directamente.
 * 
 * @author Alejandro_AC
 *
 */
public class HException extends Exception implements JsonEncodable {

	private static final long serialVersionUID = -3236410160241401158L;

	/**
	 * Contador de excepciones instanciadas (que no lanzadas).
	 */
	private static int instanceCount = 0;

	/**
	 * Código de la excepción.
	 */
	private String code;

	/**
	 * Elemento de la pila de ejecución donde se lanzó la excepción.
	 */
	private StackTraceElement stack;

	/**
	 * Incrementa en 1 el numero de veces que se ha creado una HException.
	 */
	private static void incrementInstanceCounter() {
		HException.instanceCount++;
	}

	/**
	 * Devuelve el número de veces que se ha instanciado una HException.
	 * 
	 * @return El número de veces que se ha instanciado una HException.
	 */
	public static int getInstanceCounter() {
		return HException.instanceCount;
	}

	/**
	 * Llama al constructor de la clase Exception.
	 * 
	 * @param message El mensaje de la excepción.
	 * @param cause   La causa de esta excepción si existe.
	 */
	public HException(String message, Throwable cause) {
		super(message, cause);

		HException.incrementInstanceCounter();

		this.stack = this.calculateCaller();
		this.code = this.calculateCode();
	}

	/**
	 * Llama al constructor de la clase Exception.
	 * 
	 * @param message El mensaje de la excepción.
	 */
	public HException(String message) {
		this(message, null);
	}

	/**
	 * Localiza en la pila de llamadas el momento en el que se lanzó esta excepción.
	 * 
	 */
	private StackTraceElement calculateCaller() {
		int i = 0;
		boolean findedMyself = false;
		boolean isException = false;
		StackTraceElement stackElement = null;
		try {
			StackTraceElement[] fullStackTrace = Thread.currentThread().getStackTrace();
			do {
				stackElement = fullStackTrace[i++];
				isException = HException.class.isAssignableFrom(Class.forName(stackElement.getClassName()));
				findedMyself = findedMyself || isException;
			} while (isException || !findedMyself);

			return stackElement;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Genera el código de la excepción. Debe llamarse despues de calculateCaller()
	 * o el código devuelto será CALL-NOT-IN-STACK-#
	 */
	private String calculateCode() {
		if (this.stack == null) {
			return "CALL-NOT-IN-STACK-" + (HException.getInstanceCounter());
		}

		StringBuilder sb = new StringBuilder();

		// Class name
		String raisinFile = this.stack.getFileName();
		raisinFile = raisinFile.substring(0, raisinFile.length() - 5);
		sb.append(ByteArrayConverter.toHexString(raisinFile.getBytes(), false)).append('-')
				.append(this.stack.getLineNumber()).append('-').append(HException.getInstanceCounter());

		return sb.toString();
	}

	/**
	 * Devuelve el código de la HException.
	 * 
	 * @return El código de la HException.
	 */
	public String getCode() {
		return this.code;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONAware json_encode() {
		JSONObject root = new JSONObject();
		root.put("code", this.getCode());
		root.put("message", this.getMessage());
		return root;
	}

	@Override
	public String toString() {
		return this.json_encode().toJSONString();
	}

}
