package org.threewaves.eris.engine.footprint;

import java.util.ArrayList;
import java.util.List;

public class NormalDiff {
	public static class Position {
		public final int start;
		public final int end;

		private Position(int start, int end) {
			this.start = start;
			this.end = end;
		}

		private Position(int offset, List<String> lines, boolean left, int increase) {
			String line = lines.get(offset);
			int start = 0;
			int end = 0;
			if (!line.startsWith("\\")) {
				String[] tmp = line.split("a|d|c");
				if (tmp != null && tmp.length > 1) {
					if (tmp[left ? 0 : 1].indexOf(',') >= 0) {
						String[] tmp1 = tmp[left ? 0 : 1].split("\\,");
						if (tmp1.length > 1) {
							start = Integer.parseInt(tmp1[0].trim());
							end = Integer.parseInt(tmp1[1].trim());
						}
					} else {
						start = end = Integer.parseInt(tmp[left ? 0 : 1].trim());
					}
				}
			}
			this.start = start + increase;
			this.end = end + increase;
		}

		@Override
		public String toString() {
			if (start != end) {
				return ("[" + start + ":" + end + "]");
			} else {
				return ("" + start);
			}
		}
	}

	public static class Data {
		public final char direction;
		public final List<String> data;

		private Data(int offset, List<String> lines) {
			if (offset < lines.size() && Diff.isDirection(lines.get(offset))) {
				direction = lines.get(offset).charAt(0);
				data = new ArrayList<String>();
				while (offset < lines.size() && lines.get(offset).charAt(0) == direction) {
					data.add(lines.get(offset).substring(1).replaceAll("\r", ""));
					offset++;
				}
			} else {
				direction = 0;
				data = null;
			}
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			if (data != null) {
				for (String s : data) {
					buffer.append(direction + s + "\r\n");
				}
			}
			return buffer.toString();
		}

		public String toSimpleString() {
			StringBuffer buffer = new StringBuffer();
			if (data != null) {
				for (String s : data) {
					buffer.append(s + "\r\n");
				}
			}
			return buffer.toString();
		}
	}

	public final Position leftPosition;
	public final char operation;
	public final Position rightPosition;
	public final Data data;
	public final Data addicional;

	private NormalDiff(Position leftPosition, char operation, Position rightPostion, Data data, Data addicional) {
		this.leftPosition = leftPosition;
		this.operation = operation;
		this.rightPosition = rightPostion;
		this.data = data;
		this.addicional = addicional;
	}

	public NormalDiff copy(boolean leftIncrement, int inc) {
		Position leftPosition = this.leftPosition;
		Position rightPosition = this.rightPosition;
		if (leftIncrement) {
			leftPosition = new Position(this.leftPosition.start + inc, this.leftPosition.end + inc);
		} else {
			rightPosition = new Position(this.rightPosition.start + inc, this.rightPosition.end + inc);
		}
		return new NormalDiff(leftPosition, this.operation, rightPosition, this.data, this.addicional);
	}

	public static List<NormalDiff> create(List<String> lines) {
		return create(true, lines);
	}

	public static List<NormalDiff> create(boolean decrementPosition, List<String> lines) {
		try {
			int offset = 0;
			List<NormalDiff> list = new ArrayList<NormalDiff>();
			while (offset < lines.size()) {
				char operation = getOperation(offset, lines);
				if (operation != 0) {
					Position leftPosition = new Position(offset, lines, true, decrementPosition ? -1 : 0);
					Position rightPosition = new Position(offset, lines, false, decrementPosition ? -1 : 0);
					offset++;
					if (offset < lines.size()) {
						Data left = new Data(offset, lines);
						Data right = null;
						// >
						offset = getNext(left.direction, offset, lines);
						if (operation == 'c') {
							if (lines.get(offset).startsWith("\\ No newline at end of file")) {
								offset++;
							}
							// ---
							offset++;
							right = new Data(offset, lines);
							// <
							offset = getNext(right.direction, offset, lines);
						}
						list.add(new NormalDiff(leftPosition, operation, rightPosition, left, right));
					}
				} else {
					break;
				}
			}
			return list;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toString(List<NormalDiff> diffs) {
		StringBuffer buffer = new StringBuffer();
		for (NormalDiff diffData : diffs) {
			buffer.append(diffData.toString());
		}
		return buffer.toString();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(leftPosition.toString());
		buffer.append(operation);
		buffer.append(rightPosition.toString());
		buffer.append("\r\n");
		buffer.append(data.toString());
		if (operation == 'c') {
			buffer.append("---\r\n");
			buffer.append(addicional.toString());
		}
		return buffer.toString();
	}

	public String toShortNiceString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("At line " + rightPosition.toString() + " has ");
		if (operation == 'c')
			buffer.append("changed");
		else if (operation == 'a')
			buffer.append("been deleted");
		else
			buffer.append("been added");
		buffer.append("(" + rightPosition.toString() + ")");
		return buffer.toString();
	}

	private static int getNext(char direction, int offset, List<String> lines) {
		while (offset < lines.size() && lines.get(offset).charAt(0) == direction) {
			offset++;
		}
		return offset;
	}

	private static char getOperation(int offset, List<String> lines) {
		if (lines.get(offset).indexOf('a') >= 0) {
			return 'a';
		}
		if (lines.get(offset).indexOf('d') >= 0) {
			return 'd';
		}
		if (lines.get(offset).indexOf('c') >= 0) {
			return 'c';
		}
		return 0;
	}

	public static int search(List<NormalDiff> diffs, int line, boolean left) {
		for (int i = 0; i < diffs.size(); i++) {
			if (left && line >= diffs.get(i).leftPosition.start && line <= diffs.get(i).leftPosition.end) {
				return i;
			} else if (!left && line >= diffs.get(i).rightPosition.start && line <= diffs.get(i).rightPosition.end) {
				return i;
			}
		}
		return -1;
	}

	public static int searchClosest(List<NormalDiff> diffs, int line, boolean left, boolean forward) {
		for (int i = 0; i < diffs.size(); i++) {
			if (forward) {
				if (left && line <= diffs.get(i).leftPosition.end) {
					return i;
				} else if (!left && line <= diffs.get(i).rightPosition.end) {
					return i;
				}
			} else {
				if (left && line >= diffs.get(i).leftPosition.start) {
					return i;
				} else if (!left && line >= diffs.get(i).rightPosition.start) {
					return i;
				}
			}
		}
		return -1;
	}

	public static List<NormalDiff> searchByRightText(List<NormalDiff> diffs, NormalDiff diff) {
		List<NormalDiff> list = new ArrayList<NormalDiff>();
		for (NormalDiff normalDiff : diffs) {
			if (diff.operation == 'c') {
				if (diff.addicional.toString().equals(normalDiff.addicional.toString())) {
					list.add(normalDiff);
				}
			} else if (diff.operation == 'a') {
				if (diff.data.direction == '>') {
					if (diff.addicional.toString().equals(normalDiff.addicional.toString())) {
						list.add(normalDiff);
					}
				}
			} else if (diff.operation == 'd') {
				if (diff.data.direction == '<') {
					if (diff.addicional.toString().equals(normalDiff.addicional.toString())) {
						list.add(normalDiff);
					}
				}
			}
		}
		return list;
	}
}