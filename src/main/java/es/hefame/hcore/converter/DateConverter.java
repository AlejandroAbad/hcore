package es.hefame.hcore.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase con métodos estáticos que ayudan a la conversión de los formatos de
 * datos.
 * <h2>Conversiones de Fechas</h2>
 * 
 * @author Alejandro_AC
 *
 */
public class DateConverter {

	/**
	 * Enumeración de los multiplicadores de tiempo soportados.
	 * 
	 * @author Alejandro_AC
	 *
	 */
	public enum TimeFactor {

		NANO(0, 1000, "nsec", "nanosegundo", "nanosegundos"), MICRO(1, 1000, "usec", "microsegundo", "microsegundos"),
		MILI(2, 1000, "msec", "milisegundo", "milisegundos"), SECOND(3, 60, "sec", "segundo", "segundos"),
		MINUTE(4, 60, "min", "minuto", "minutos"), HOUR(5, 24, "h", "hora", "horas"), DAY(6, 7, "d", "día", "días"),
		WEEK(7, 4, "w", "semana", "semanas"), MONTH(8, 12, "m", "mes", "meses"), YEAR(9, 1, "y", "año", "años");

		public final int index;
		public final int factor;
		public final String abbreviation;
		public final String unit;
		public final String unitPlural;

		private TimeFactor(int factorIndex, int factor, String abbreviation, String unit, String unitPlural) {
			this.index = factorIndex;
			this.factor = factor;
			this.abbreviation = abbreviation;
			this.unit = unit;
			this.unitPlural = unitPlural;
		}

		public static TimeFactor fromIndex(int index) {
			for (TimeFactor factor : TimeFactor.values()) {
				if (factor.index == index)
					return factor;
			}
			return null;
		}

		public TimeFactor next() {
			return TimeFactor.fromIndex(this.index + 1);
		}

	}

	/**
	 * Enumeración de los formatos de tiempo estandarizados
	 * 
	 * @author Alejandro_AC
	 *
	 */
	public enum TimeFormat {

		TINY("ddMMYY", "HHmmss", ""), SHORT("dd/MM/YY", "HH:mm:ss", " "), COMMON("dd/MM/YYYY", "HH:mm:ss", " "),
		LONG("dd/MM/YYYY", "HH:mm:ss", "' a las '");

		private SimpleDateFormat fullFormat;
		private SimpleDateFormat dateFormat;
		private SimpleDateFormat timeFormat;

		private TimeFormat(String dateFormat, String timeFormat, String dateTimeSeparator) {
			this.fullFormat = new SimpleDateFormat(dateFormat + dateTimeSeparator + timeFormat);
			this.dateFormat = new SimpleDateFormat(dateFormat);
			this.timeFormat = new SimpleDateFormat(timeFormat);
		}

		public String formatDateTime(Date date) {
			return fullFormat.format(date);
		}

		public String formatDate(Date date) {
			return dateFormat.format(date);
		}

		public String formatTime(Date date) {
			return timeFormat.format(date);
		}

	}

	/**
	 * Convierte una cantidad de tiempo expresada en nanosegundos en una cadena más
	 * legible al ser humano.
	 * 
	 * @see #timeToHuman(long, TimeFactor)
	 * @param nanos La cantidad de nanosegundos de entrada
	 * @return Una representación legible para el ser humano.
	 */
	public static String nanosecToHuman(long nanos) {
		return timeToHuman(nanos, TimeFactor.NANO);
	}

	/**
	 * Convierte una cantidad de tiempo expresada en microsegundos en una cadena más
	 * legible al ser humano.
	 * 
	 * @see #timeToHuman(long, TimeFactor)
	 * @param micros La cantidad de microsegundos de entrada
	 * @return Una representación legible para el ser humano.
	 */
	public static String microsecToHuman(long micros) {
		return timeToHuman(micros, TimeFactor.MICRO);
	}

	/**
	 * Convierte una cantidad de tiempo expresada en milisegundos en una cadena más
	 * legible al ser humano.
	 * 
	 * @see #timeToHuman(long, TimeFactor)
	 * @param milis La cantidad de milisegundos de entrada
	 * @return Una representación legible para el ser humano.
	 */
	public static String milisecToHuman(long milis) {
		return timeToHuman(milis, TimeFactor.MILI);
	}

	/**
	 * Convierte una cantidad de tiempo expresada en segundos en una cadena más
	 * legible al ser humano.
	 * 
	 * @see #timeToHuman(long, TimeFactor)
	 * @param secs La cantidad de segundos de entrada
	 * @return Una representación legible para el ser humano.
	 */
	public static String secToHuman(long secs) {
		return timeToHuman(secs, TimeFactor.SECOND);
	}

	/**
	 * Formatea un valor numérico a un formato legible por el ser humano.
	 * 
	 * <pre>
	 * 		Converter.time_to_human(4000, TimeFactor.NANO) -&gt; "4 microsegundos"
	 * 		Converter.time_to_human(4000000, TimeFactor.NANO) -&gt; "4 milisegundos"
	 * 		Converter.time_to_human(4000, TimeFactor.MICRO) -&gt; "4 milisegundos"
	 * 		Converter.time_to_human(8000, TimeFactor.SECONDS) -&gt; "2 horas"
	 * </pre>
	 * 
	 * 
	 * @param time        El valor a formatear.
	 * @param base_factor La unidad de tiempo en la que esta representado el valor.
	 *                    Si se indica un null, se asume el valor en segundos.
	 * @return El valor formateado.
	 */
	public static String timeToHuman(long time, TimeFactor baseFactor) {

		TimeFactor currentTimeFactor = baseFactor == null ? TimeFactor.SECOND : baseFactor;
		double floatingPointTime = (double) time;

		while (currentTimeFactor != null && currentTimeFactor.factor > 1
				&& floatingPointTime >= currentTimeFactor.factor) {
			floatingPointTime = floatingPointTime / currentTimeFactor.factor;
			currentTimeFactor = currentTimeFactor.next();
		}

		if (currentTimeFactor == null) {
			currentTimeFactor = TimeFactor.YEAR;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%1$.2f", floatingPointTime));
		sb.append(currentTimeFactor.abbreviation);
		return sb.toString();
	}

}
