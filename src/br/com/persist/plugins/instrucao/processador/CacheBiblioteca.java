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
				funcao = criarFuncao(linha);
				biblioteca.addFuncao(funcao);
				constante = null;
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_FUNCAO_NATIVA)) {
				funcao = criarFuncaoNativa(linha);
				biblioteca.addFuncao(funcao);
				constante = null;
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_CONSTANTE)) {
				constante = criarConstante(linha);
				biblioteca.addConstante(constante);
				funcao = null;
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_PARAMETRO)) {
				String nomeParametro = linha.substring(InstrucaoConstantes.PREFIXO_PARAMETRO.length());
				if (funcao == null) {
					throw new InstrucaoException("erro.parametro_sem_funcao", nome, nomeParametro);
				}
				funcao.addParametro(nomeParametro);
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_TIPO_VOID)) {
				if (funcao == null) {
					throw new InstrucaoException("erro.tipo_sem_funcao", nome);
				}
				funcao.setTipoVoid(true);
			} else if (linhaInstrucao(linha)) {
				processarInstrucao(nome, constante, funcao, linha);
			}
		}
		biblioteca.initConstantes();
		return biblioteca;
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
		Instrucao instrucao = criarInstrucao(linha);
		if (funcao != null) {
			funcao.addInstrucao(instrucao);
		} else {
			constante.addInstrucao(instrucao);
		}
	}

	private Instrucao criarInstrucao(String linha) {
		String espacos = InstrucaoConstantes.ESPACO + InstrucaoConstantes.ESPACO;
		int pos = linha.indexOf(espacos);
		int sequencia = Integer.parseInt(linha.substring(0, pos));
		String stringInstrucao = linha.substring(pos + espacos.length());
		return getInstrucao(sequencia, stringInstrucao);
	}

	private Instrucao getInstrucao(int sequencia, String string) {
		int pos = string.indexOf(' ');
		if (pos == -1) {
			Instrucao clone = Instrucoes.get(string).clonar();
			clone.sequencia = sequencia;
			return clone;
		} else {
			String nome = string.substring(0, pos);
			String parametros = string.substring(pos + 1);
			Instrucao clone = Instrucoes.get(nome).clonar();
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