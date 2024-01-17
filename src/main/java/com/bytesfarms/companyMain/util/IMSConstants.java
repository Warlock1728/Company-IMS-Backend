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
	
	
	
	
	public static final String AUTH_TOKEN_URL = "https://zoom.us/oauth/token";
	public static final String CLIENT_ID = "jkvtbpkSza9nY2mYlFG5g";
	public static final String CLIENT_SECRET = "EP2pNojcMwU1tLlfnBSUhf6lf6VkR34u";
	public static final String ACCOUNT_ID = "qqOebfiTR4G0iCZHfJirpQ";
	public static final String MEETING_URL = "https://api.zoom.us/v2/users/me/meetings";
	
	
	
	
	
	public static final String REFRESH_TOKEN_URL = "https://zoom.us/oauth/token";
	public static final String REDIRECT_URI = "https://oauth.pstmn.io/v1/callback";

	public static final String ZOOM_USER_ID = "shivendrasinghbais14@gmail.com";

	public static final String ZOOM_USER_PASSWORD = "Shivendra@1716";

	public static final String SECRET_KEY = "Bytesfarms@BytewiseManager@765123";
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String MAIL_USERNAME = "bytewisemis@gmail.com";
	public static final String MAIL_PASSWORD = "clli tpyo metg izka";

	public static final String SMTP_MAIL_HOST ="smtp.gmail.com";
	public static final String SMTP_MAIL_PORT ="587";
	public static final String RECEIPIENT ="managerbytewise@gmail.com";
	
	
	
	
	public static final String SMTP_HOST = "mail.smtp.host";
	public static final String SMTP_PORT = "mail.smtp.port";
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

}
