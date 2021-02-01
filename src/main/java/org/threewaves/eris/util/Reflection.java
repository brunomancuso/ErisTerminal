package org.threewaves.eris.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reflection {
	public static <T> T newInstance(String className, Class<T> parent) {
		T instance = null;
		if (className == null || className.length() == 0) {
			return null;
		}
		try {
			Class<? extends T> classInputClass = Class.forName(className).asSubclass(parent);
			Class<?>[] params = new Class[0];
			Object[] args = new Object[0];
			Constructor<? extends T> ctor = classInputClass.getConstructor(params);
			instance = ctor.newInstance(args);
		} catch (RuntimeException | ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public static List<Field> fieldsRecursive(Class<?> startClass, Class<?> exclusiveParent) {
		List<Field> currentClassFields = new ArrayList<Field>();
		currentClassFields.addAll(Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Field> parentClassFields = fieldsRecursive(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	public static boolean hasAnnotation(Class<?> annotation, Field field) {
		return field != null && getAnnotation(annotation, field) != null;
	}

	public static Field getFieldWithAnnotation(Class<?> annotation, Class<?> clazz) {
		for (Field f : clazz.getFields()) {
			if (getAnnotation(annotation, f) != null) {
				return f;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAnnotation(Class<T> annotation, Field field) {
		Annotation[] as = field.getDeclaredAnnotations();
		if (as != null) {
			for (Annotation a : as) {
				if (a.getClass().equals(annotation)) {
					return (T) a;
				}
			}
		}
		return null;
	}

	public static List<String> valuesAsString(Class<? extends Enum<?>> class1) {
		try {
			Method m = class1.getDeclaredMethod("values");
			Object[] s = (Object[]) m.invoke(null);
			if (s != null) {
				List<String> values = new ArrayList<>();
				for (Object o : s) {
					values.add(o.toString());
				}
				return values;
			}
		} catch (RuntimeException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

}
