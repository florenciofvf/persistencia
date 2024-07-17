package br.com.persist.plugins.instrucao.processador;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.instrucao.instrucoes.DeclareVar;
import br.com.persist.plugins.instrucao.instrucoes.Goto;
import br.com.persist.plugins.instrucao.instrucoes.Ifeq;
import br.com.persist.plugins.instrucao.instrucoes.Invoke;
import br.com.persist.plugins.instrucao.instrucoes.InvokeDin;
import br.com.persist.plugins.instrucao.instrucoes.LoadPar;
import br.com.persist.plugins.instrucao.instrucoes.LoadVar;
import br.com.persist.plugins.instrucao.instrucoes.Neg;
import br.com.persist.plugins.instrucao.instrucoes.Return;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Add;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.And;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Diff;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Div;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Igual;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Maior;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.MaiorIgual;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Menor;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.MenorIgual;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Mul;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Or;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Rem;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Sub;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Xor;

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
		add(new MaiorIgual());
		add(new Menor());
		add(new MenorIgual());
		add(new Mul());
		add(new Neg());
		add(new Or());
		add(new PushBigDecimalInstrucao());
		add(new PushBigIntegerInstrucao());
		add(new PushStringInstrucao());
		add(new Rem());
		add(new Return());
		add(new Sub());
		add(new Xor());
	}
}