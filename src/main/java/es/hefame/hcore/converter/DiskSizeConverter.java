package es.hefame.hcore.converter;


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
public class DiskSizeConverter {

	/**
	 * Enumeración de los multiplicadores de tamaño de disco soportados.
	 * 
	 * @author Alejandro_AC
	 *
	 */
	public enum SizeFactor {
		BYTE(0, "B", "byte"), KILO(1, "K", "kilobyte"), MEGA(2, "M", "megabyte"), GIGA(3, "G", "gigabyte"),
		TERA(4, "T", "terabyte"), PETA(5, "P", "petabyte");

		public final int index;
		public final long factor;
		public final String abbreviation;
		public final String longName;

		private SizeFactor(int index, String abbreviation, String longName) {
			this.index = index;
			this.factor = (long) Math.pow(1024, index);
			this.abbreviation = abbreviation;
			this.longName = longName;
		}

		public static String formatAbbreviated(double value, SizeFactor factor) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%1$.2f", value));
			if (factor != null)	sb.append(factor.abbreviation);
			return sb.toString();
		}

		public static String format(double value, SizeFactor factor) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%1$.2f", value));
			sb.append(factor.longName);
			sb.append('s');
			return sb.toString();
		}

		public static SizeFactor fromIndex(int index) {
			for (SizeFactor factor : SizeFactor.values()) {
				if (factor.index == index)
					return factor;
			}
			return null;
		}

		public static SizeFactor fromAbbreviation(String abbr) {
			for (SizeFactor factor : SizeFactor.values()) {
				if (factor.abbreviation.equalsIgnoreCase(abbr))
					return factor;
			}
			return null;
		}

		public SizeFactor next() {
			return SizeFactor.fromIndex(index + 1);
		}

	}

	/**
	 * Convierte un número dado en bytes a un formato legible para los humanos.
	 * Algunos ejemplos
	 * <ul>
	 * <li>45 - 45B</li>
	 * <li>5482 - 5KB</li>
	 * <li>9986852 - 9MB</li>
	 * <li>7854854783 - 7GB</li>
	 * </ul>
	 * 
	 * @param bytes Un número de bytes de entrada.
	 * @return Una representación mas humana de los bytes de entrada.
	 */
	public static String bytesToHuman(double bytes) {
		int negativeFactor = bytes < 0 ? -1 : 1;

		SizeFactor currentFactor = SizeFactor.BYTE;
		double converted = Math.abs(bytes);

		while (converted >= 1024 && currentFactor != null && currentFactor.next() != null) {
			converted = converted / 1024;
			currentFactor = currentFactor.next();
		}

		return SizeFactor.formatAbbreviated(negativeFactor * converted, currentFactor);
	}

	/**
	 * Convierte una representación de bytes de la forma ##.##[unidad] a un
	 * equivalente expresado en bytes. Algunos ejemplos:
	 * <ul>
	 * <li>45B - 45</li>
	 * <li>5KB - 5120</li>
	 * <li>9MB - 9437184</li>
	 * <li>9GB - 9663676416</li>
	 * </ul>
	 * 
	 * @param formattedNumber Una cantidad de bytes representada de la forma
	 *                         ##.##[unidad]
	 * @return El número de bytes equivalente a la cantidad pasada como parámetro.
	 */
	public static long parseDiskSize(String formattedNumber) throws NumberFormatException {
		formattedNumber = formattedNumber.trim();

		StringBuilder sb = new StringBuilder();
		int j;
		for (j = formattedNumber.length() - 1; j >= 0; j--) {
			char c = formattedNumber.charAt(j);
			if (c >= '0' && c <= '9')
				break;
			else
				sb.append(c);
		}

		String unitAbbreviation = sb.reverse().toString();

		SizeFactor factor = SizeFactor.fromAbbreviation(unitAbbreviation);
		if (factor == null) {
			throw new NumberFormatException("Unknown unit abbreviation: '" + unitAbbreviation + "'");
		}
		String number = formattedNumber.substring(0, j + 1);
		number = number.replace(',', '.');

		double floatingPointNumber = Double.parseDouble(number);

		return (long) (floatingPointNumber * factor.factor);
	}

}
