package org.threewaves.eris.engine.footprint;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.ClassUtils;
import org.threewaves.eris.util.PrettyGson;

public class Footprint {
	public static final String NEWLINE = "\r\n";
	private Path path;

	public Footprint(String name) {
		this.path = Paths.get("logs", name + ".footprint");
	}

	public <T> T write(T object) {		
		return write("", object);
	}

	public <T> T writeNullable(String header, T object) {
		if (object == null) {
			write(header, "null");
			return null;
		}
		return write(header, object);
	}
	
	public synchronized <T> T write(String header, T object) {
		try {
			String w = "";
			if (object == null) {
			} else if (String.class.isInstance(object) || ClassUtils.isPrimitiveOrWrapper(object.getClass())) {
				w = header + object.toString() + NEWLINE;								
			} else {
				w = header + PrettyGson.create().toJson(object) + NEWLINE;		
			}
			if (!w.isEmpty()) {
				if (!Files.exists(path)) {
					Files.write(path, w.getBytes(Charset.defaultCharset()));					
				} else {
					Files.write(path, w.getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}	
}
