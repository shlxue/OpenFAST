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

}
