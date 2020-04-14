package com.appd.iop;

/**
 * Response format to client
 * 
 * @author niwar
 *
 */
public class GatewayResponse {
	private String UUID;

	public GatewayResponse(String uuid) {
		this.setUUID(uuid);
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

}
