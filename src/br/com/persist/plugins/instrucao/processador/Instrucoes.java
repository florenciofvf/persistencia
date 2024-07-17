package br.com.persist.plugins.instrucao.processador;

import java.util.HashMap;
import java.util.Map;

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
		add(new Diff());
		add(new Div());
		add(new GotoInstrucao());
		add(new IfEqInstrucao());
		add(new Igual());
		add(new InvocacaoInstrucao());
		add(new LoadParametroInstrucao());
		add(new LoadConstanteInstrucao());
		add(new Maior());
		add(new MaiorIgual());
		add(new Menor());
		add(new MenorIgual());
		add(new Mul());
		add(new NegativoInstrucao());
		add(new Or());
		add(new PushBigDecimalInstrucao());
		add(new PushBigIntegerInstrucao());
		add(new PushStringInstrucao());
		add(new Rem());
		add(new RetornoInstrucao());
		add(new Sub());
		add(new Xor());
	}
}