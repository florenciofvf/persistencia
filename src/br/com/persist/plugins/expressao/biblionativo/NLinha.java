package br.com.persist.plugins.expressao.biblionativo;

public class NLinha {
	private NLinha() {
	}

	@Biblio(0)
	public static String getString(Object linha) {
		Linha objLinha = (Linha) linha;
		return objLinha.getString();
	}

	@Biblio(1)
	public static void setString(Object linha, Object string) {
		Linha objLinha = (Linha) linha;
		objLinha.setString((String) string);
	}

	@Biblio(2)
	public static String getStringEntre(Object linha, Object ini, Object fim) {
		Linha objLinha = (Linha) linha;
		return objLinha.getStringEntre((String) ini, (String) fim);
	}

	@Biblio(3)
	public static void setStringEntre(Object linha, Object ini, Object fim, Object nova) {
		Linha objLinha = (Linha) linha;
		objLinha.setStringEntre((String) ini, (String) fim, (String) nova);
	}

	@Biblio(4)
	public static Lista getLinhasQueContem(Object arquivo, Object strProcurado) throws IllegalAccessException {
		Lista resposta = new Lista();
		Arquivo objArquivo = (Arquivo) arquivo;
		String procurado = (String) strProcurado;
		Lista lista = objArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			Linha linha = (Linha) lista.get(i);
			if (linha.contem(procurado)) {
				resposta.add(linha);
			}
		}
		return resposta;
	}

	@Biblio(5)
	public static Lista getLinhasQueContemExtremos(Object arquivo, Object ini, Object fim)
			throws IllegalAccessException {
		Lista resposta = new Lista();
		Arquivo objArquivo = (Arquivo) arquivo;
		String strIni = (String) ini;
		String strFim = (String) fim;
		Lista lista = objArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			Linha linha = (Linha) lista.get(i);
			if (linha.contemExtremos(strIni, strFim)) {
				resposta.add(linha);
			}
		}
		return resposta;
	}
}