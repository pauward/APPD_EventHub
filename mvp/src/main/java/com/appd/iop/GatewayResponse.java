package com.appd.iop;

public class GatewayResponse {
	private String UUID;

	public GatewayResponse(String uuid){
		this.setUUID(uuid);
	}
	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}
	
}
