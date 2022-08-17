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
		if (ChecagemGramatica.prefixas.isEmpty()) {
			String arquivo = ChecagemConstantes.CHECAGENS + Constantes.SEPARADOR + ChecagemConstantes.CHECAGENS;
			File file = new File(arquivo);
			if (!file.exists()) {
				throw new ChecagemException(ChecagemUtil.class, "ARQUIVO: " + arquivo + " inexistente!");
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

	public static List<Object> processar(String idModulo, String idBloco, Contexto ctx)
			throws ChecagemException, XMLException, IOException {
		Modulo modulo = checagem.getModulo(idModulo);
		if (modulo == null) {
			montarGramaticaArquivo(idModulo, checagem);
		}
		return checagem.processar(idModulo, idBloco, ctx);
	}

	public static String executar(String idModulo, String idBloco, Contexto ctx) {
		StringBuilder sb = new StringBuilder();
		try {
			List<Object> lista = ChecagemUtil.processar(idModulo, idBloco, ctx);
			for (Object object : lista) {
				append(sb, object);
			}
		} catch (ChecagemException | XMLException | IOException e) {
			append(sb, e.getMessage());
		}
		return sb.toString();
	}

	private static void append(StringBuilder sb, Object obj) {
		if (obj != null && !Util.estaVazio(obj.toString())) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}
			sb.append(obj.toString());
		}
	}

	public static void checarModulo(String idModulo) throws ChecagemException {
		Modulo modulo = checagem.getModulo(idModulo);
		if (modulo == null) {
			try {
				montarGramaticaArquivo(idModulo, checagem);
			} catch (XMLException | IOException e) {
				throw new ChecagemException(ChecagemUtil.class, e.getMessage());
			}
		}
	}

	public static void checarEstrutura(String conteudo) throws ChecagemException, XMLException {
		checarGramaticaString(conteudo, checagem);
	}

	public static void atualizarEstrutura(File file, String conteudo) throws ChecagemException, XMLException {
		atualizarGramaticaString(file.getName(), conteudo, checagem);
	}

	public static void atualizarEstrutura(File file) throws ChecagemException, XMLException, IOException {
		atualizarGramaticaArquivo(file.getName(), checagem);
	}

	private static void checarGramaticaString(String conteudo, Checagem checagem)
			throws XMLException, ChecagemException {
		Modulo modulo = new Modulo("tmp");
		lerBlocosString(modulo, conteudo);
		ChecagemGramatica.criarHierarquiaSentencas(modulo.getBlocos());
	}

	private static void montarGramaticaArquivo(String idModulo, Checagem checagem)
			throws ChecagemException, XMLException, IOException {
		Modulo modulo = new Modulo(idModulo);
		lerBlocosArquivo(modulo, false);
		ChecagemGramatica.criarHierarquiaSentencas(modulo.getBlocos());
		checagem.add(modulo);
	}

	private static void atualizarGramaticaArquivo(String idModulo, Checagem checagem)
			throws ChecagemException, XMLException, IOException {
		Modulo modulo = new Modulo(idModulo);
		lerBlocosArquivo(modulo, false);
		ChecagemGramatica.criarHierarquiaSentencas(modulo.getBlocos());
		checagem.set(modulo);
	}

	public static Modulo getModulo(File file) throws ChecagemException, XMLException, IOException {
		Modulo modulo = new Modulo(file.getName());
		lerBlocosArquivo(modulo, true);
		return modulo;
	}

	private static void atualizarGramaticaString(String idModulo, String conteudo, Checagem checagem)
			throws ChecagemException, XMLException {
		Modulo modulo = new Modulo(idModulo);
		lerBlocosString(modulo, conteudo);
		ChecagemGramatica.criarHierarquiaSentencas(modulo.getBlocos());
		checagem.set(modulo);
	}

	private static void lerBlocosArquivo(Modulo modulo, boolean lexicalHandler)
			throws ChecagemException, XMLException, IOException {
		ChecagemHandler handler = new ChecagemHandler(modulo, lexicalHandler);
		File file = new File(ChecagemConstantes.CHECAGENS + Constantes.SEPARADOR + modulo.getId());
		if (file.exists() && file.canRead()) {
			String conteudo = Util.conteudo(file);
			processarXMLModulo(handler, conteudo, lexicalHandler);
		} else {
			throw new ChecagemException(ChecagemUtil.class, "Erro ao carregar o modulo >>> " + modulo.getId());
		}
	}

	private static void lerBlocosString(Modulo modulo, String conteudo) throws XMLException {
		ChecagemHandler handler = new ChecagemHandler(modulo, false);
		processarXMLModulo(handler, conteudo, false);
	}

	private static void processarXMLModulo(ChecagemHandler handler, String conteudo, boolean lexicalHandler)
			throws XMLException {
		StringWriter sw = new StringWriter();
		XMLUtil util = new XMLUtil(sw);
		util.prologo();
		util.abrirTag2("sentencas");
		util.print(conteudo).ql();
		util.finalizarTag("sentencas");
		util.close();
		XML.processar(new ByteArrayInputStream(sw.toString().getBytes()), handler, lexicalHandler);
	}
}