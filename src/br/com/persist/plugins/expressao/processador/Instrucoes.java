package br.com.persist.plugins.expressao.processador;

import java.util.HashMap;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.constante.ConstanteDefineInstrucao;
import br.com.persist.plugins.expressao.constante.ConstanteInvokeInstrucao;
import br.com.persist.plugins.expressao.constante.ConstanteLoadInstrucao;
import br.com.persist.plugins.expressao.funcao.FuncaoLoadInstrucao;
import br.com.persist.plugins.expressao.invocacao.InvocacaoInstrucao;
import br.com.persist.plugins.expressao.invocacao.InvocacaoParamInstrucao;
import br.com.persist.plugins.expressao.lista.AddItemListaInstrucao;
import br.com.persist.plugins.expressao.mapa.PutItemMapaInstrucao;
import br.com.persist.plugins.expressao.nativo.FlutuantePushInstrucao;
import br.com.persist.plugins.expressao.nativo.InteiroPushInstrucao;
import br.com.persist.plugins.expressao.nativo.StringPushInstrucao;
import br.com.persist.plugins.expressao.negativo.NegativoInstrucao;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Add;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.AddLista;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.And;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.ConcatLista;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Diff;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Div;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Igual;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Maior;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.MaiorIgual;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Menor;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.MenorIgual;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Mul;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Or;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Rem;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Sub;
import br.com.persist.plugins.expressao.operador.OperadorInstrucao.Xor;
import br.com.persist.plugins.expressao.parametros.ParametroLoadInstrucao;
import br.com.persist.plugins.expressao.retorno.RetornoInstrucao;
import br.com.persist.plugins.expressao.salto.GotoInstrucao;
import br.com.persist.plugins.expressao.salto.IFEqInstrucao;

public class Instrucoes {
	static final Map<String, Instrucao> cache = new HashMap<>();

	private Instrucoes() {
	}

	private static void add(Instrucao instrucao) {
		if (instrucao != null) {
			cache.put(instrucao.getNome(), instrucao);
		}
	}

	public static Instrucao get(String nome, Biblioteca biblioteca) throws ExpressaoException {
		Instrucao obj = cache.get(nome);
		if (obj == null) {
			throw new ExpressaoException("erro.instrucao_invalida", nome, biblioteca.getNomeAbsoluto());
		}
		return obj;
	}

	static {
		add(new InvocacaoParamInstrucao(false));
		add(new InvocacaoParamInstrucao(true));
		add(new ConstanteDefineInstrucao());
		add(new FuncaoLoadInstrucao(false));
		add(new ConstanteInvokeInstrucao());
		add(new FuncaoLoadInstrucao(true));
		add(new InvocacaoInstrucao(false));
		add(new InvocacaoInstrucao(true));
		add(new ParametroLoadInstrucao());
		add(new ConstanteLoadInstrucao());
		add(new FlutuantePushInstrucao());
		add(new AddItemListaInstrucao());
		add(new PutItemMapaInstrucao());
		add(new InteiroPushInstrucao());
		add(new StringPushInstrucao());
		add(new NegativoInstrucao());
		add(new RetornoInstrucao());
		add(new GotoInstrucao());
		add(new IFEqInstrucao());
		add(new ConcatLista());
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