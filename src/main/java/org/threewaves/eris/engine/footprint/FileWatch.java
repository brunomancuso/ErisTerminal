package org.threewaves.eris.engine.footprint;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileWatch {
	private final Path path;
	private transient int mark;
	private final String charset;

	public FileWatch(Path path, String charset) {
		this.path = path;
		this.mark = 0;
		this.charset = charset;
	}

	public FileWatch(Path path) {
		this.path = path;
		this.mark = 0;
		this.charset = Charset.defaultCharset().name();
	}

	public void mark() {
		if (path != null && Files.exists(path)) {
			mark = (int) path.toFile().length();
		} else {
			mark = 0;
		}
	}

	public List<String> readContent() throws IOException {
		return readContent(path, charset, mark);
	}

	public static List<String> readContent(Path path, int from) throws IOException {
		return readContent(path, Charset.defaultCharset().name(), from);
	}

	private static List<String> readContent(Path file, String charset, int from) throws IOException {
		StringBuilder content = new StringBuilder();
		if (file == null || !Files.exists(file)) {
			return Collections.emptyList();
		}
		long length = Files.size(file);
		if (length > 0 && length > from) {
			try (FileInputStream reader = new FileInputStream(file.toFile())) {
				int len = (int) length - from;
				long skipped = reader.skip(from);
				if (skipped != from) {
					System.err.println("Warning skipped to few: " + skipped + " != " + from);
				}
				int size = 0;
				byte[] chars = new byte[1024];
				while (size < len) {
					int l = reader.read(chars);
					if (l == -1) {
						break;
					}
					content.append(new String(chars, 0, l, charset));
					size += l;
				}
				reader.close();
			}
		} else {
			return new ArrayList<String>();
		}
		return Arrays.asList(content.toString().replaceAll("\r\n", "\n").split("\n"));
	}
}
