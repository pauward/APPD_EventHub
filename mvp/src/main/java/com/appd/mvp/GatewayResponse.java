package com.appd.mvp;

public class GatewayResponse {
	private String UUID;

	GatewayResponse(String uuid){
		this.setUUID(uuid);
	}
	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}
	
}
