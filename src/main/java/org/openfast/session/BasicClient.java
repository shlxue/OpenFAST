package org.openfast.session;

public class BasicClient implements Client {

	private final String name;
	private final String vendorId;

	public BasicClient(String clientName, String vendorId) {
		this.name = clientName;
		this.vendorId = vendorId;
	}

	public String getName() {
		return name;
	}

	public String getVendorId() {
		return vendorId;
	}

	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || !(obj instanceof BasicClient)) return false;
		return ((BasicClient) obj).name.equals(name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
