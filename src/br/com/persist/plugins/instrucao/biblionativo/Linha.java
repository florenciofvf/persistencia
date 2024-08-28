package br.com.persist.plugins.instrucao.biblionativo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Linha {
	private Linha() {
	}

	@Biblio
	public static Lista iguais(Object arquivo, Object string, Object trim) throws IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		String str = (String) string;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ILinha linha = (ILinha) lista.get(i);
			if (linha.stringEqual(str, Util.TRUE.equals(trim))) {
				resposta.add(linha);
			}
		}
		return resposta;
	}

	@Biblio
	public static Lista iniciaEfinaliza(Object arquivo, Object stringInicio, Object stringFinal, Object trim)
			throws IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		String strInicio = (String) stringInicio;
		String strFinal = (String) stringFinal;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ILinha linha = (ILinha) lista.get(i);
			if (linha.iniciaEfinalizaCom(strInicio, strFinal, Util.TRUE.equals(trim))) {
				resposta.add(linha);
			}
		}
		return resposta;
	}

	@Biblio
	public static Lista entreIniciaEfinaliza(Object arquivo, Object stringInicio, Object stringFinal, Object trim)
			throws IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		String strInicio = (String) stringInicio;
		String strFinal = (String) stringFinal;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ILinha linha = (ILinha) lista.get(i);
			String stringEntre = linha.stringEntre(strInicio, strFinal, Util.TRUE.equals(trim));
			if (stringEntre != null) {
				resposta.add(linha.clonar(stringEntre));
			}
		}
		return resposta;
	}

	@Biblio
	public static Lista entreIniciaEfinalizaReplace(Object arquivo, Object stringInicio, Object stringFinal,
			Object novaString, Object trim) throws IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		String strInicio = (String) stringInicio;
		String strFinal = (String) stringFinal;
		String strNova = (String) novaString;
		Lista resposta = new Lista();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ILinha linha = (ILinha) lista.get(i);
			String stringEntreReplace = linha.stringEntreReplace(strInicio, strFinal, strNova, Util.TRUE.equals(trim));
			if (stringEntreReplace != null) {
				resposta.add(linha.clonar(stringEntreReplace));
			}
		}
		return resposta;
	}

	@Biblio
	public static ILinha get(Object arquivo, Object numero) throws IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		long numeroLinha = ((Number) numero).longValue();
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ILinha linha = (ILinha) lista.get(i);
			if (linha.numeroEqual(numeroLinha)) {
				return linha;
			}
		}
		return null;
	}

	@Biblio
	public static ILinha clonar(Object linha, Object string) {
		ILinha entityLinha = (ILinha) linha;
		return entityLinha.clonar((String) string);
	}

	@Biblio
	public static ILinha criar(Object numero, Object string) {
		return new ILinha(((Number) numero).longValue(), (String) string, (char) 0, '\n');
	}

	@Biblio
	public static ILinha setEsalva(Object arquivo, Object linha, Object charset)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		ILinha entityLinha = (ILinha) linha;
		PrintWriter pw = Arquivo.criarPrintWriter(entityArquivo, (String) charset);
		Lista lista = entityArquivo.getLista();
		long size = lista.size().longValue();
		for (long i = 0, num = 1; i < size; i++, num++) {
			ILinha entity = (ILinha) lista.get(i);
			entityLinha.print(pw, entity, num);
		}
		pw.close();
		return entityLinha;
	}

	@Biblio
	public static void set(Object arquivo, Object linha) throws IllegalAccessException {
		IArquivo entityArquivo = (IArquivo) arquivo;
		ILinha entityLinha = (ILinha) linha;
		Lista lista = entityArquivo.getLista();
		lista.set(entityLinha.getNumero() - 1, entityLinha);
	}
}