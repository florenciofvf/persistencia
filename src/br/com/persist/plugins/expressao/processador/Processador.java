package br.com.persist.plugins.expressao.processador;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.compl.funcao.FuncaoConstantesContexto;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaFuncao pilhaFuncao = new PilhaFuncao();

	public List<Object> processar(String nomeAbsoluto, String nomeFuncao, Object... args) throws InstrucaoException {
		Biblioteca biblioteca = cacheBiblioteca.getBiblioteca(nomeAbsoluto);

		Funcao funcao = biblioteca.getFuncao(nomeFuncao).clonar();

		for (int i = 0; i < args.length; i++) {
			funcao.setValorParametro(i, args[i]);
		}

		Funcao funcaoConstantes = biblioteca.getFuncao(FuncaoConstantesContexto.NOME_FUNCAO_CONSTANTES).clonar();

		pilhaFuncao.clear();
		pilhaOperando.clear();
		pilhaFuncao.push(funcaoConstantes);
		pilhaFuncao.push(funcao);

		funcao = pilhaFuncao.peek();

		while (funcao != null) {
			boolean processar = false;
			Instrucao instrucao = funcao.proximaInstrucao();
			if (instrucao instanceof ReferenciaBiblioteca) {
				ReferenciaBiblioteca referencia = (ReferenciaBiblioteca) instrucao;
				if (!cacheBiblioteca.contem(referencia.getNomeAbsoluto())) {
					Biblioteca novaBiblioteca = cacheBiblioteca.getBiblioteca(referencia.getNomeAbsoluto());
					funcaoConstantes = novaBiblioteca.getFuncao(FuncaoConstantesContexto.NOME_FUNCAO_CONSTANTES)
							.clonar();
					funcao.setIndice(referencia.getIndice());
					pilhaFuncao.push(funcaoConstantes);
					funcao = pilhaFuncao.peek();
				} else {
					processar = true;
				}
			} else {
				processar = true;
			}
			if (processar) {
				instrucao.processar(cacheBiblioteca, funcao.getBiblioteca(), funcao, pilhaFuncao, pilhaOperando);
				funcao = pilhaFuncao.isEmpty() ? null : pilhaFuncao.peek();
			}
		}

		List<Object> resposta = new ArrayList<>();

		while (!pilhaOperando.isEmpty()) {
			resposta.add(pilhaOperando.pop());
		}

		return resposta;
	}

	public CacheBiblioteca getCacheBiblioteca() {
		return cacheBiblioteca;
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