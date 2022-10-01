package io.github.anvilloystudio.minimods.api;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class ModConfig extends JSONObject {
	private static final HashMap<String, ModConfig> configs = new HashMap<>();

	@Nullable
	public static ModConfig getConfig(String modId) {
		return configs.get(modId);
	}

	public static ModConfig getConfigOrCreate(String modId) {
		ModConfig cfg = configs.get(modId);
		if (cfg == null) cfg = configs.put(modId, new ModConfig());
		return cfg;
	}

	public static ModConfig getConfigFromLoader(String modId) {
		try {
			return (ModConfig) ModLoaderCommunication.getMethod(ModConfig.class.getSimpleName(), "getConfig", new Class<?>[] {String.class})
				.invoke(null, modId);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
				| ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	@NotNull
	public static ModConfig getConfigOrCreateFromLoader(String modId) {
		try {
			return (ModConfig) ModLoaderCommunication.getMethod(ModConfig.class.getSimpleName(), "getConfigOrCreate", new Class<?>[] {String.class})
				.invoke(null, modId);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
				| ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	private HashMap<String, Vector2<Number>> numberValueLimits = new HashMap<>();

	public void setLimit(String key, Vector2<Number> limits) {
		numberValueLimits.put(key, limits);
	}
	@SuppressWarnings("unchecked")
	public <T extends Number> Vector2<T> getLimit(String key) {
		try {
			return (Vector2<T>) numberValueLimits.get(key);
		} catch (ClassCastException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends Number> T getUpperLimit(String key) {
		Vector2<Number> val = numberValueLimits.get(key);
		try {
			return val == null ? null : (T) val.y;
		} catch (ClassCastException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends Number> T getLowerLimit(String key) {
		Vector2<Number> val = numberValueLimits.get(key);
		try {
			return val == null ? null : (T) val.x;
		} catch (ClassCastException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public <T extends Number> Vector2<Number> setLowerLimit(String key, T lowerLimit) {
		Vector2<Number> val = numberValueLimits.get(key);
		if (val == null) {
			Number maxVal = 0;
			if (lowerLimit instanceof Integer) maxVal = Integer.MAX_VALUE;
			else if (lowerLimit instanceof Byte) maxVal = Byte.MAX_VALUE;
			else if (lowerLimit instanceof Short) maxVal = Short.MAX_VALUE;
			else if (lowerLimit instanceof Long) maxVal = Long.MAX_VALUE;
			else if (lowerLimit instanceof Float) maxVal = Float.MAX_VALUE;
			else if (lowerLimit instanceof Double) maxVal = Double.MAX_VALUE;
			else throw new UnsupportedOperationException("Unsupported number type: " + lowerLimit.getClass().getSimpleName());
			val = numberValueLimits.put(key, new Vector2<Number>(lowerLimit, maxVal));
		} else {
			val.x = lowerLimit;
		}

		return val;
	}
	public <T extends Number> Vector2<Number> setUpperLimit(String key, T upperLimit) {
		Vector2<Number> val = numberValueLimits.get(key);
		if (val == null) {
			Number minVal = 0;
			if (upperLimit instanceof Integer) minVal = Integer.MIN_VALUE;
			else if (upperLimit instanceof Byte) minVal = Byte.MIN_VALUE;
			else if (upperLimit instanceof Short) minVal = Short.MIN_VALUE;
			else if (upperLimit instanceof Long) minVal = Long.MIN_VALUE;
			else if (upperLimit instanceof Float) minVal = Float.MIN_VALUE;
			else if (upperLimit instanceof Double) minVal = Double.MIN_VALUE;
			else throw new UnsupportedOperationException("Unsupported number type: " + upperLimit.getClass().getSimpleName());
			val = numberValueLimits.put(key, new Vector2<Number>(minVal, upperLimit));
		} else {
			val.y = upperLimit;
		}

		return val;
	}
}
