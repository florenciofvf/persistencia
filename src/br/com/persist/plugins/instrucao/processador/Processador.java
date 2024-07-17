package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaFuncao pilhaFuncao = new PilhaFuncao();

	public List<Object> executar(String nomeBiblioteca, String nomeMetodo, Object... args) throws InstrucaoException {
		Invocacao invocacao = new Invocacao();
		invocacao.setParametros(nomeBiblioteca + "." + nomeMetodo);
		for (Object obj : args) {
			pilhaOperando.push(obj);
		}
		invocacao.processar(cacheBiblioteca, pilhaFuncao, pilhaOperando);

		Funcao funcao = pilhaFuncao.isEmpty() ? null : pilhaFuncao.peek();
		while (funcao != null) {
			Instrucao instrucao = funcao.getInstrucao();
			instrucao.processar(cacheBiblioteca, pilhaFuncao, pilhaOperando);
			funcao = pilhaFuncao.isEmpty() ? null : pilhaFuncao.peek();
		}

		List<Object> resposta = new ArrayList<>();
		while (!pilhaOperando.isEmpty()) {
			resposta.add(pilhaOperando.pop());
		}

		return resposta;
	}

	public Biblioteca getBiblioteca(String nome) throws InstrucaoException {
		return cacheBiblioteca.getBiblioteca(nome);
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
