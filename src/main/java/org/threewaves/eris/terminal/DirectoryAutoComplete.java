package org.threewaves.eris.terminal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DirectoryAutoComplete implements IAutoComplete {

	@Override
	public List<List<String>> complete(String text) {
		try {
			String last = "";
			String pre = "";
			if (text.lastIndexOf(' ') >= 0) {
				text = text.substring(text.lastIndexOf(' ') + 1);
			}
			if (text.contains("/")) {
				if (text.lastIndexOf('/') + 1 < text.length()) {
					last = text.substring(text.lastIndexOf('/') + 1);
				}
				pre = text.substring(0, text.lastIndexOf('/'));
			} else {
				return Collections.emptyList();
			}
			Path p = Paths.get(pre.replaceAll("\\\\_", " "));
			final String filter = last.replaceAll("\\\\_", " ");
			try (Stream<Path> s = Files.list(p)) {
				List<Path> ps = s.map((f) -> f.getFileName()).filter((f) -> f.toString().startsWith(filter))
						.collect(Collectors.toList());
				List<String> fs = new ArrayList<>();
				List<String> ds = new ArrayList<>();

				for (Path a : ps) {
					Path c = Paths.get(p.toString(), a.toString());
					Path f = a.getFileName();
					if (Files.isDirectory(c)) {
						ds.add(c.toString().replace('\\', '/').replaceAll(" ", "\\\\_"));
					} else if (f != null && !f.toString().startsWith(".")) {
						fs.add(c.toString().replace('\\', '/').replaceAll(" ", "\\\\_"));
					}
				}
				List<List<String>> ls = new ArrayList<>();
				ls.add(ds);
				ls.add(fs);
				return ls;
			}
		} catch (RuntimeException | IOException e) {
			System.err.println(e);
		}
		return Collections.emptyList();
	}

}
