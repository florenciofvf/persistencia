package br.com.persist.plugins.instrucao.biblionativo;

public class ILinha {
	private ILinha() {
	}

	@Biblio(4)
	public static String getString(Object linha) {
		Linha entityLinha = (Linha) linha;
		return entityLinha.getString();
	}

	@Biblio(1)
	public static void setString(Object linha, Object string) {
		Linha entityLinha = (Linha) linha;
		entityLinha.setString((String) string);
	}

	@Biblio(2)
	public static String getStringEntre(Object objLinha, Object stringInicio, Object stringFinal) {
		Linha linha = (Linha) objLinha;
		String strInicio = (String) stringInicio;
		String strFinal = (String) stringFinal;
		return linha.getStringEntre(strInicio, strFinal);
	}

	@Biblio(3)
	public static void setStringEntre(Object objLinha, Object stringInicio, Object stringFinal, Object stringNova) {
		Linha linha = (Linha) objLinha;
		String strInicio = (String) stringInicio;
		String strFinal = (String) stringFinal;
		String strNova = (String) stringNova;
		linha.setStringEntre(strInicio, strFinal, strNova);
	}
}