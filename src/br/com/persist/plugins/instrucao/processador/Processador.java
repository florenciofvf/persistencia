package br.com.persist.plugins.instrucao.processador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.instrucoes.Add;
import br.com.persist.plugins.instrucao.instrucoes.And;
import br.com.persist.plugins.instrucao.instrucoes.DeclareVar;
import br.com.persist.plugins.instrucao.instrucoes.Diff;
import br.com.persist.plugins.instrucao.instrucoes.Div;
import br.com.persist.plugins.instrucao.instrucoes.Goto;
import br.com.persist.plugins.instrucao.instrucoes.Ifeq;
import br.com.persist.plugins.instrucao.instrucoes.Igual;
import br.com.persist.plugins.instrucao.instrucoes.Invoke;
import br.com.persist.plugins.instrucao.instrucoes.InvokeDin;
import br.com.persist.plugins.instrucao.instrucoes.LoadPar;
import br.com.persist.plugins.instrucao.instrucoes.LoadVar;
import br.com.persist.plugins.instrucao.instrucoes.Maior;
import br.com.persist.plugins.instrucao.instrucoes.MaiorI;
import br.com.persist.plugins.instrucao.instrucoes.Menor;
import br.com.persist.plugins.instrucao.instrucoes.MenorI;
import br.com.persist.plugins.instrucao.instrucoes.Mul;
import br.com.persist.plugins.instrucao.instrucoes.Neg;
import br.com.persist.plugins.instrucao.instrucoes.Or;
import br.com.persist.plugins.instrucao.instrucoes.PushBD;
import br.com.persist.plugins.instrucao.instrucoes.PushBI;
import br.com.persist.plugins.instrucao.instrucoes.PushSTR;
import br.com.persist.plugins.instrucao.instrucoes.Rem;
import br.com.persist.plugins.instrucao.instrucoes.Return;
import br.com.persist.plugins.instrucao.instrucoes.Sub;
import br.com.persist.plugins.instrucao.instrucoes.Xor;

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
