package es.hefame.hcore.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import es.hefame.hcore.HException;

/**
 * Clase con métodos estáticos que ayudan a la conversión de los formatos de
 * datos.
 * <h2>Byte array conversion</h2> Operaciones y conversiones con cadenas de
 * bytes inputStreamToByteArray(InputStream) : byte[] toHexString(byte[]) :
 * String toHexString(byte[], boolean) : String fromHexString(String) : byte[]
 * 
 * 
 * @author Alejandro_AC
 *
 */
public class ByteArrayConverter {

	private ByteArrayConverter() {

	}



	/**
	 * Lee los contenidos de un InputStream en un array de bytes.
	 * 
	 * @param stream     Un stream del que leer.
	 * @param bufferSize el tamaño del buffer de lectura. Si se ajusta al total de bytes a leer del stream, mas eficiente.
	 * @return Un array de bytes con los datos leidos del stream.
	 * @throws IOException Si ocurre algún error al leer del stream.
	 */
	public static byte[] inputStreamToBytearray(InputStream stream, int bufferSize) throws IOException {
		if (stream == null) {
			return new byte[0];
		}

		if (bufferSize <= 0) {
			bufferSize = 1024;
		}

		byte[] buffer = new byte[bufferSize];
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		int line = 0;
		while ((line = stream.read(buffer)) != -1) {
			os.write(buffer, 0, line);
		}

		stream.close();
		os.flush();
		os.close();
		return os.toByteArray();
	}

	/**
	 * Lee los contenidos de un InputStream en un array de bytes. Utiliza un tamaño
	 * de buffer de 4Kb.
	 * 
	 * @param stream Un stream del que leer.
	 * @return Un array de bytes con los datos leidos del stream.
	 * @throws IOException Si ocurre algún error al leer del stream.
	 */
	public static byte[] inputStreamToBytearray(InputStream stream) throws IOException {
		return ByteArrayConverter.inputStreamToBytearray(stream, 4096);
	}

	/**
	 * Convierte un array de bytes a una cadena en formato hexadecimal, precedido de
	 * la cadena '0x'.
	 * 
	 * @param bytes El array de bytes de entrada.
	 * @return La representación hexadecimal del array de bytes.
	 */
	public static String toHexString(byte[] bytes) {
		return toHexString(bytes, true);
	}

	/**
	 * Convierte un array de bytes a una cadena en formato hexadecimal.
	 * 
	 * @param bytes     El array de bytes de entrada.
	 * @param hexPrefix Indica si añadir el prefijo '0x' al inicio de la cadena
	 *                  devuelta.
	 * @return La representación hexadecimal del array de bytes.
	 */
	public static String toHexString(byte[] bytes, boolean hexPrefix) {

		if (bytes == null) {
			return "";
		}

		String hexadecimal = DatatypeConverter.printHexBinary(bytes);

		if (hexPrefix && hexadecimal.length() > 0) {
			return "0x" + hexadecimal;
		}
		return hexadecimal;

	}

	/**
	 * Convierte una cadena de carácteres hexadecimales en un array de bytes
	 * equivalente. Si la cadena comienza por '0x', este sufijo se elimina
	 * automáticamente. La entrada es insensible a mayúsculas y minúsculas.
	 * 
	 * @param s La cadena de entrada en formato hexadecimal.
	 * @return El array de bytes representado por la cadena hexadecimal entrante.
	 * @throws APIException Si la cadena contiene carácteres no válidos para la
	 *                      representación hexadecimal (i.e: 0123456789abcdef)
	 */
	public static byte[] fromHexString(String s) throws HException {

		if (s == null) {
			return new byte[0];
		}

		if (s.startsWith("0x")) {
			s = s.substring(2, s.length());
		}

		try {
			return DatatypeConverter.parseHexBinary(s);
		} catch (IllegalArgumentException iae) {
			throw new HException("Representación hexadecimal inválida", iae);
		}

	}

	/**
	 * Decodifica datos codificados con MIME base64 en una cadena en un array de
	 * bytes.
	 * 
	 * @param base64 La cadena a descodificar.
	 * @return Un array de bytes con los datos originales.
	 */
	public static byte[] fromBase64(String base64) {
		int neededEq = base64.length() % 4;
		while (neededEq > 0) {
			base64 += '=';
			neededEq--;
		}

		return DatatypeConverter.parseBase64Binary(base64);
	}

	/**
	 * Convierte un array de bytes a una cadena en formato MIME base64.
	 * 
	 * @param raw El array de bytes a convertir.
	 * @return La cadena MIME base64 resultante.
	 */
	public static String toBase64(byte[] raw) {
		return DatatypeConverter.printBase64Binary(raw);
	}

}
