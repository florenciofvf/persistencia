package br.com.persist.plugins.expressao.processador;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.expressao.nativo.FlutuantePushInstrucao;
import br.com.persist.plugins.expressao.nativo.InteiroPushInstrucao;
import br.com.persist.plugins.expressao.nativo.StringPushInstrucao;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Add;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.AddLista;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.And;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Diff;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Div;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Igual;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Maior;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.MaiorIgual;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Menor;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.MenorIgual;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Mul;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Or;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Rem;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Sub;
import br.com.persist.plugins.expressao.processador.OperadorInstrucao.Xor;
import br.com.persist.plugins.expressao.retorno.RetornoInstrucao;
import br.com.persist.plugins.expressao.salto.GotoInstrucao;
import br.com.persist.plugins.expressao.salto.IFEqInstrucao;
import br.com.persist.plugins.instrucao.InstrucaoException;

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
		add(new FlutuantePushInstrucao());
		add(new InteiroPushInstrucao());
		add(new InvocacaoParamInstrucao());
		add(new LoadFuncaoLambInstrucao());
		add(new LoadParametroInstrucao());
		add(new LoadConstanteInstrucao());
		add(new InvocacaoExpInstrucao());
		add(new DefineConstInstrucao());
		add(new LoadFuncaoInstrucao());
		add(new StringPushInstrucao());
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