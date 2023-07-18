package br.com.persist.plugins.instrucao.pro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.inst.Add;
import br.com.persist.plugins.instrucao.inst.AddLista;
import br.com.persist.plugins.instrucao.inst.And;
import br.com.persist.plugins.instrucao.inst.DeclareVar;
import br.com.persist.plugins.instrucao.inst.Diff;
import br.com.persist.plugins.instrucao.inst.Div;
import br.com.persist.plugins.instrucao.inst.Goto;
import br.com.persist.plugins.instrucao.inst.Ifeq;
import br.com.persist.plugins.instrucao.inst.Igual;
import br.com.persist.plugins.instrucao.inst.Invoke;
import br.com.persist.plugins.instrucao.inst.InvokeDin;
import br.com.persist.plugins.instrucao.inst.LoadHeadLista;
import br.com.persist.plugins.instrucao.inst.LoadListaVazia;
import br.com.persist.plugins.instrucao.inst.LoadPar;
import br.com.persist.plugins.instrucao.inst.LoadTailLista;
import br.com.persist.plugins.instrucao.inst.LoadVar;
import br.com.persist.plugins.instrucao.inst.Maior;
import br.com.persist.plugins.instrucao.inst.MaiorI;
import br.com.persist.plugins.instrucao.inst.Menor;
import br.com.persist.plugins.instrucao.inst.MenorI;
import br.com.persist.plugins.instrucao.inst.ModificVar;
import br.com.persist.plugins.instrucao.inst.Mul;
import br.com.persist.plugins.instrucao.inst.Neg;
import br.com.persist.plugins.instrucao.inst.Or;
import br.com.persist.plugins.instrucao.inst.Pow;
import br.com.persist.plugins.instrucao.inst.PushBD;
import br.com.persist.plugins.instrucao.inst.PushBI;
import br.com.persist.plugins.instrucao.inst.PushSTR;
import br.com.persist.plugins.instrucao.inst.Rem;
import br.com.persist.plugins.instrucao.inst.Return;
import br.com.persist.plugins.instrucao.inst.Sub;
import br.com.persist.plugins.instrucao.inst.TailCall;
import br.com.persist.plugins.instrucao.inst.Xor;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaMetodo pilhaMetodo = new PilhaMetodo();

	public List<Object> executar(String nomeBiblioteca, String nomeMetodo, Object... args) throws InstrucaoException {
		Invoke invoke = new Invoke(null);
		invoke.setParam(nomeBiblioteca + "." + nomeMetodo);
		for (Object obj : args) {
			pilhaOperando.push(obj);
		}
		invoke.executar(pilhaMetodo, pilhaOperando, cacheBiblioteca);
		Metodo metodo = pilhaMetodo.isEmpty() ? null : pilhaMetodo.peek();
		while (metodo != null) {
			Instrucao instrucao = metodo.getInstrucao();
			instrucao.executar(pilhaMetodo, pilhaOperando, cacheBiblioteca);
			metodo = pilhaMetodo.isEmpty() ? null : pilhaMetodo.peek();
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

	public void excluirBiblioteca(String nome) {
		cacheBiblioteca.excluir(nome);
	}

	public void clear() {
		cacheBiblioteca.clear();
		pilhaOperando.clear();
		pilhaMetodo.clear();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(cacheBiblioteca.toString() + "\n");
		sb.append(pilhaMetodo.toString() + "\n");
		sb.append(pilhaOperando.toString());
		return sb.toString();
	}

	static {
		Instrucoes.add(new Add(null));
		Instrucoes.add(new AddLista(null));
		Instrucoes.add(new And(null));
		Instrucoes.add(new DeclareVar(null));
		Instrucoes.add(new Diff(null));
		Instrucoes.add(new Div(null));
		Instrucoes.add(new Goto(null));
		Instrucoes.add(new Ifeq(null));
		Instrucoes.add(new Igual(null));
		Instrucoes.add(new Invoke(null));
		Instrucoes.add(new InvokeDin(null));
		Instrucoes.add(new LoadHeadLista(null));
		Instrucoes.add(new LoadListaVazia(null));
		Instrucoes.add(new LoadPar(null));
		Instrucoes.add(new LoadTailLista(null));
		Instrucoes.add(new LoadVar(null));
		Instrucoes.add(new Maior(null));
		Instrucoes.add(new MaiorI(null));
		Instrucoes.add(new Menor(null));
		Instrucoes.add(new MenorI(null));
		Instrucoes.add(new ModificVar(null));
		Instrucoes.add(new Mul(null));
		Instrucoes.add(new Neg(null));
		Instrucoes.add(new Or(null));
		Instrucoes.add(new Pow(null));
		Instrucoes.add(new PushBD(null));
		Instrucoes.add(new PushBI(null));
		Instrucoes.add(new PushSTR(null));
		Instrucoes.add(new Rem(null));
		Instrucoes.add(new Return(null));
		Instrucoes.add(new Sub(null));
		Instrucoes.add(new TailCall(null));
		Instrucoes.add(new Xor(null));
	}
}

class Instrucoes {
	static final Map<String, Instrucao> cache = new HashMap<>();

	private Instrucoes() {
	}

	static void add(Instrucao instrucao) {
		if (instrucao != null) {
			cache.put(instrucao.getNome(), instrucao);
		}
	}

	static Instrucao get(String nome) {
		return cache.get(nome);
	}
}