package br.com.persist.plugins.instrucao;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.assistencia.ArquivoUtil;

public class CacheBiblioteca {
	private final Map<String, Biblioteca> map;

	public CacheBiblioteca() {
		map = new HashMap<>();
	}

	public Biblioteca get(String nome) throws InstrucaoException {
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
			if (linha.startsWith("@@")) {
				metodo = new Metodo(linha.substring(2));
				resp.add(metodo);
			} else if (linha.startsWith("##")) {
				if (metodo == null) {
					throw new InstrucaoException("erro.parametro_sem_metodo", nome, linha.substring(2));
				}
				metodo.addParam(linha.substring(2));
			} else if (linha.startsWith("$$")) {
				if (metodo == null) {
					throw new InstrucaoException("erro.instrucao_sem_metodo", nome, linha.substring(2));
				}
				String string = linha.substring(2);
				addInstrucao(metodo, string);
			}
		}
		return resp;
	}

	private void addInstrucao(Metodo metodo, String string) {
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