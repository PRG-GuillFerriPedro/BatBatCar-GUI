package es.batbatcar.v2p4.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {
	private static final String RUTA_REGEXP = "[A-Za-z]*-[A-Za-z]+";
	private static final String NOMBREAPELLIDOS_REGEXP = "[A-Za-z]+\\s[A-Za-z]*\\s*[A-Za-z]*";

	private static boolean isNotEmptyOrNull(String param) {
		return param != null && !param.isEmpty();
	}

	public static boolean isValidRuta(String param) {
		return isNotEmptyOrNull(param) && param.matches(RUTA_REGEXP);
	}

	public static boolean isValidPlazasOfertadas(int param) {
		return param > 0 && param <= 6;
	}

	public static boolean isValidPrecio(float param) {
		return param > 0;
	}

	public static boolean isValidDur(int param) {
		return param > 0;
	}

	public static boolean isValidPropietario(String param) {
		return isNotEmptyOrNull(param) && param.matches(NOMBREAPELLIDOS_REGEXP);
	}

	public static boolean isValidDateTime(String dateTime) {
		try {
			LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidDate(String date) {
		try {
			LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidTime(String time) {
		try {
			LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
		} catch (DateTimeParseException e) {
			return false;
		}
		return true;
	}

}
