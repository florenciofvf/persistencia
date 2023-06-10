package br.com.persist.plugins.instrucao.pro;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class CacheBiblioteca {
	private final Map<String, Biblioteca> map;

	public CacheBiblioteca() {
		map = new HashMap<>();
	}

	public Biblioteca getBiblioteca(String nome) throws InstrucaoException {
		Biblioteca resp = map.get(nome);
		if (resp == null) {
			resp = lerBiblioteca(nome);
			if (resp == null) {
				throw new InstrucaoException("erro.biblio_inexistente", nome);
			}
			map.put(nome, resp);
		}
		return resp;
	}

	private Biblioteca lerBiblioteca(String nome) throws InstrucaoException {
		Biblioteca resp = null;
		List<String> arquivo = ArquivoUtil.lerArquivo(new File(nome));
		if (arquivo.isEmpty()) {
			return resp;
		}
		resp = new Biblioteca(nome);
		Metodo metodo = null;
		for (String linha : arquivo) {
			if (linha.startsWith(InstrucaoConstantes.PREFIXO_METODO_NATIVO)) {
				metodo = new Metodo(resp, linha.substring(3), true);
				resp.add(metodo);
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_METODO)) {
				metodo = new Metodo(resp, linha.substring(2), false);
				resp.add(metodo);
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_PARAM)) {
				if (metodo == null) {
					throw new InstrucaoException("erro.parametro_sem_metodo", nome, linha.substring(2));
				}
				metodo.addParam(linha.substring(2));
			} else if (linha.startsWith(InstrucaoConstantes.PREFIXO_INSTRUCAO)) {
				if (metodo == null) {
					throw new InstrucaoException("erro.instrucao_sem_metodo", nome, linha.substring(2));
				}
				int pos = linha.indexOf('-');
				String stringInstrucao = linha.substring(pos + 2);
				addInstrucao(metodo, stringInstrucao);
			}
		}
		return resp;
	}

	private void addInstrucao(Metodo metodo, String string) throws InstrucaoException {
		int pos = string.indexOf(' ');
		if (pos == -1) {
			Instrucao instrucao = Instrucoes.get(string);
			metodo.addInstrucao(instrucao);
		} else {
			Instrucao instrucao = Instrucoes.get(string.substring(0, pos));
			instrucao.setParam(string.substring(pos + 1));
			metodo.addInstrucao(instrucao);
		}
	}
}