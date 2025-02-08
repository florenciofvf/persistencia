package br.com.persist.plugins.instrucao.processador;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.Add;
import br.com.persist.plugins.instrucao.processador.OperadorInstrucao.AddLista;
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

	public static Instrucao get(String nome, String biblio) throws InstrucaoException {
		Instrucao obj = cache.get(nome);
		if (obj == null) {
			throw new InstrucaoException("erro.instrucao_invalida", nome, biblio);
		}
		return obj;
	}

	static {
		add(new LoadParametroSuperInstrucao());
		add(new InvocacaoParamExpInstrucao());
		add(new PushBigDecimalInstrucao());
		add(new PushBigIntegerInstrucao());
		add(new InvocacaoParamInstrucao());
		add(new LoadFuncaoLambInstrucao());
		add(new LoadParametroInstrucao());
		add(new LoadConstanteInstrucao());
		add(new InvocacaoExpInstrucao());
		add(new DefineConstInstrucao());
		add(new LoadFuncaoInstrucao());
		add(new PushStringInstrucao());
		add(new InvocacaoInstrucao());
		add(new NegativoInstrucao());
		add(new RetornoInstrucao());
		add(new GotoInstrucao());
		add(new IFEqInstrucao());
		add(new MaiorIgual());
		add(new MenorIgual());
		add(new AddLista());
		add(new Igual());
		add(new Menor());
		add(new Maior());
		add(new Diff());
		add(new Add());
		add(new And());
		add(new Div());
		add(new Mul());
		add(new Rem());
		add(new Sub());
		add(new Xor());
		add(new Or());
	}
}