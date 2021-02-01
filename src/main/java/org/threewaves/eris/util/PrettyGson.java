package org.threewaves.eris.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PrettyGson {
	// Semi JSON, sin la comillas, para que sea mas legible en eris
	private final Gson gson;

	private PrettyGson(Gson gson) {
		this.gson = gson;
	}

	public String toJson(Object a) {
		String p = gson.toJson(a);
		p = p.replaceAll("\"__START__", "'");
		p = p.replaceAll("__END__\"", "'");
		p = p.replaceAll("\"", "");
		p = p.replaceAll("__QUOTE__", "\"");
		return p;
	}

	public static String toString(String p) {
		p = p.replaceAll("\"", "'");
		return p;
	}

	public static PrettyGson create() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(String.class, new JsonSerializer<String>() {
			@Override
			public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
				String a = s.replaceAll("\"", "__QUOTE__");
				return new JsonPrimitive("__START__" + a + "__END__");
			}

		});
		builder.setPrettyPrinting();
		return new PrettyGson(builder.create());
	}
}