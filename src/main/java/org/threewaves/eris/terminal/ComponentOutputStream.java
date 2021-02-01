package org.threewaves.eris.terminal;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.threewaves.eris.terminal.GenericAppender.Type;

class ComponentOutputStream extends OutputStream {
	private byte[] oneByte; 
	private GenericAppender appender; 
	private boolean enabled = true;
	private Type defaultType;
	
	public ComponentOutputStream(GenericAppender appender, Type defaultType) {
		this.oneByte = new byte[1];
		this.appender = appender;
		this.defaultType = defaultType;
	}

	public void clear() {
		if (appender != null) {
			appender.clear();
		}
	}

	public void close() {
		appender = null;
	}

	public void flush() {
	}

	public void write(int val) {
		if (enabled) {	
			oneByte[0] = (byte) val;
			write(oneByte, 0, 1);
		}
	}

	public void write(byte[] ba) {
		if (enabled) {
			write(ba, 0, ba.length);
		}
	}

	public void write(byte[] ba, int str, int len) {		
		if (enabled && appender != null) {
			appender.append(defaultType, bytesToString(ba, str, len));
		}
	}

	private static String bytesToString(byte[] ba, int str, int len) {
		try {
			return new String(ba, str, len, "UTF-8");
		} catch (UnsupportedEncodingException thr) {
			return new String(ba, str, len, Charset.defaultCharset());
		}
	}

	public void enable(boolean e) {
		this.enabled = e;
	}

}