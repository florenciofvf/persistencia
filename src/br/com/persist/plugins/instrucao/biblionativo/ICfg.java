package br.com.persist.plugins.instrucao.biblionativo;

public class ICfg {
	private static String iniNorm = "";
	private static String fimNorm = "";
	private static String iniInfo = "";
	private static String fimInfo = "";
	private static String iniWarn = "";
	private static String fimWarn = "";
	private static String iniErro = "";
	private static String fimErro = "";

	private ICfg() {
	}

	@Biblio(0)
	public static void setIniNorm(Object object) {
		if (object != null) {
			iniNorm = object.toString();
		}
	}

	@Biblio(1)
	public static void setIniInfo(Object object) {
		if (object != null) {
			iniInfo = object.toString();
		}
	}

	@Biblio(2)
	public static void setIniWarn(Object object) {
		if (object != null) {
			iniWarn = object.toString();
		}
	}

	@Biblio(3)
	public static void setIniErro(Object object) {
		if (object != null) {
			iniErro = object.toString();
		}
	}

	@Biblio(4)
	public static void setFimNorm(Object object) {
		if (object != null) {
			fimNorm = object.toString();
		}
	}

	@Biblio(5)
	public static void setFimInfo(Object object) {
		if (object != null) {
			fimInfo = object.toString();
		}
	}

	@Biblio(6)
	public static void setFimWarn(Object object) {
		if (object != null) {
			fimWarn = object.toString();
		}
	}

	@Biblio(7)
	public static void setFimErro(Object object) {
		if (object != null) {
			fimErro = object.toString();
		}
	}

	@Biblio(8)
	public static String norm(Object object) {
		if (object == null) {
			return "";
		}
		String string = object.toString();
		return iniNorm + string + fimNorm;
	}

	@Biblio(9)
	public static String info(Object object) {
		if (object == null) {
			return "";
		}
		String string = object.toString();
		return iniInfo + string + fimInfo;
	}

	@Biblio(10)
	public static String warn(Object object) {
		if (object == null) {
			return "";
		}
		String string = object.toString();
		return iniWarn + string + fimWarn;
	}

	@Biblio(11)
	public static String erro(Object object) {
		if (object == null) {
			return "";
		}
		String string = object.toString();
		return iniErro + string + fimErro;
	}
}