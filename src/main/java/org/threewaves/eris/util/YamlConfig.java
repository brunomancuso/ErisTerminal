package org.threewaves.eris.util;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;


public class YamlConfig {	
	private static Yaml yaml() {
		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		Yaml yaml = new Yaml(representer);
		yaml.setBeanAccess(BeanAccess.FIELD);
		return yaml;
	}

	private static <T> T load(String content, Class<T> clazz) {
		return yaml().loadAs(content, clazz);
	}

	public static <T> T create(byte[] bytes, T defaultValue) {
		return create(new String(bytes, Charset.defaultCharset()), defaultValue);
	}
	@SuppressWarnings("unchecked")
	public static <T> T create(String content, T defaultValue) {
		try {
			Class<T> clazz = (Class<T>) defaultValue.getClass();
			T newInstance = load(content, clazz);
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(newInstance);
				if (value != null) {
					field.set(defaultValue, value);
				}
				field.setAccessible(false);
			}
			return defaultValue;
		} catch (RuntimeException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}	
}
