package org.openfast.session;

public class Client {
	protected final String name;
	
	public Client(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void disconnect() throws FastConnectionException {
		
	}
	
	public String toString() {
		return name;
	}
	
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Client)) return false;
		return equals((Client) obj);
	}
	
	private boolean equals(Client other) {
		return name.equals(other.name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
