package org.threewaves.eris.util;

public class Pad {

	public static String left(String value, int len) {
		return left(value, len, ' ');
	}

	public static String left(int value, int len) {
		return left(value, len, '0');
	}

	public static String left(Integer value, int len, char pad) {
		return left(value.toString(), len, pad);
	}

	public static String left(Long value, int len, char pad) {
		return left(value.toString(), len, pad);
	}

	public static String left(String value, int len, char pad) {
		if (value.length() > len) {
			value = value.substring(0, len);
		}
		while (value.length() < len) {
			value = pad + value;
		}
		return value;
	}

	public static String right(Integer value, int len, char pad) {
		return right(value.toString(), len, pad);
	}

	public static String right(Long value, int len, char pad) {
		return right(value.toString(), len, pad);
	}

	public static String right(String value, int len) {
		return right(value, len, ' ');
	}

	public static String right(String value, int len, char pad) {
		if (value.length() > len) {
			value = value.substring(0, len);
		}
		StringBuilder v = new StringBuilder(value);
		while (v.length() < len) {
			v.append(pad);
		}
		return v.toString();
	}

	public static String border(String left, String right, int width) {
		if (width - right.length() >= 0) {
			return right(left, width - right.length(), ' ') + right;
		}
		return left + right;
	}

	public static String center(String value, int width) {
		return left(value, (int) Math.round(width / 2.0 + value.length() / 2.0), ' ');
	}

	public static String trim(String value, int len) {
		value = value.trim();
		if (value.length() > len) {
			value = value.substring(0, len);
		}
		return value;
	}

	public static String fill(int len, char pad) {
		return left("", len, pad);
	}

	public static String trimLeft(String s, char c) {
		if (s == null) {
			return s;
		}
		while (s.length() > 0) {
			if (s.charAt(0) == c) {
				s = s.substring(1);
			} else {
				break;
			}
		}
		return s;
	}
}
