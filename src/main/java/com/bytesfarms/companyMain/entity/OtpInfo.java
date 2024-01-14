package com.bytesfarms.companyMain.entity;

public class OtpInfo {
	private final String otp;
	private final long creationTime;

	public OtpInfo(String otp, long creationTime) {
		this.otp = otp;
		this.creationTime = creationTime;
	}

	public String getOtp() {
		return otp;
	}

	public boolean isValid(long expirationTimeMillis) {
		return System.currentTimeMillis() - creationTime <= expirationTimeMillis;
	}
}