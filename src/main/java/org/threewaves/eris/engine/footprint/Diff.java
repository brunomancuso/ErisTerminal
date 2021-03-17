package org.threewaves.eris.engine.footprint;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.threewaves.eris.util.ShellExec;

/**
 * Wrapper for the diff linux command
 * @author Bruno Mancuso
 *
 */
public class Diff {
	private final Path diff = Paths.get("bin", "diff.exe");
	private final static Path OUT = Paths.get("out");

	public static boolean isDirection(String line) {
		return line.charAt(0) == '>' || line.charAt(0) == '<';
	}

	public static boolean isPosition(String line) {
		return line.charAt(0) != '>' && line.charAt(0) != '<' && line.charAt(0) != '-';
	}

	public List<NormalDiff> ndiff(Path expected, Path actual) throws IOException {
		String lines = ndiff1(expected, actual);
		if (lines != null) {
			return NormalDiff.create(Arrays.asList(lines.split("\n")));
		}
		return new ArrayList<NormalDiff>();
	}

	private String ndiff1(Path expected, Path actual) throws IOException {
		if (Files.exists(expected) && Files.exists(actual)) {
			ShellExec sh = new ShellExec(false, true, false);
			sh.execute(diff.toString(), ".", true, "-a", expected.toString(), actual.toString());
			return sh.getOutput();
		}
		return null;
	}

	public static List<NormalDiff> diff(String left, String right) throws IOException {
		if (!Files.exists(OUT)) {
			Files.createDirectories(OUT);
		}
		Files.deleteIfExists(Paths.get(OUT.toString(), "left.tmp"));
		Files.deleteIfExists(Paths.get(OUT.toString(), "right.tmp"));
		Files.write(Paths.get(OUT.toString(), "left.tmp"), left.getBytes(Charset.defaultCharset()));
		Files.write(Paths.get("OUT.toString(), right.tmp"), right.getBytes(Charset.defaultCharset()));
		List<NormalDiff> diff = new Diff().ndiff(Paths.get(OUT.toString(), "left.tmp"),
				Paths.get(OUT.toString(), "right.tmp"));
		return diff;
	}

	public static List<NormalDiff> diff(List<String> left, List<String> right) throws IOException {
		return diff(left.stream().collect(Collectors.joining("\r\n")),
				right.stream().collect(Collectors.joining("\r\n")));
	}
}