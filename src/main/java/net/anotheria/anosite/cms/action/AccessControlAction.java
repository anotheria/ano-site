package net.anotheria.anosite.cms.action;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import net.anotheria.util.crypt.CryptTool;

public abstract class AccessControlAction extends BaseAction {
	
	public static final String COOKIE_PREFIX = "anoweb.auth.";
	public static final char AUTH_DELIMITER = ':';
	
	private static final String AUTH_KEY = "97531f6c04afcbd529028f3f45221cce";
	private static CryptTool crypt = new CryptTool(AUTH_KEY);
	
	protected String getAuthCookieName(HttpServletRequest req){
		String ctx = req.getContextPath();
		if (ctx==null)
			ctx = "";
		if (ctx.length()>0 && ctx.charAt(0)=='/')
			ctx = ctx.substring(1);
		if (ctx.length()==0)
			ctx = "ROOT";
		return COOKIE_PREFIX+ctx;
	}

	protected Cookie createAuthCookie(HttpServletRequest req, String userId, String password){
		String value = userId+AUTH_DELIMITER+password;
		Cookie c = new Cookie(getAuthCookieName(req), crypt.encryptToHex(value));

		c.setMaxAge(60*60*24*90);
		
		return c;
	}

	protected CryptTool getCryptTool(){
		return crypt;
	}
}
