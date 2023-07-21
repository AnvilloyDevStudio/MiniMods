package io.github.anvilloystudio.minimods.coremods;

public class Vector2<T extends Number> {
	public T x, y;

	public Vector2(T x, T y) {
		this.x = x;
		this.y = y;
	}

	@SuppressWarnings("unchecked")
	public Vector2<T> add(T x, T y) {
		if (x instanceof Integer) {
			this.x = (T) (Integer) ((Integer) this.x + (Integer) x);
			this.y = (T) (Integer) ((Integer) this.y + (Integer) y);
		} else if (x instanceof Short) {
			this.x = (T) (Short) (short) ((Short) this.x + (Short) x);
			this.y = (T) (Short) (short) ((Short) this.y + (Short) y);
		} else if (x instanceof Byte) {
			this.x = (T) (Byte) (byte) ((Byte) this.x + (Byte) x);
			this.y = (T) (Byte) (byte) ((Byte) this.y + (Byte) y);
		} else if (x instanceof Long) {
			this.x = (T) (Long) ((Long) this.x + (Long) x);
			this.y = (T) (Long) ((Long) this.y + (Long) y);
		} else if (x instanceof Float) {
			this.x = (T) (Float) ((Float) this.x + (Float) x);
			this.y = (T) (Float) ((Float) this.y + (Float) y);
		} else if (x instanceof Double) {
			this.x = (T) (Double) ((Double) this.x + (Double) x);
			this.y = (T) (Double) ((Double) this.y + (Double) y);
		} else {
			throw new IllegalArgumentException("Not Handled. Type: " + x.getClass().getName());
		}

		return this;
	}
}
