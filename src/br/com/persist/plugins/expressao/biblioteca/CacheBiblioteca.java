package br.com.persist.plugins.expressao.biblioteca;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoNativaContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.organiza.PacoteContexto;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.processador.Instrucao;
import br.com.persist.plugins.expressao.processador.Instrucoes;

public class CacheBiblioteca {
	public static final File ROOT = new File(ExpressaoConstantes.EXPRESSOES);
	public static final File COMPILADOS = new File(ROOT, "compilados");
	private final Map<String, Biblioteca> mapaBibliotecas;

	public CacheBiblioteca() {
		mapaBibliotecas = new HashMap<>();
	}

	public boolean contem(String nomeBiblioAbsoluto) {
		return mapaBibliotecas.get(nomeBiblioAbsoluto) != null;
	}

	public Biblioteca getBiblioteca(String nomeBiblioAbsoluto) throws ExpressaoException {
		Biblioteca resp = mapaBibliotecas.get(nomeBiblioAbsoluto);
		if (resp == null) {
			resp = lerBiblioteca(nomeBiblioAbsoluto);
			if (resp == null) {
				throw new ExpressaoException("erro.biblio_inexistente", nomeBiblioAbsoluto);
			}
			mapaBibliotecas.put(nomeBiblioAbsoluto, resp);
		}
		return resp;
	}

	private Biblioteca lerBiblioteca(String nomeBiblioAbsoluto) throws ExpressaoException {
		File file = new File(COMPILADOS, converter(nomeBiblioAbsoluto) + Biblioteca.EXTENSAO);
		List<String> arquivo = ArquivoUtil.lerArquivo(file);
		if (arquivo.isEmpty()) {
			return null;
		}
		Biblioteca biblioteca = new Biblioteca(new File(COMPILADOS, converter(nomeBiblioAbsoluto)));
		AtomicReference<Funcao> funcaoSelecionada = new AtomicReference<>();
		Iterator<String> it = arquivo.iterator();
		while (it.hasNext()) {
			String linha = it.next();
			processar(biblioteca, funcaoSelecionada, linha);
		}
		return biblioteca;
	}

	private static String converter(String nomeBiblioAbsoluto) throws ExpressaoException {
		checarNomeBiblioAbsoluto(nomeBiblioAbsoluto);
		return Util.replaceAll(nomeBiblioAbsoluto, ".", Constantes.SEPARADOR);
	}

	private static void checarNomeBiblioAbsoluto(String nomeBiblioAbsoluto) throws ExpressaoException {
		if (nomeBiblioAbsoluto == null) {
			throw new ExpressaoException(
					"Nome absoluto de biblioteca nulo >>> CacheBiblioteca.checarNomeBiblioAbsoluto(String s)", false);
		}
		if (nomeBiblioAbsoluto.indexOf('.') == -1) {
			throw new ExpressaoException("Nome de biblioteca inv\u00E1lido >>> " + nomeBiblioAbsoluto, false);
		}
	}

	private void processar(Biblioteca biblioteca, AtomicReference<Funcao> funcaoSelecionada, String linha)
			throws ExpressaoException {
		if (linha.startsWith(FuncaoContexto.PREFIXO_FUNCAO)) {
			Funcao funcao = criarFuncao(biblioteca, linha);
			biblioteca.addFuncao(funcao);
			funcaoSelecionada.set(funcao);
		} else if (linha.startsWith(FuncaoNativaContexto.PREFIXO_FUNCAO_NATIVA)) {
			Funcao funcao = criarFuncaoNativa(biblioteca, linha);
			biblioteca.addFuncao(funcao);
			funcaoSelecionada.set(funcao);
		} else if (linha.startsWith(PacoteContexto.PREFIXO_PACKAGE)) {
			String string = linha.substring(PacoteContexto.PREFIXO_PACKAGE.length());
			biblioteca.setNomePacote(string);
		} else if (linha.startsWith(AliasContexto.PREFIXO_ALIAS)) {
			String string = linha.substring(AliasContexto.PREFIXO_ALIAS.length());
			biblioteca.addAlias(string);
		} else if (linha.startsWith(ParametroContexto.PREFIXO_PARAMETRO)) {
			String nomeParametro = linha.substring(ParametroContexto.PREFIXO_PARAMETRO.length());
			if (funcaoSelecionada.get() == null) {
				throw new ExpressaoException("erro.parametro_sem_funcao", biblioteca.getNomeAbsoluto(), nomeParametro);
			}
			funcaoSelecionada.get().addParametro(nomeParametro);
		} else if (linha.startsWith(FuncaoContexto.PREFIXO_TIPO_VOID)) {
			if (funcaoSelecionada.get() == null) {
				throw new ExpressaoException("erro.tipo_sem_funcao", biblioteca.getNomeAbsoluto());
			}
			funcaoSelecionada.get().setTipoVoid(true);
		} else if (linhaInstrucao(linha)) {
			processarInstrucao(biblioteca, funcaoSelecionada.get(), linha);
		}
	}

	private Funcao criarFuncao(Biblioteca biblioteca, String linha) throws ExpressaoException {
		String nomeEParametros = linha.substring(FuncaoContexto.PREFIXO_FUNCAO.length());
		int pos = nomeEParametros.indexOf(' ');
		if (pos == -1) {
			return new Funcao(biblioteca, nomeEParametros);
		}
		String nome = nomeEParametros.substring(0, pos);
		String parametros = nomeEParametros.substring(pos + 1);
		String[] array = parametros.split(Instrucao.CIFRAO);
		Funcao funcaoParent = biblioteca.getFuncao(array[array.length - 1]);
		Funcao funcao = new Funcao(biblioteca, nome);
		funcaoParent.add(funcao);
		return funcao;
	}

	private Funcao criarFuncaoNativa(Biblioteca biblioteca, String linha) {
		linha = linha.substring(FuncaoNativaContexto.PREFIXO_FUNCAO_NATIVA.length());
		int pos = linha.indexOf(' ');
		String biblioNativa = linha.substring(0, pos);
		String nome = linha.substring(pos + 1);
		Funcao funcao = new Funcao(biblioteca, nome);
		funcao.setBiblioNativa(biblioNativa);
		return funcao;
	}

	private boolean linhaInstrucao(String s) {
		char c = s.charAt(0);
		return c >= '0' && c <= '9';
	}

	private void processarInstrucao(Biblioteca biblioteca, Funcao funcao, String linha) throws ExpressaoException {
		if (funcao == null) {
			throw new ExpressaoException("erro.instrucao_sem_funcao", biblioteca.getNomeAbsoluto(), linha);
		}
		Instrucao instrucao = getInstrucao(linha, biblioteca);
		funcao.addInstrucao(instrucao);
	}

	private Instrucao getInstrucao(String linha, Biblioteca biblioteca) throws ExpressaoException {
		String espacos = ExpressaoConstantes.ESPACO + ExpressaoConstantes.ESPACO;
		int pos = linha.indexOf(espacos);
		int indice = Integer.parseInt(linha.substring(0, pos));
		String stringInstrucao = linha.substring(pos + espacos.length());
		return clonarInstrucao(indice, stringInstrucao, biblioteca);
	}

	private Instrucao clonarInstrucao(int indice, String instrucao, Biblioteca biblioteca) throws ExpressaoException {
		int pos = instrucao.indexOf(' ');
		if (pos == -1) {
			Instrucao novo = Instrucoes.get(instrucao, biblioteca).novo();
			novo.setIndice(indice);
			return novo;
		} else {
			String nome = instrucao.substring(0, pos);
			String parametros = instrucao.substring(pos + 1);
			Instrucao novo = Instrucoes.get(nome, biblioteca).novo();
			novo.setParametros(parametros);
			novo.setIndice(indice);
			return novo;
		}
	}

	public static File getArquivoCompilado(BibliotecaContexto biblioteca) throws ExpressaoException {
		if (biblioteca == null) {
			throw new ExpressaoException("Biblioteca nula.", false);
		}
		PacoteContexto pacoteContexto = biblioteca.getPackage();
		String pack = Util.replaceAll(pacoteContexto.getNomeAbsoluto(), ".", Constantes.SEPARADOR);
		File path = new File(COMPILADOS, pack);
		if (!path.isDirectory() && !path.mkdirs()) {
			throw new ExpressaoException("erro.criar_diretorios", path.getPath());
		}
		return new File(path, biblioteca.getNome() + Biblioteca.EXTENSAO);
	}

	public static File arquivoParaCompilar(String nomeBiblioAbsoluto) throws ExpressaoException {
		checarNomeBiblioAbsoluto(nomeBiblioAbsoluto);
		int pos = nomeBiblioAbsoluto.lastIndexOf('.');
		String name = nomeBiblioAbsoluto.substring(pos + 1);
		nomeBiblioAbsoluto = nomeBiblioAbsoluto.substring(0, pos);
		String pack = Util.replaceAll(nomeBiblioAbsoluto, ".", Constantes.SEPARADOR);
		File path = new File(ROOT, pack);
		if (!path.isDirectory() && !path.mkdirs()) {
			throw new ExpressaoException("erro.criar_diretorios", path.getPath());
		}
		return new File(path, name);
	}

	@Override
	public String toString() {
		return "CacheBiblioteca size=" + mapaBibliotecas.size() + "\n" + mapaBibliotecas.toString();
	}
}