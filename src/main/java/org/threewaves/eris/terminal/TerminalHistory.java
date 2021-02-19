package org.threewaves.eris.terminal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class TerminalHistory {
	private static final Path FILE = Paths.get(".history");
	private final List<String> prompts = new ArrayList<>();
	private int index = 0;
	private int posX;
	private int posY;
	private int width;
	private int height;

	public static TerminalHistory load() {
		TerminalHistory t = new TerminalHistory();
		if (Files.exists(FILE)) {
			String content = null;
			try {
				content = new String(Files.readAllBytes(FILE), Charset.defaultCharset());
				t = gson().fromJson(content, TerminalHistory.class);
				t.index = t.prompts.size();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		t.save();
		return t;
	}

	public void save() {
		try {
			Files.write(FILE, gson().toJson(this).getBytes(Charset.defaultCharset()));
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static Gson gson() {
		GsonBuilder b = new GsonBuilder();
		b.setPrettyPrinting();
		return b.create();
	}

	public int getIndex() {
		return index;
	}

	public void maxIndex() {
		index = prompts.size();
	}

	public void resetIndex() {
		index = 0;
	}

	public void decreaseIndex() {
		index--;
	}

	public String getPrompt() {
		return prompts.get(index);
	}

	public void moved(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void resize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void increaseIndex() {
		index++;
	}

	public void addPrompt(String line) {
		if (prompts.size() == 0 || !prompts.get(prompts.size() - 1).equals(line)) {
			prompts.add(line);
			index = prompts.size();
		}

	}

	public int sizePrompt() {
		return prompts.size();
	}

	public static Path getFile() {
		return FILE;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
