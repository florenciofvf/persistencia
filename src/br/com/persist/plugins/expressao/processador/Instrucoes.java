package br.com.persist.plugins.expressao.processador;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.constante.ConstanteDefineInstrucao;
import br.com.persist.plugins.expressao.constante.ConstanteInvokeInstrucao;
import br.com.persist.plugins.expressao.constante.ConstanteLoadInstrucao;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoLoadInnerInstrucao;
import br.com.persist.plugins.expressao.funcao.FuncaoLoadInstrucao;
import br.com.persist.plugins.expressao.invocacao.InvocacaoContexto;
import br.com.persist.plugins.expressao.invocacao.InvokeInstrucao;
import br.com.persist.plugins.expressao.lista.AddItemListaContexto;
import br.com.persist.plugins.expressao.lista.AddItemListaInstrucao;
import br.com.persist.plugins.expressao.mapa.PutItemMapaContexto;
import br.com.persist.plugins.expressao.mapa.PutItemMapaInstrucao;
import br.com.persist.plugins.expressao.nativo.FlutuanteContexto;
import br.com.persist.plugins.expressao.nativo.FlutuantePushInstrucao;
import br.com.persist.plugins.expressao.nativo.InteiroContexto;
import br.com.persist.plugins.expressao.nativo.InteiroPushInstrucao;
import br.com.persist.plugins.expressao.nativo.StringContexto;
import br.com.persist.plugins.expressao.nativo.StringPushInstrucao;
import br.com.persist.plugins.expressao.negativo.NegativoContexto;
import br.com.persist.plugins.expressao.negativo.NegativoInstrucao;
import br.com.persist.plugins.expressao.operador.OperadorContexto;
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
import br.com.persist.plugins.expressao.parametros.ParametroInvokeInstrucao;
import br.com.persist.plugins.expressao.parametros.ParametroContexto;
import br.com.persist.plugins.expressao.parametros.ParametroLoadInstrucao;
import br.com.persist.plugins.expressao.retorno.RetornoContexto;
import br.com.persist.plugins.expressao.retorno.RetornoInstrucao;
import br.com.persist.plugins.expressao.salto.GotoContexto;
import br.com.persist.plugins.expressao.salto.GotoInstrucao;
import br.com.persist.plugins.expressao.salto.IFEqContexto;
import br.com.persist.plugins.expressao.salto.IFEqInstrucao;

public class Instrucoes {
	private Instrucoes() {
	}

	public static Instrucao criar(Biblioteca biblioteca, int indice, String nome, String parametros)
			throws ExpressaoException {
		if (NegativoContexto.NEG.equals(nome)) {
			return new NegativoInstrucao(indice);
		}

		if (RetornoContexto.RETURN.equals(nome)) {
			return new RetornoInstrucao(indice);
		}

		if (ParametroContexto.LOAD_PARAM.equals(nome)) {
			return new ParametroLoadInstrucao(indice, parametros);
		}

		Instrucao resp = criarConstante(indice, nome, parametros);
		if (resp != null) {
			return resp;
		}

		resp = criarInvocacao(indice, nome, parametros);
		if (resp != null) {
			return resp;
		}

		resp = criarNativo(indice, nome, parametros);
		if (resp != null) {
			return resp;
		}

		resp = criarFuncao(indice, nome, parametros);
		if (resp != null) {
			return resp;
		}

		resp = criarSalto(indice, nome, parametros);
		if (resp != null) {
			return resp;
		}

		resp = criarOperador(indice, nome);
		if (resp != null) {
			return resp;
		}

		throw new ExpressaoException("erro.instrucao_invalida", nome, biblioteca.getNomeAbsoluto());
	}

	private static Instrucao criarConstante(int indice, String nome, String parametros) throws ExpressaoException {
		if (ConstanteContexto.DEF_CONST.equals(nome)) {
			return new ConstanteDefineInstrucao(indice, parametros);
		} else if (ConstanteContexto.LOAD_CONST.equals(nome)) {
			return new ConstanteLoadInstrucao(indice, parametros);
		} else if (ConstanteContexto.INVOKE_CONST.equals(nome)) {
			return new ConstanteInvokeInstrucao(indice, parametros);
		}
		return null;
	}

	private static Instrucao criarInvocacao(int indice, String nome, String parametros) throws ExpressaoException {
		if (InvocacaoContexto.INVOKE_CRET.equals(nome)) {
			return new InvokeInstrucao(true, indice, parametros);
		} else if (InvocacaoContexto.INVOKE_VOID.equals(nome)) {
			return new InvokeInstrucao(false, indice, parametros);
		} else if (InvocacaoContexto.INVOKE_PARAM_CRET.equals(nome)) {
			return new ParametroInvokeInstrucao(true, indice, parametros);
		} else if (InvocacaoContexto.INVOKE_PARAM_VOID.equals(nome)) {
			return new ParametroInvokeInstrucao(false, indice, parametros);
		}
		return null;
	}

	private static Instrucao criarFuncao(int indice, String nome, String parametros) throws ExpressaoException {
		if (FuncaoContexto.LOAD_FUNCTION_VOID.equals(nome)) {
			return new FuncaoLoadInstrucao(true, indice, parametros);
		} else if (FuncaoContexto.LOAD_FUNCTION_CRET.equals(nome)) {
			return new FuncaoLoadInstrucao(false, indice, parametros);
		} else if (FuncaoContexto.LOAD_FUNCTION_INNER_VOID.equals(nome)) {
			return new FuncaoLoadInnerInstrucao(true, indice, parametros);
		} else if (FuncaoContexto.LOAD_FUNCTION_INNER_CRET.equals(nome)) {
			return new FuncaoLoadInnerInstrucao(false, indice, parametros);
		}
		return null;
	}

	private static Instrucao criarNativo(int indice, String nome, String parametros) throws ExpressaoException {
		if (StringContexto.PUSH_STRING.equals(nome)) {
			return new StringPushInstrucao(indice, parametros);
		} else if (InteiroContexto.PUSH_INTEIRO.equals(nome)) {
			return new InteiroPushInstrucao(indice, parametros);
		} else if (FlutuanteContexto.PUSH_FLUTUANTE.equals(nome)) {
			return new FlutuantePushInstrucao(indice, parametros);
		} else if (PutItemMapaContexto.PUT_ITEM_MAPA.equals(nome)) {
			return new PutItemMapaInstrucao(indice, parametros);
		} else if (AddItemListaContexto.ADD_ITEM_LISTA.equals(nome)) {
			return new AddItemListaInstrucao(indice);
		}
		return null;
	}

	private static Instrucao criarSalto(int indice, String nome, String parametros) throws ExpressaoException {
		if (GotoContexto.GOTO.equals(nome)) {
			return new GotoInstrucao(indice, parametros);
		} else if (IFEqContexto.IF_EQ.equals(nome)) {
			return new IFEqInstrucao(indice, parametros);
		}
		return null;
	}

	private static Instrucao criarOperador(int indice, String nome) throws ExpressaoException {
		Instrucao resp = criarMatematicos(indice, nome);
		if (resp != null) {
			return resp;
		}

		resp = criarComparacao(indice, nome);
		if (resp != null) {
			return resp;
		}

		resp = criarLogicos(indice, nome);
		if (resp != null) {
			return resp;
		}

		return criarOutros(indice, nome);
	}

	private static Instrucao criarMatematicos(int indice, String nome) throws ExpressaoException {
		if (OperadorContexto.ADD.equals(nome)) {
			return new Add(indice);
		} else if (OperadorContexto.SUB.equals(nome)) {
			return new Sub(indice);
		} else if (OperadorContexto.MUL.equals(nome)) {
			return new Mul(indice);
		} else if (OperadorContexto.DIV.equals(nome)) {
			return new Div(indice);
		} else if (OperadorContexto.REM.equals(nome)) {
			return new Rem(indice);
		}
		return null;
	}

	private static Instrucao criarComparacao(int indice, String nome) throws ExpressaoException {
		if (OperadorContexto.IGUAL.equals(nome)) {
			return new Igual(indice);
		} else if (OperadorContexto.DIFF.equals(nome)) {
			return new Diff(indice);
		} else if (OperadorContexto.MENOR.equals(nome)) {
			return new Menor(indice);
		} else if (OperadorContexto.MAIOR.equals(nome)) {
			return new Maior(indice);
		} else if (OperadorContexto.MENOR_IGUAL.equals(nome)) {
			return new MenorIgual(indice);
		} else if (OperadorContexto.MAIOR_IGUAL.equals(nome)) {
			return new MaiorIgual(indice);
		}
		return null;
	}

	private static Instrucao criarLogicos(int indice, String nome) throws ExpressaoException {
		if (OperadorContexto.AND.equals(nome)) {
			return new And(indice);
		} else if (OperadorContexto.OR.equals(nome)) {
			return new Or(indice);
		} else if (OperadorContexto.XOR.equals(nome)) {
			return new Xor(indice);
		}
		return null;
	}

	private static Instrucao criarOutros(int indice, String nome) throws ExpressaoException {
		if (OperadorContexto.ADD_LISTA.equals(nome)) {
			return new AddLista(indice);
		} else if (OperadorContexto.CONCAT_LISTA.equals(nome)) {
			return new ConcatLista(indice);
		}
		return null;
	}
}