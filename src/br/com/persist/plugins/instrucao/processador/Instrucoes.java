package br.com.persist.plugins.instrucao.processador;

import java.util.HashMap;
import java.util.Map;

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

public class Instrucoes {
	static final Map<String, Instrucao> cache = new HashMap<>();

	private Instrucoes() {
	}

	private static void add(Instrucao instrucao) {
		if (instrucao != null) {
			cache.put(instrucao.getNome(), instrucao);
		}
	}

	public static Instrucao get(String nome) {
		return cache.get(nome);
	}

	static {
		add(new Add());
		add(new And());
		add(new DeclareVar());
		add(new Diff());
		add(new Div());
		add(new Goto());
		add(new Ifeq());
		add(new Igual());
		add(new Invoke());
		add(new InvokeDin());
		add(new LoadPar());
		add(new LoadVar());
		add(new Maior());
		add(new MaiorI());
		add(new Menor());
		add(new MenorI());
		add(new Mul());
		add(new Neg());
		add(new Or());
		add(new PushBD());
		add(new PushBI());
		add(new PushSTR());
		add(new Rem());
		add(new Return());
		add(new Sub());
		add(new Xor());
	}
}