package br.com.persist.plugins.instrucao.processador;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;

public class CacheBiblioteca {
	private static final String ERRO_BIBLIO_INEXISTENTE = "erro.biblio_inexistente";
	public static final File ROOT = new File(InstrucaoConstantes.INSTRUCAO);
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
		AtomicReference<Constante> atomicConstante = new AtomicReference<>();
		AtomicReference<Funcao> atomicFuncao = new AtomicReference<>();
		while (it.hasNext()) {
			String linha = it.next();
			processar(nome, biblioteca, atomicConstante, atomicFuncao, linha);
		}
		biblioteca.initConstantes();
		return biblioteca;
	}

	private static String get(String nome) {
		if (nome.indexOf('.') != -1) {
			return Util.replaceAll(nome, ".", Constantes.SEPARADOR);
		}
		return nome;
	}

	public static File getArquivo(BibliotecaContexto biblioteca) throws InstrucaoException {
		if (biblioteca == null) {
			throw new InstrucaoException("Biblioteca nula.", false);
		}
		String nome = biblioteca.getNome();
		int pos = nome.lastIndexOf('.');
		if (pos != -1) {
			String name = nome.substring(pos + 1);
			nome = nome.substring(0, pos);
			String pack = Util.replaceAll(nome, ".", Constantes.SEPARADOR);
			File path = new File(COMPILADOS, pack);
			if (!path.isDirectory() && !path.mkdirs()) {
				throw new InstrucaoException("erro.criar_diretorios", path.getPath());
			}
			return new File(path, name + Biblioteca.EXTENSAO);
		}
		return new File(COMPILADOS, nome + Biblioteca.EXTENSAO);
	}

	private void processar(String nome, Biblioteca biblioteca, AtomicReference<Constante> atomicConstante,
			AtomicReference<Funcao> atomicFuncao, String linha) throws InstrucaoException {
		if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO)) {
			Funcao funcao = criarFuncao(linha);
			biblioteca.addFuncao(funcao);
			atomicFuncao.set(funcao);
			atomicConstante.set(null);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA)) {
			Funcao funcao = criarFuncaoNativa(linha);
			biblioteca.addFuncao(funcao);
			atomicFuncao.set(funcao);
			atomicConstante.set(null);
		} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_CONSTANTE)) {
			Constante constante = criarConstante(linha);
			biblioteca.addConstante(constante);
			atomicFuncao.set(null);
			atomicConstante.set(constante);
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
			processarInstrucao(nome, atomicConstante.get(), atomicFuncao.get(), linha);
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

	private Constante criarConstante(String linha) {
		linha = linha.substring(InstrucaoConstantes.PREFIXO_CONSTANTE.length());
		return new Constante(linha);
	}

	private void processarInstrucao(String nome, Constante constante, Funcao funcao, String linha)
			throws InstrucaoException {
		if (funcao == null && constante == null) {
			throw new InstrucaoException("erro.instrucao_sem_funcao", nome, linha);
		}
		Instrucao instrucao = criarInstrucao(linha, nome);
		if (funcao != null) {
			funcao.addInstrucao(instrucao);
		} else {
			constante.addInstrucao(instrucao);
		}
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