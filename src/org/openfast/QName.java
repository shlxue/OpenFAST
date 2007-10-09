package org.openfast;

public class QName {
	public static final QName NULL = new QName("", "");
	
	private final String namespace;
	private final String name;

	public QName(String name) {
		this(name, "");
	}
	
	public QName(String name, String namespace) {
		if (name == null || namespace == null) throw new NullPointerException();
		this.name = name;
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		QName other = (QName) obj;
		return other.namespace.equals(namespace) && other.name.equals(name);
	}
	
	public int hashCode() {
		return name.hashCode() + 31*namespace.hashCode();
	}
	
	public String toString() {
		return name + "[" + namespace + "]";
	}
}
