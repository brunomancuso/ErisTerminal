package org.threewaves.eris.terminal;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

class Quote {
	public final String text;
	public final String author;

	public Quote(String text, String author) {
		this.text = text;
		this.author = author;
	}

	public static List<Quote> quotes() {
		try {
			String q = IOUtils.toString(Quote.class.getResourceAsStream("/quotes.json"));
			return new Gson().fromJson(q, new TypeToken<List<Quote>>() {
			}.getType());
		} catch (JsonSyntaxException | IOException e) {
		}
		return Collections.emptyList();
	}

	public static String random() {
		long ms = new Date().getTime() % 1000;
		List<Quote> qs = quotes();
		return qs.get((int) (ms % qs.size())).text;
	}

	public static void main(String[] args) {
		System.out.println(quotes().get(0));
	}
}
