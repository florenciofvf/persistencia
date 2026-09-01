package br.com.persist.plugins.expressao.biblionativo;

import java.util.Base64;

public class NBase64 {
	private NBase64() {
	}

	@Biblio(0)
	public static String encode(Object string) {
		if (string == null) {
			return "";
		}
		String str = string.toString();
		if (str == null) {
			return "";
		}
		return Base64.getEncoder().encodeToString(str.getBytes());
	}

	@Biblio(1)
	public static String decode(Object string) {
		if (string == null) {
			return "";
		}
		String str = string.toString();
		if (str == null) {
			return "";
		}
		return new String(Base64.getDecoder().decode(str));
	}

	@Biblio(2)
	public static String encodeFileName(Object string) {
		if (string == null) {
			return "";
		}
		String str = string.toString();
		if (str == null) {
			return "";
		}
		return Base64.getUrlEncoder().encodeToString(str.getBytes());
	}
}