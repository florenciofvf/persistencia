package br.com.persist.plugins.expressao.processador;

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
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.organiza.PacoteContexto;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;

public class CacheBiblioteca {
	private static final String ERRO_BIBLIO_INEXISTENTE = "erro.biblio_inexistente";
	public static final File ROOT = new File(ExpressaoConstantes.EXPRESSOES);
	public static final File COMPILADOS = new File(ROOT, "compilados");
	private final Map<String, Biblioteca> bibliotecas;

	public CacheBiblioteca() {
		bibliotecas = new HashMap<>();
	}

	public Biblioteca getBiblioteca(String nome, BibliotecaContexto biblio) throws InstrucaoException {
		if (biblio != null) {
			nome = biblio.getNomeImport(nome);
		}
		Biblioteca resp = bibliotecas.get(nome);
		if (resp == null) {
			resp = lerBiblioteca(nome);
			if (resp == null) {
				throw new InstrucaoException(ERRO_BIBLIO_INEXISTENTE, nome);
			}
			bibliotecas.put(nome, resp);
		}
		return resp;
	}

	public boolean contem(String nomeAbsoluto) {
		return bibliotecas.get(nomeAbsoluto) != null;
	}

	public Biblioteca getBiblioteca(String nome, Biblioteca biblio) throws InstrucaoException {
		if (biblio != null) {
			nome = biblio.getNomeImport(nome);
		}
		Biblioteca resp = bibliotecas.get(nome);
		if (resp == null) {
			resp = lerBiblioteca(nome);
			if (resp == null) {
				throw new InstrucaoException(ERRO_BIBLIO_INEXISTENTE, nome);
			}
			bibliotecas.put(nome, resp);
		}
		return resp;
	}

	public Biblioteca getBiblioteca(String nome) throws InstrucaoException {
		Biblioteca resp = bibliotecas.get(nome);
		if (resp == null) {
			resp = lerBiblioteca(nome);
			if (resp == null) {
				throw new InstrucaoException(ERRO_BIBLIO_INEXISTENTE, nome);
			}
			bibliotecas.put(nome, resp);
		}
		return resp;
	}

	private Biblioteca lerBiblioteca(String nome) throws InstrucaoException {
		Biblioteca biblioteca = null;
		File file = new File(COMPILADOS, get(nome) + Biblioteca.EXTENSAO);
		List<String> arquivo = ArquivoUtil.lerArquivo(file);
		if (arquivo.isEmpty()) {
			return biblioteca;
		}
		biblioteca = new Biblioteca(file);
		Iterator<String> it = arquivo.iterator();
		AtomicReference<Funcao> atomicFuncao = new AtomicReference<>();
		while (it.hasNext()) {
			String linha = it.next();
			processar(nome, biblioteca, atomicFuncao, linha);
		}
		return biblioteca;
	}

	private static String get(String nome) {
		if (nome.indexOf('.') != -1) {
			return Util.replaceAll(nome, ".", Constantes.SEPARADOR);
		}
		return nome;
	}

	public static File getArquivoCompilado(br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto biblioteca)
			throws ExpressaoException {
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

	public static File arquivoParaCompilar(String nome) throws InstrucaoException {
		int pos = nome.lastIndexOf('.');
		if (pos != -1) {
			String name = nome.substring(pos + 1);
			nome = nome.substring(0, pos);
			String pack = Util.replaceAll(nome, ".", Constantes.SEPARADOR);
			File path = new File(ROOT, pack);
			if (!path.isDirectory() && !path.mkdirs()) {
				throw new InstrucaoException("erro.criar_diretorios", path.getPath());
			}
			return new File(path, name);
		}
		return new File(ROOT, nome);
	}

	private void processar(String nome, Biblioteca biblioteca, AtomicReference<Funcao> atomicFuncao, String linha)
			throws InstrucaoException {
		if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO)) {
			Funcao funcao = criarFuncao(linha);
			biblioteca.addFuncao(funcao);
			atomicFuncao.set(funcao);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA)) {
			Funcao funcao = criarFuncaoNativa(linha);
			biblioteca.addFuncao(funcao);
			atomicFuncao.set(funcao);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_PACKAGE)) {
			String string = linha.substring(InstrucaoConstantes.PREFIXO_PACKAGE.length());
			biblioteca.setNomePackage(string);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_IMPORT)) {
			String string = linha.substring(InstrucaoConstantes.PREFIXO_IMPORT.length());
			biblioteca.addImport(string);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_PARAMETRO)) {
			String nomeParametro = linha.substring(InstrucaoConstantes.PREFIXO_PARAMETRO.length());
			if (atomicFuncao.get() == null) {
				throw new InstrucaoException("erro.parametro_sem_funcao", nome, nomeParametro);
			}
			atomicFuncao.get().addParametro(nomeParametro);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_TIPO_VOID)) {
			if (atomicFuncao.get() == null) {
				throw new InstrucaoException("erro.tipo_sem_funcao", nome);
			}
			atomicFuncao.get().setTipoVoid(true);
		} else if (linhaInstrucao(linha)) {
			processarInstrucao(nome, atomicFuncao.get(), linha);
		}
	}

	private boolean linhaInstrucao(String s) {
		char c = s.charAt(0);
		return c >= '0' && c <= '9';
	}

	private Funcao criarFuncao(String linha) {
		linha = linha.substring(InstrucaoConstantes.PREFIXO_FUNCAO.length());
		return new Funcao(linha);
	}

	private Funcao criarFuncaoNativa(String linha) {
		linha = linha.substring(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA.length());
		int pos = linha.indexOf(' ');
		String biblioNativa = linha.substring(0, pos);
		String nomeFuncao = linha.substring(pos + 1);
		Funcao funcao = new Funcao(nomeFuncao);
		funcao.setBiblioNativa(biblioNativa);
		return funcao;
	}

	private void processarInstrucao(String nome, Funcao funcao, String linha) throws InstrucaoException {
		if (funcao == null) {
			throw new InstrucaoException("erro.instrucao_sem_funcao", nome, linha);
		}
		Instrucao instrucao = criarInstrucao(linha, nome);
		funcao.addInstrucao(instrucao);
	}

	private Instrucao criarInstrucao(String linha, String biblio) throws InstrucaoException {
		String espacos = InstrucaoConstantes.ESPACO + InstrucaoConstantes.ESPACO;
		int pos = linha.indexOf(espacos);
		int sequencia = Integer.parseInt(linha.substring(0, pos));
		String stringInstrucao = linha.substring(pos + espacos.length());
		return getInstrucao(sequencia, stringInstrucao, biblio);
	}

	private Instrucao getInstrucao(int sequencia, String string, String biblio) throws InstrucaoException {
		int pos = string.indexOf(' ');
		if (pos == -1) {
			Instrucao clone = Instrucoes.get(string, biblio).clonar();
			clone.sequencia = sequencia;
			return clone;
		} else {
			String nome = string.substring(0, pos);
			String parametros = string.substring(pos + 1);
			Instrucao clone = Instrucoes.get(nome, biblio).clonar();
			clone.setParametros(parametros);
			clone.sequencia = sequencia;
			return clone;
		}
	}

	@Override
	public String toString() {
		return "CacheBiblioteca size=" + bibliotecas.size() + "\n" + bibliotecas.toString();
	}
}