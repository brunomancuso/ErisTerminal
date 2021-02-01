package org.threewaves.eris.terminal;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.threewaves.eris.util.Pair;

class GenericAppender implements Runnable {
	public enum Type {
		NORMAL, ERROR, NOTIFICATION;
	}

	private static final String EOL1 = "\n";
	private static final String EOL2 = System.getProperty("line.separator", EOL1);

	public interface IAppender {
		void append(Type type, String val);
		void remove(int start, int end);
		int size();	
	}
	private final IAppender container;
	private final int maxLines; // maximum lines allowed in text area
	private final LinkedList<Integer> lengths; // length of lines within text area
	private final List<Pair<Type, String>> values; // values waiting to be appended

	private int curLength; 
	private boolean clear;
	private boolean queue;

	public GenericAppender(IAppender container, int maxLines) {
		if (maxLines < 1) {
			throw new IllegalArgumentException("Maximum lines must be positive (value=" + maxLines + ")");
		}
		this.container = container;
		this.maxLines = maxLines;
		lengths = new LinkedList<>();
		values = new ArrayList<>();

		curLength = 0;
		clear = false;
		queue = true;
	}

	synchronized void append(Type type, String val) {
		values.add(new Pair<>(type, val));
		if (queue) {
			queue = false;
			EventQueue.invokeLater(this);
		}
	}

	synchronized void clear() {
		clear = true;
		curLength = 0;
		lengths.clear();
		values.clear();
		if (queue) {
			queue = false;
			EventQueue.invokeLater(this);
		}
	}

	// MUST BE THE ONLY METHOD THAT TOUCHES textArea!
	public synchronized void run() {
		if (clear) {
			container.remove(0, container.size() - 1);
		}
		for (Pair<Type,String> val : values) {
			curLength += val.second.length();
			if (val.second.endsWith(EOL1) || val.second.endsWith(EOL2)) {
				if (lengths.size() >= maxLines) {
					container.remove(0, lengths.removeFirst());
				}
				lengths.addLast(curLength);
				curLength = 0;
			}
			container.append(val.first, val.second);
		}
		values.clear();
		clear = false;
		queue = true;
	}
}	
