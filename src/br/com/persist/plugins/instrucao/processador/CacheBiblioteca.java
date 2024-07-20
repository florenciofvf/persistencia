package br.com.persist.plugins.instrucao.processador;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class CacheBiblioteca {
	public static final File ROOT = new File(InstrucaoConstantes.INSTRUCAO);
	public static final File COMPILADOS = new File(ROOT, "compilados");
	private final Map<String, Biblioteca> bibliotecas;

	public CacheBiblioteca() {
		bibliotecas = new HashMap<>();
	}

	public Biblioteca getBiblioteca(String nome) throws InstrucaoException {
		Biblioteca resp = bibliotecas.get(nome);
		if (resp == null) {
			resp = lerBiblioteca(nome);
			if (resp == null) {
				throw new InstrucaoException("erro.biblio_inexistente", nome);
			}
			bibliotecas.put(nome, resp);
		}
		return resp;
	}

	private Biblioteca lerBiblioteca(String nome) throws InstrucaoException {
		Biblioteca biblioteca = null;
		List<String> arquivo = ArquivoUtil.lerArquivo(new File(COMPILADOS, nome + Biblioteca.EXTENSAO));
		if (arquivo.isEmpty()) {
			return biblioteca;
		}
		biblioteca = new Biblioteca(nome);
		Iterator<String> it = arquivo.iterator();
		Constante constante = null;
		Funcao funcao = null;
		while (it.hasNext()) {
			String linha = it.next();
			if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO)) {
				funcao = criarFuncao(biblioteca, linha);
				biblioteca.addFuncao(funcao);
				constante = null;
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA)) {
				funcao = criarFuncaoNativa(biblioteca, linha);
				biblioteca.addFuncao(funcao);
				constante = null;
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_CONSTANTE)) {
				constante = criarConstante(biblioteca, linha);
				biblioteca.addConstante(constante);
				funcao = null;
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_PARAMETRO)) {
				String nomeParametro = linha.substring(InstrucaoConstantes.PREFIXO_PARAMETRO.length());
				if (funcao == null) {
					throw new InstrucaoException("erro.parametro_sem_metodo", nome, nomeParametro);
				}
				funcao.addParametro(nomeParametro);
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_INSTRUCAO)) {
				processarInstrucao(nome, constante, funcao, linha);
			}
		}
		return biblioteca;
	}

	private Funcao criarFuncao(Biblioteca biblioteca, String linha) {
		linha = linha.substring(InstrucaoConstantes.PREFIXO_FUNCAO.length());
		Funcao funcao = new Funcao(linha);
		funcao.setBiblioteca(biblioteca);
		return funcao;
	}

	private Funcao criarFuncaoNativa(Biblioteca biblioteca, String linha) {
		linha = linha.substring(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA.length());
		int pos = linha.indexOf(' ');
		String biblioNativa = linha.substring(0, pos);
		String nomeFuncao = linha.substring(pos + 1);
		Funcao funcao = new Funcao(nomeFuncao);
		funcao.setBiblioNativa(biblioNativa);
		funcao.setBiblioteca(biblioteca);
		return funcao;
	}

	private Constante criarConstante(Biblioteca biblioteca, String linha) {
		linha = linha.substring(InstrucaoConstantes.PREFIXO_CONSTANTE.length());
		Constante constante = new Constante(linha);
		constante.setBiblioteca(biblioteca);
		return constante;
	}

	private void processarInstrucao(String nome, Constante constante, Funcao funcao, String linha)
			throws InstrucaoException {
		String linhaInstrucao = linha.substring(InstrucaoConstantes.PREFIXO_INSTRUCAO.length());
		if (funcao == null && constante == null) {
			throw new InstrucaoException("erro.instrucao_sem_metodo", nome, linhaInstrucao);
		}
		Instrucao instrucao = criarInstrucao(linhaInstrucao);
		if (funcao != null) {
			funcao.addInstrucao(instrucao);
		} else {
			constante.addInstrucao(instrucao);
		}
	}

	private Instrucao criarInstrucao(String linha) {
		int pos = linha.indexOf('-');
		String stringInstrucao = linha.substring(pos + 2);
		return getInstrucao(stringInstrucao);
	}

	private Instrucao getInstrucao(String string) {
		int pos = string.indexOf(' ');
		if (pos == -1) {
			return Instrucoes.get(string);
		} else {
			String nome = string.substring(0, pos);
			String parametros = string.substring(pos + 1);
			Instrucao instrucao = Instrucoes.get(nome);
			Instrucao clone = instrucao.clonar();
			clone.setParametros(parametros);
			return clone;
		}
	}

	@Override
	public String toString() {
		return "CacheBiblioteca size=" + bibliotecas.size() + "\n" + bibliotecas.toString();
	}
}