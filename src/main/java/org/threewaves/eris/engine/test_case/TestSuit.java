package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestSuit {
	private final Path directory;

	public TestSuit(Path directory) {
		this.directory = directory;
	}

	public List<TestCase> list() {
		if (Files.exists(directory)) {
			try (Stream<Path> s = Files.list(directory)) {
				return s.filter((tc) -> TestCase.number(tc) > 0).map(p -> TestCase.of(p)).collect(Collectors.toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}

	public Path directory() {
		return directory;
	}

	public Optional<TestCase> findByFileName(String fileName) {
		if (Files.exists(directory)) {
			try (Stream<Path> s = Files.list(directory)) {
				return s.filter((tc) -> {
					return fileName.equals(tc.getFileName().toString());
				}).map(p -> TestCase.of(p)).findFirst();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Optional.empty();
	}

}
