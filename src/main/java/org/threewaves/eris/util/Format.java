package org.threewaves.eris.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Format {

	public static Date parseDate(String date, String format) {
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			// no log
		}
		return null;
	}

	public static double parseDouble(String value, double _default) {
		if (value == null) {
			return _default;
		}
		try {
			return Double.parseDouble(value);
		} catch (RuntimeException e) {
			// no log
		}
		return _default;
	}

	public static long parseLong(String value, long _default) {
		if (value == null) {
			return _default;
		}
		try {
			return Long.parseLong(value);
		} catch (RuntimeException e) {
			// no log
		}
		return _default;
	}

	public static long parseLong(String value) {
		return parseLong(value, 0);
	}

	public static int parseInt(String value, int _default) {
		if (value == null) {
			return _default;
		}
		try {
			return Integer.parseInt(value);
		} catch (RuntimeException e) {
			// no log
		}
		return _default;
	}

	public static boolean parseBoolean(String s) {
		return Boolean.parseBoolean(s);
	}

	public static int parseInt(String value) {
		return parseInt(value, 0);
	}

	public static <T extends Enum<T>> T cast(Class<T> classType, Enum<?> en) {
		if (en == null) {
			return null;
		} else {
			return Enum.valueOf(classType, en.name());
		}
	}

	public static long round(double d) {
		return Math.round(d);
	}

	public static BigDecimal toDecimal(long value, int decimals) {
		BigDecimal d = new BigDecimal((double) value).divide(BigDecimal.valueOf(Math.pow(10, decimals)));
		d = d.setScale(decimals);
		return d;
	}

	public static BigDecimal toDecimal(Double value) {
		if (value == null) {
			return null;
		}
		return BigDecimal.valueOf(value);
	}

	public static BigDecimal toDecimalNullable(Double value) {
		if (value == null) {
			return null;
		}
		return BigDecimal.valueOf(value);
	}

	public static BigDecimal toPercentage(long value) {
		int digits = parseInt(System.getProperty("DefaultPercentageDigits", "2"));
		return toDecimal(value, digits);
	}

	public static Double toDouble(BigDecimal value) {
		if (value == null) {
			return null;
		}
		return value.doubleValue();
	}

	public static boolean toBoolean(Boolean value) {
		return Optional.ofNullable(value).orElse(false);
	}

	public static String toString(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	public static long toLong(double value, int decimals) {
		return Math.round((value * Math.pow(10, decimals)));
	}

	public static boolean isNull(Object o) {
		if (o == null) {
			return true;
		}
		return false;
	}
}