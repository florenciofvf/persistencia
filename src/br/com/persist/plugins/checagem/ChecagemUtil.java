package br.com.persist.plugins.checagem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;

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

	public static List<Object> processar(String idModulo, Contexto ctx)
			throws ChecagemException, XMLException, IOException {
		Modulo modulo = checagem.getModulo(idModulo);
		if (modulo == null) {
			montarGramaticaArquivo(idModulo, checagem);
		}
		return checagem.processar(idModulo, ctx);
	}

	public static void checarEstrutura(String conteudo) throws ChecagemException, XMLException {
		checarGramaticaString(conteudo, checagem);
	}

	public static void atualizarEstrutura(File file, String conteudo) throws ChecagemException, XMLException {
		atualizarGramaticaString(file.getName(), conteudo, checagem);
	}

	public static void atualizarEstrutura(File file) throws ChecagemException, XMLException, IOException {
		montarGramaticaArquivo(file.getName(), checagem);
	}

	private static void checarGramaticaString(String conteudo, Checagem checagem)
			throws XMLException, ChecagemException {
		List<Bloco> blocos = lerBlocosString(conteudo);
		ChecagemGramatica.criarHierarquiaSentencas(blocos);
	}

	private static void montarGramaticaArquivo(String idModulo, Checagem checagem)
			throws ChecagemException, XMLException, IOException {
		List<Bloco> blocos = lerBlocosArquivo(idModulo);
		ChecagemGramatica.criarHierarquiaSentencas(blocos);
		Modulo modulo = new Modulo(idModulo);
		modulo.addBlocos(blocos);
		checagem.add(modulo);
	}

	private static void atualizarGramaticaString(String idModulo, String conteudo, Checagem checagem)
			throws ChecagemException, XMLException {
		List<Bloco> blocos = lerBlocosString(conteudo);
		ChecagemGramatica.criarHierarquiaSentencas(blocos);
		Modulo modulo = new Modulo(idModulo);
		modulo.addBlocos(blocos);
		checagem.set(modulo);
	}

	private static List<Bloco> lerBlocosArquivo(String idModulo) throws XMLException, IOException {
		ChecagemHandler handler = new ChecagemHandler();
		File file = new File(ChecagemConstantes.CHECAGENS + Constantes.SEPARADOR + idModulo);
		if (file.exists() && file.canRead()) {
			String conteudo = Util.conteudo(file);
			processarXMLModulo(handler, conteudo);
		}
		return handler.getBlocos();
	}

	private static List<Bloco> lerBlocosString(String conteudo) throws XMLException {
		ChecagemHandler handler = new ChecagemHandler();
		processarXMLModulo(handler, conteudo);
		return handler.getBlocos();
	}

	private static void processarXMLModulo(ChecagemHandler handler, String conteudo) throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		util.prologo();
		util.abrirTag2("sentencas");
		util.print(conteudo).ql();
		util.finalizarTag("sentencas");
		util.close();
		XML.processar(new ByteArrayInputStream(sw.toString().getBytes()), handler);
	}
}