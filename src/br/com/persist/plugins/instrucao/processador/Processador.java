package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaFuncao pilhaFuncao = new PilhaFuncao();

	public List<Object> processar(String nomeBiblioteca, String nomeFuncao, Object... args) throws InstrucaoException {
		Biblioteca biblioteca = cacheBiblioteca.getBiblioteca(nomeBiblioteca);
		Funcao funcao = biblioteca.getFuncao(nomeFuncao);
		for (Object obj : args) {
			pilhaOperando.push(obj);
		}
		pilhaFuncao.push(funcao);

		funcao = pilhaFuncao.isEmpty() ? null : pilhaFuncao.peek();
		while (funcao != null) {
			Instrucao instrucao = funcao.getInstrucao();
			instrucao.processar(cacheBiblioteca, funcao.getBiblioteca(), funcao, pilhaFuncao, pilhaOperando);
			funcao = pilhaFuncao.isEmpty() ? null : pilhaFuncao.peek();
		}

		List<Object> resposta = new ArrayList<>();
		while (!pilhaOperando.isEmpty()) {
			resposta.add(pilhaOperando.pop());
		}

		return resposta;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(cacheBiblioteca.toString() + "\n");
		sb.append(pilhaFuncao.toString() + "\n");
		sb.append(pilhaOperando.toString());
		return sb.toString();
	}
}