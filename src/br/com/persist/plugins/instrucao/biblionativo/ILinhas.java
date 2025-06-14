package br.com.persist.plugins.instrucao.biblionativo;

public class ILinhas {
	private ILinhas() {
	}

	@Biblio(0)
	public static Lista getLinhasQueContem(Object arquivo, Object string) throws IllegalAccessException {
		Arquivo entityArquivo = (Arquivo) arquivo;
		String str = (String) string;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			Linha linha = (Linha) lista.get(i);
			if (linha.contem(str)) {
				resposta.add(linha);
			}
		}
		return resposta;
	}

	@Biblio(1)
	public static Lista getLinhasQueContemExtremos(Object arquivo, Object stringInicio, Object stringFinal)
			throws IllegalAccessException {
		Arquivo entityArquivo = (Arquivo) arquivo;
		String strInicio = (String) stringInicio;
		String strFinal = (String) stringFinal;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			Linha linha = (Linha) lista.get(i);
			if (linha.contemExtremos(strInicio, strFinal)) {
				resposta.add(linha);
			}
		}
		return resposta;
	}
}