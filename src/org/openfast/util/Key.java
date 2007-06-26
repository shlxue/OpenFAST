package org.openfast.util;

public class Key {
	private final Object key1;
	private final Object key2;

	public Key(Object key1, Object key2)
	{
		if (key1 == null) throw new NullPointerException();
		if (key2 == null) throw new NullPointerException();
		this.key1 = key1;
		this.key2 = key2;
		if (key1 == null)
		{
			throw new NullPointerException();
		}	
			if (key2 == null) {
				throw new NullPointerException();
			}
	}
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Key)) return false;
		Key other = ((Key) obj);
		return other.key1.equals(key1) && other.key2.equals(key2);
	}
	public int hashCode() {
		return key1.hashCode() * 37 + key2.hashCode();
	}
}
