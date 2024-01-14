package com.bytesfarms.companyMain.util;

import org.springframework.stereotype.Component;

@Component	
public class IMSConstants {

	public static final String CREATE_ZOOM_MEET = "https://api.zoom.us/v2/users/me/meetings";

	// O-AUTH 2.0 FOR ZOOM
	public static final String TOKEN_NAME = "ZOOM API";
	public static final String GRANT_TYPE = "authorization_code";
	public static final String CALLBACK_URL = "https://oauth.pstmn.io/v1/callback";
	public static final String AUTH_URL = "https://zoom.us/oauth/authorize";
	public static final String ACCESS_TOKEN_URL = "https://zoom.us/oauth/token";
	public static final String CLIENT_ID = "rIFk56kMT_6F5G54NvzXKA";
	public static final String CLIENT_SECRET = "oJXiyyjW5TYBr1knfR5c6sv9loX1c3d1";
	public static final String REFRESH_TOKEN_URL = "https://zoom.us/oauth/token";
	public static final String REDIRECT_URI = "https://oauth.pstmn.io/v1/callback";

	public static final String ZOOM_USER_ID = "shivendrasinghbais14@gmail.com";

	public static final String ZOOM_USER_PASSWORD = "Shivendra@1716";

	public static final String SECRET_KEY = "Bytesfarms@BytewiseManager@765123";

}
