package org.threewaves.eris.simulator;

import org.threewaves.eris.util.PrettyGson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface ISimulator {

	public static Gson gson() {
		GsonBuilder b = new GsonBuilder();
		b.setPrettyPrinting();
		return b.create();
	}

	public static PrettyGson prettyGson() {
		return PrettyGson.create();
	}

	public static <T> String marshall(T r) {
		return gson().toJson(r);
	}

	public static <T> T unmarshall(String value, Class<T> clazz) {
		if (value == null) {
			return null;
		}
		return gson().fromJson(value, clazz);
	}

	void put(String simulator, String key, Object response);

	void put(String simulator, String key, String response);

	String pop(String simulator, String key);

	<T> T pop(Class<T> class1, String simulator, String key);

	String get(String simulator, String key);

	void clear(String simulator);

}
