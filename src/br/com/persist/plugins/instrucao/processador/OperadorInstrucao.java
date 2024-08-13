package br.com.persist.plugins.instrucao.processador;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.biblionativo.Lista;
import br.com.persist.plugins.instrucao.compilador.OperadorContexto;

public class OperadorInstrucao {
	private OperadorInstrucao() {
	}

	public static BigInteger castBI(Object obj) {
		return (BigInteger) obj;
	}

	public static BigDecimal castBD(Object obj) {
		return (BigDecimal) obj;
	}

	public static BigDecimal createBD(BigInteger bigInteger) {
		return new BigDecimal(bigInteger);
	}

	public static BigInteger createFalse() {
		return BigInteger.valueOf(0);
	}

	public static BigInteger createTrue() {
		return BigInteger.valueOf(1);
	}

	public static BigInteger igual(BigInteger e, BigInteger d) {
		return e.compareTo(d) == 0 ? createTrue() : createFalse();
	}

	public static BigInteger igual(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) == 0 ? createTrue() : createFalse();
	}

	public static BigInteger iquals(Object e, Object d) {
		return e.equals(d) ? createTrue() : createFalse();
	}

	public static BigInteger diferente(BigInteger e, BigInteger d) {
		return e.compareTo(d) != 0 ? createTrue() : createFalse();
	}

	public static BigInteger diferente(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) != 0 ? createTrue() : createFalse();
	}

	public static BigInteger differ(Object e, Object d) {
		return !e.equals(d) ? createTrue() : createFalse();
	}

	public static BigInteger maior(BigInteger e, BigInteger d) {
		return e.compareTo(d) > 0 ? createTrue() : createFalse();
	}

	public static BigInteger maior(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) > 0 ? createTrue() : createFalse();
	}

	public static BigInteger menor(BigInteger e, BigInteger d) {
		return e.compareTo(d) < 0 ? createTrue() : createFalse();
	}

	public static BigInteger menor(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) < 0 ? createTrue() : createFalse();
	}

	public static BigInteger maiorI(BigInteger e, BigInteger d) {
		return e.compareTo(d) >= 0 ? createTrue() : createFalse();
	}

	public static BigInteger maiorI(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) >= 0 ? createTrue() : createFalse();
	}

	public static BigInteger menorI(BigInteger e, BigInteger d) {
		return e.compareTo(d) <= 0 ? createTrue() : createFalse();
	}

	public static BigInteger menorI(BigDecimal e, BigDecimal d) {
		return e.compareTo(d) <= 0 ? createTrue() : createFalse();
	}

	public static class Add extends Instrucao {
		public Add() {
			super(OperadorContexto.ADD);
		}

		@Override
		public Instrucao clonar() {
			return new Add();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarOperando(operandoE);
			InstrucaoUtil.checarOperando(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBI(operandoE).add(castBI(operandoD)));
				} else if (operandoD instanceof BigDecimal) {
					pilhaOperando.push(createBD(castBI(operandoE)).add(castBD(operandoD)));
				} else {
					pilhaOperando.push(operandoE.toString() + operandoD.toString());
				}
			} else if (operandoE instanceof BigDecimal) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBD(operandoE).add(createBD(castBI(operandoD))));
				} else if (operandoD instanceof BigDecimal) {
					pilhaOperando.push(castBD(operandoE).add(castBD(operandoD)));
				} else {
					pilhaOperando.push(operandoE.toString() + operandoD.toString());
				}
			} else {
				pilhaOperando.push(operandoE.toString() + operandoD.toString());
			}
		}
	}

	public static class AddLista extends Instrucao {
		public AddLista() {
			super(OperadorContexto.ADD_LISTA);
		}

		@Override
		public Instrucao clonar() {
			return new AddLista();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarOperando(operandoE);
			InstrucaoUtil.checarOperando(operandoD);
			Lista lista = new Lista();
			add(lista, operandoE);
			add(lista, operandoD);
			pilhaOperando.push(lista);
		}

		private void add(Lista lista, Object obj) {
			if (obj instanceof Lista) {
				lista.addLista((Lista) obj);
			} else {
				lista.add(obj);
			}
		}
	}

	public static class Sub extends Instrucao {
		public Sub() {
			super(OperadorContexto.SUB);
		}

		@Override
		public Instrucao clonar() {
			return new Sub();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBI(operandoE).subtract(castBI(operandoD)));
				} else {
					pilhaOperando.push(createBD(castBI(operandoE)).subtract(castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBD(operandoE).subtract(createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(castBD(operandoE).subtract(castBD(operandoD)));
				}
			}
		}
	}

	public static class Mul extends Instrucao {
		public Mul() {
			super(OperadorContexto.MUL);
		}

		@Override
		public Instrucao clonar() {
			return new Mul();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBI(operandoE).multiply(castBI(operandoD)));
				} else {
					pilhaOperando.push(createBD(castBI(operandoE)).multiply(castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBD(operandoE).multiply(createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(castBD(operandoE).multiply(castBD(operandoD)));
				}
			}
		}
	}

	public static class Div extends Instrucao {
		public Div() {
			super(OperadorContexto.DIV);
		}

		@Override
		public Instrucao clonar() {
			return new Div();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBI(operandoE).divide(castBI(operandoD)));
				} else {
					pilhaOperando.push(createBD(castBI(operandoE)).divide(castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBD(operandoE).divide(createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(castBD(operandoE).divide(castBD(operandoD)));
				}
			}
		}
	}

	public static class Rem extends Instrucao {
		public Rem() {
			super(OperadorContexto.REM);
		}

		@Override
		public Instrucao clonar() {
			return new Rem();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBI(operandoE).remainder(castBI(operandoD)));
				} else {
					pilhaOperando.push(createBD(castBI(operandoE)).remainder(castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(castBD(operandoE).remainder(createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(castBD(operandoE).remainder(castBD(operandoD)));
				}
			}
		}
	}

	public static class And extends Instrucao {
		public And() {
			super(OperadorContexto.AND);
		}

		@Override
		public Instrucao clonar() {
			return new And();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarNumber(operandoE);
			InstrucaoUtil.checarNumber(operandoD);
			int valor = ((Number) operandoE).intValue();
			if (valor == 0) {
				pilhaOperando.push(createFalse());
				return;
			}
			valor = ((Number) operandoD).intValue();
			if (valor == 0) {
				pilhaOperando.push(createFalse());
				return;
			}
			pilhaOperando.push(createTrue());
		}
	}

	public static class Or extends Instrucao {
		public Or() {
			super(OperadorContexto.OR);
		}

		@Override
		public Instrucao clonar() {
			return new Or();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarNumber(operandoE);
			InstrucaoUtil.checarNumber(operandoD);
			int valor = ((Number) operandoE).intValue();
			if (valor != 0) {
				pilhaOperando.push(createTrue());
				return;
			}
			valor = ((Number) operandoD).intValue();
			if (valor != 0) {
				pilhaOperando.push(createTrue());
				return;
			}
			pilhaOperando.push(createFalse());
		}
	}

	public static class Xor extends Instrucao {
		public Xor() {
			super(OperadorContexto.XOR);
		}

		@Override
		public Instrucao clonar() {
			return new Xor();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarNumber(operandoE);
			InstrucaoUtil.checarNumber(operandoD);
			int valorE = ((Number) operandoE).intValue();
			int valorD = ((Number) operandoD).intValue();
			if ((valorE == 0 && valorD != 0) || (valorE != 0 && valorD == 0)) {
				pilhaOperando.push(createTrue());
			} else {
				pilhaOperando.push(createFalse());
			}
		}
	}

	public static class Igual extends Instrucao {
		public Igual() {
			super(OperadorContexto.IGUAL);
		}

		@Override
		public Instrucao clonar() {
			return new Igual();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarOperando(operandoE);
			InstrucaoUtil.checarOperando(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(igual(castBI(operandoE), castBI(operandoD)));
				} else if (operandoD instanceof BigDecimal) {
					pilhaOperando.push(igual(createBD(castBI(operandoE)), castBD(operandoD)));
				} else {
					pilhaOperando.push(createFalse());
				}
			} else if (operandoE instanceof BigDecimal) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(igual(castBD(operandoE), createBD(castBI(operandoD))));
				} else if (operandoD instanceof BigDecimal) {
					pilhaOperando.push(igual(castBD(operandoE), castBD(operandoD)));
				} else {
					pilhaOperando.push(createFalse());
				}
			} else {
				pilhaOperando.push(iquals(operandoE, operandoD));
			}
		}
	}

	public static class Diff extends Instrucao {
		public Diff() {
			super(OperadorContexto.DIFF);
		}

		@Override
		public Instrucao clonar() {
			return new Diff();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarOperando(operandoE);
			InstrucaoUtil.checarOperando(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(diferente(castBI(operandoE), castBI(operandoD)));
				} else if (operandoD instanceof BigDecimal) {
					pilhaOperando.push(diferente(createBD(castBI(operandoE)), castBD(operandoD)));
				} else {
					pilhaOperando.push(createTrue());
				}
			} else if (operandoE instanceof BigDecimal) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(diferente(castBD(operandoE), createBD(castBI(operandoD))));
				} else if (operandoD instanceof BigDecimal) {
					pilhaOperando.push(diferente(castBD(operandoE), castBD(operandoD)));
				} else {
					pilhaOperando.push(createTrue());
				}
			} else {
				pilhaOperando.push(differ(operandoE, operandoD));
			}
		}
	}

	public static class Menor extends Instrucao {
		public Menor() {
			super(OperadorContexto.MENOR);
		}

		@Override
		public Instrucao clonar() {
			return new Menor();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(menor(castBI(operandoE), castBI(operandoD)));
				} else {
					pilhaOperando.push(menor(createBD(castBI(operandoE)), castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(menor(castBD(operandoE), createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(menor(castBD(operandoE), castBD(operandoD)));
				}
			}
		}
	}

	public static class Maior extends Instrucao {
		public Maior() {
			super(OperadorContexto.MAIOR);
		}

		@Override
		public Instrucao clonar() {
			return new Maior();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(maior(castBI(operandoE), castBI(operandoD)));
				} else {
					pilhaOperando.push(maior(createBD(castBI(operandoE)), castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(maior(castBD(operandoE), createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(maior(castBD(operandoE), castBD(operandoD)));
				}
			}
		}
	}

	public static class MenorIgual extends Instrucao {
		public MenorIgual() {
			super(OperadorContexto.MENOR_IGUAL);
		}

		@Override
		public Instrucao clonar() {
			return new MenorIgual();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(menorI(castBI(operandoE), castBI(operandoD)));
				} else {
					pilhaOperando.push(menorI(createBD(castBI(operandoE)), castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(menorI(castBD(operandoE), createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(menorI(castBD(operandoE), castBD(operandoD)));
				}
			}
		}
	}

	public static class MaiorIgual extends Instrucao {
		public MaiorIgual() {
			super(OperadorContexto.MAIOR_IGUAL);
		}

		@Override
		public Instrucao clonar() {
			return new MaiorIgual();
		}

		@Override
		public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
				PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
			Object operandoD = pilhaOperando.pop();
			Object operandoE = pilhaOperando.pop();
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoE);
			InstrucaoUtil.checarBigIntegerBigDecimal(operandoD);
			if (operandoE instanceof BigInteger) {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(maiorI(castBI(operandoE), castBI(operandoD)));
				} else {
					pilhaOperando.push(maiorI(createBD(castBI(operandoE)), castBD(operandoD)));
				}
			} else {
				if (operandoD instanceof BigInteger) {
					pilhaOperando.push(maiorI(castBD(operandoE), createBD(castBI(operandoD))));
				} else {
					pilhaOperando.push(maiorI(castBD(operandoE), castBD(operandoD)));
				}
			}
		}
	}
}