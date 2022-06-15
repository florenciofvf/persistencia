package br.com.persist.plugins.checagem;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.marca.XMLException;

public class ChecagemUtil {
	private static final Checagem checagem = new Checagem();
	private static final Logger LOG = Logger.getGlobal();

	private ChecagemUtil() {
	}

	public static void inicializar() throws ChecagemException {
		if (ChecagemGramatica.map.isEmpty()) {
			String arquivo = ChecagemConstantes.CHECAGENS + Constantes.SEPARADOR + ChecagemConstantes.CHECAGENS;
			File file = new File(arquivo);
			if (!file.exists()) {
				throw new ChecagemException("ARQUIVO: " + arquivo + " inexistente!");
			}
			try {
				ChecagemGramatica.mapear(arquivo);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			} catch (ClassNotFoundException ex) {
				LOG.log(Level.SEVERE, Constantes.ERRO, ex);
			}
		}
	}

	public static List<Object> processar(String arquivo, Contexto ctx) throws ChecagemException {
		List<Sentenca> sentencas = checagem.map.get(arquivo);
		if (sentencas == null) {
			ChecagemGramatica.montarGramatica(arquivo, checagem);
		}
		return checagem.processar(arquivo, ctx);
	}

	public static void checarEstrutura(String conteudo) throws ChecagemException, XMLException {
		ChecagemGramatica.checarGramatica(conteudo, checagem);
	}

	public static void atualizarEstrutura(File file, String conteudo) throws ChecagemException, XMLException {
		ChecagemGramatica.atualizarGramatica(file.getName(), conteudo, checagem);
	}

	public static void atualizarEstrutura(File file) throws ChecagemException {
		ChecagemGramatica.montarGramatica(file.getName(), checagem);
	}
}