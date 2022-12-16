package io.github.anvilloystudio.minimods.api;

import io.github.anvilloystudio.minimods.core.ModContainer;
import io.github.anvilloystudio.minimods.core.Mods;
import io.github.anvilloystudio.minimods.loader.FileHandler;
import io.github.anvilloystudio.minimods.loader.LoaderUtils;
import io.github.anvilloystudio.minimods.loader.ModConfigFileHandler;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for mod configuration.
 * There are 2 stages of configs:
 * 	The loader stage - available only in pre-init stage.
 * 	The game stage - available only after pre-init stage.
 */
public class ModConfig extends JSONObject {
	private static final HashMap<String, ModConfig> configs = new HashMap<>();

	/**
	 * Get the available and registered config from the list.
	 * @param modId The mod ID of the associated mod config.
	 * @return The available associated mod config. `null` otherwise.
	 */
	@Nullable
	public static ModConfig getConfig(String modId) {
		return configs.get(modId);
	}

	/**
	 * Create and return a new config instance if there is not an existed one.<br/>
	 * The config with the caller mod class will be created.
	 * @return The newly created config. `null` if there is existed one.
	 */
	@Nullable
	public static ModConfig createConfigIfNotExist() { return createConfigIfNotExist(getCallerModId()); }
	/**
	 * Create and return a new config instance if there is not an existed one.<br/>
	 * The config with specified mod ID will be created.
	 * @param modId The mod ID.
	 * @return The newly created config. `null` if there is existed one.
	 */
	@Nullable
	public static ModConfig createConfigIfNotExist(String modId) {
		ModConfig cfg = new ModConfig(modId, false);
		if (addConfig(modId, cfg))
			return cfg;
		else
			return null;
	}

	/**
	 * Add the given config to the mod config list.
	 * @param modId The associated mod ID of the given config.
	 * @param cfg The related mod config.
	 * @return `true` if there was no associated mod config. `false` and the given config was not added to the list otherwise.
	 */
	private static boolean addConfig(String modId, ModConfig cfg) {
		return configs.putIfAbsent(modId, cfg) == null; // The config exists.
	}

	private final String modId;
	private final File file;
	private final ModContainer mod;
	private final boolean isValidMod;

	/**
	 * Creating a mod config by the mod package it belongs to.
	 */
	public ModConfig() { this(1); }
	private ModConfig(int i) { this(getCallerModId(i)); }
	/**
	 * Creating a mod config by the specified mod ID.
	 * @param modId The mod ID.
	 */
	public ModConfig(String modId) { this(modId, true); }
	private ModConfig(String modId, boolean addToList) {
		this.modId = modId;
		this.file = new File(ModConfigFileHandler.configDir, this.modId + ".cfg");
		FileHandler.checkAndDeleteIfDir(file);
		if (addToList) addConfig(this.modId, this);
		mod = Mods.mods.stream().filter(m -> m.metadata.modId.equals(modId)).findAny().orElse(null);
		isValidMod = mod != null;
		initializeConfig();
	}
	/**
	 * Creating a mod config by the mod ID and the file.<br/>
	 * This instance is not added into the config list.
	 * @param modId The mod ID.
	 * @param file The config file (in JSON format).
	 */
	public ModConfig(String modId, File file) {
		this.modId = modId;
		this.file = file;
		mod = Mods.mods.stream().filter(m -> m.metadata.modId.equals(modId)).findAny().orElse(null);
		isValidMod = mod != null;
		initializeConfig();
	}

	private void initializeConfig() {
		try {
			file.toPath().getParent().toFile().mkdirs();
			if (!file.createNewFile()) {
				String s = LoaderUtils.readStringFromInputStream(Files.newInputStream(file.toPath()));
				if (!s.isEmpty()) {
					JSONObject json = new JSONObject();
					for (String k : json.keySet()) {
						put(k, json.get(k));
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getCallerModId() { return getCallerModId(1); }
	private static String getCallerModId(int i) {
		try {
			return LoaderUtils.getCallerModId(1 + i);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private final HashMap<String, Vector2<Number>> numberValueLimits = new HashMap<>();

	/**
	 * Setting the number value range bounds by the key.
	 * @param key The corresponding key to the actual value.
	 * @param limits The value range bounds: the lower and upper bounds.
	 * @throws IllegalArgumentException if the {@code limits} generic number type is unsupported.
	 */
	public void setLimit(String key, Vector2<Number> limits) throws IllegalArgumentException {
		validateNumberClass((Class<?>) ((ParameterizedType) limits.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		numberValueLimits.put(key, limits);
	}
	/**
	 * Getting the lower and upper limits on the specified key.
	 * @param key The corresponding key to the value.
	 * @return The limit
	 * @param <T>
	 */
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
			else if (lowerLimit instanceof Long || lowerLimit instanceof BigInteger) maxVal = Long.MAX_VALUE;
			else if (lowerLimit instanceof Float) maxVal = Float.MAX_VALUE;
			else if (lowerLimit instanceof Double || lowerLimit instanceof BigDecimal) maxVal = Double.MAX_VALUE;
			else throw new UnsupportedOperationException("Unsupported number type: " + lowerLimit.getClass().getSimpleName());
			val = numberValueLimits.put(key, new Vector2<>(lowerLimit, maxVal));
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
			else if (upperLimit instanceof Long || upperLimit instanceof BigInteger) minVal = Long.MIN_VALUE;
			else if (upperLimit instanceof Float) minVal = Float.MIN_VALUE;
			else if (upperLimit instanceof Double || upperLimit instanceof BigDecimal) minVal = Double.MIN_VALUE;
			else throw new UnsupportedOperationException("Unsupported number type: " + upperLimit.getClass().getSimpleName());
			val = numberValueLimits.put(key, new Vector2<>(minVal, upperLimit));
		} else {
			val.y = upperLimit;
		}

		return val;
	}

	private final HashMap<String, Number> defaultNumericValues = new HashMap<>();

	/**
	 * Setting the default numeric value if the numeric value out of set bounds in validation.
	 * @param key The corresponding key to the value.
	 * @param val The default value to the corresponding key.
	 * @throws IllegalArgumentException if the number is not supported.
	 */
	public <T extends Number> void setDefaultNumericValue(String key, T val) throws IllegalArgumentException {
		validateNumberClass(val.getClass());
		defaultNumericValues.put(key, val);
	}

	public Number getDefaultNumericValue(String key) { return defaultNumericValues.get(key); }

	/**
	 * Validating if the value out of the set bounds.
	 * @param key The key of the mapping.
	 * @return If the value is changed.
	 */
	public boolean validateNumericLimit(String key) {
		Vector2<Number> vector = numberValueLimits.get(key);
		if (vector != null) {
			Class<?> type = (Class<?>) ((ParameterizedType) vector.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			boolean exceededLower = false, exceededUpper = false;
			boolean resetDefaultValue = false, restrictValueBounds = false;
			boolean setDefaultValueWhenOutOfScope = setDefaultValueWhenOutOfScope();
			boolean setDefaultValueWhenTypeMismatched = setDefaultValueWhenTypeMismatched();
			Class<?> methodClass = type;
			Method compareTo;
			while (true) {
				try {
					compareTo = methodClass.getMethod("compareTo", methodClass);
					break;
				} catch (NoSuchMethodException e) {
					methodClass = methodClass.getSuperclass();
				}
			}

			Number lowerBound = vector.x;
			Number upperBound = vector.y;
			try {
				if (Integer.class.isAssignableFrom(type)) {
						Integer val = getInt(key);
						exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
						exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (Byte.class.isAssignableFrom(type)) {
						Byte val = (byte) getInt(key);
						exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
						exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (Short.class.isAssignableFrom(type)) {
						Short val = (short) getInt(key);
						exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
						exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (Long.class.isAssignableFrom(type)) {
						Long val = getLong(key);
						exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
						exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (BigInteger.class.isAssignableFrom(type)) {
					BigInteger val = getBigInteger(key);
					exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
					exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (Float.class.isAssignableFrom(type)) {
					Float val = getFloat(key);
					exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
					exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (Double.class.isAssignableFrom(type)) {
					Double val = getDouble(key);
					exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
					exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else if (BigDecimal.class.isAssignableFrom(type)) {
					BigDecimal val = getBigDecimal(key);
					exceededLower = (int) compareTo.invoke(lowerBound, val) > 0;
					exceededUpper = (int) compareTo.invoke(upperBound, val) < 0;
				} else
					throw new UnsupportedOperationException(modId + "config: Unsupported number type: " + type.getSimpleName());
			} catch (JSONException /* The value is missing or type mismatched. */ | InvocationTargetException | IllegalAccessException e) {
				if (setDefaultValueWhenTypeMismatched) resetDefaultValue = true;
			}

			if (exceededLower || exceededUpper) {
				if (setDefaultValueWhenOutOfScope) {
					if (defaultNumericValues.containsKey(key)) resetDefaultValue = true;
					else {
						Logger.warn("{} config: No default found for {}, restricting value bounds instead.", modId, key);
						restrictValueBounds = true;
					}
				} else restrictValueBounds = true;
				if (restrictValueBounds) {
					if (exceededLower) put(key, lowerBound);
					else put(key, upperBound);
				}
			}

			if (resetDefaultValue) {
				if (defaultNumericValues.containsKey(key))
					put(key, (Object) getAndValidateDefaultNumericValue(type, defaultNumericValues.get(key)));
				else
					Logger.warn("{} config: No default found for {}, ignoring...", modId, key);
			}
		}

		return false;
	}

	/**
	 * Validating all key-value pairs that the number value limits defined.
	 * @return If there is any value changed.
	 */
	public boolean validateAllNumericLimits() {
		boolean anyValueChanged = false;
		for (String k : numberValueLimits.keySet()) {
			if (validateNumericLimit(k)) anyValueChanged = true;
		}

		return anyValueChanged;
	}

	@SuppressWarnings("unchecked")
	private <T extends Number> T getAndValidateDefaultNumericValue(Class<?> targetClass, Number defaultValue) {
		if (targetClass != defaultValue.getClass())
			throw new IllegalArgumentException(String.format("Type %s does not match to the type of default numeric value %s", targetClass.getCanonicalName(), defaultValue.getClass().getCanonicalName()));
		return (T) defaultValue;
	}

	private boolean setDefaultValueWhenOutOfScope() {
		if (isValidMod) return mod.settings.configValueResetDefaultIfOutOfScope;
		return true;
	}
	private boolean setDefaultValueWhenTypeMismatched() {
		if (isValidMod) return !mod.settings.configNumericLimitIgnoreOtherTypedValue;
		return true;
	}

	/**
	 * Validates if the number class is a supported class.
	 * @param clazz The class of number to check.
	 * @throws IllegalArgumentException if the number is not supported.
	 */
	private void validateNumberClass(Class<?> clazz) throws IllegalArgumentException {
		if (!checkSupportedNumberClass(clazz))
			throw new IllegalArgumentException(String.format("Type %s of Number is not supported.", clazz.getCanonicalName()));
	}

	/**
	 * Checks if the provided number is an instance of a supported type.
	 * @param number The number instance to check.
	 * @return if the number is supported.
	 */
	public boolean checkSupportedNumberType(Number number) {
		return checkSupportedNumberClass(number.getClass());
	}

	private boolean checkSupportedNumberClass(Class<?> clazz) {
		return Integer.class.isAssignableFrom(clazz) ||
			Byte.class.isAssignableFrom(clazz) ||
			Short.class.isAssignableFrom(clazz) ||
			Long.class.isAssignableFrom(clazz) ||
			BigInteger.class.isAssignableFrom(clazz) ||
			Float.class.isAssignableFrom(clazz) ||
			Double.class.isAssignableFrom(clazz) ||
			BigDecimal.class.isAssignableFrom(clazz);
	}

	@Override
	public String toString() {
		return toString(4);
	}

	public String toJSONC(int indentFactor) {
		StringBuilder json = new StringBuilder(toString(indentFactor));
		for (Map.Entry<String, Vector2<Number>> e : numberValueLimits.entrySet()) {
			json.append(String.format("\n// Key \"%s\"; Lower Limit: %s; Upper Limit: %s;", e.getKey(), e.getValue().x, e.getValue().y));
		}

		return json.append("\n").toString();
	}

	/**
	 * Force reloading all the configs from the config file.
	 * Overriding all existed configs, even if the file is empty.<br/>
	 * Warning: All unsaved changes will be lost.
	 */
	public void forceReloadConfig() {
		initializeConfig();
	}

	/**
	 * Saving all the configs into the config file.
	 * All unloaded changes will be lost.
	 */
	public void saveFile() {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(toJSONC(4));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
} // TODO https://docs.minecraftforge.net/en/1.19.x/misc/config/#configuration
