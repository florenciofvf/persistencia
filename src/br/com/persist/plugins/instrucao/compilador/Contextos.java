package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Contextos {
	public static final FechaParenteses FECHA_PARENTESES = new FechaParenteses();
	public static final AbreParenteses ABRE_PARENTESES = new AbreParenteses();
	public static final PontoVirgula PONTO_VIRGULA = new PontoVirgula();
	public static final FechaChaves FECHA_CHAVES = new FechaChaves();
	public static final AbreChaves ABRE_CHAVES = new AbreChaves();
	public static final Parenteses PARENTESES = new Parenteses();
	public static final Identity IDENTITY = new Identity();
	public static final Invalido INVALIDO = new Invalido();
	public static final Virgula VIRGULA = new Virgula();
	public static final Texto TEXTO = new Texto();

	private Contextos() {
	}

	public static class AbreParenteses extends AbstratoContexto {
		@Override
		public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
			if (!"(".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class FechaParenteses extends AbstratoContexto {
		@Override
		public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
			if (!")".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class Parenteses extends AbstratoContexto {
		@Override
		public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
			if (!"(".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}

		@Override
		public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
			if (!")".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class AbreChaves extends AbstratoContexto {
		@Override
		public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
			if (!"{".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class FechaChaves extends AbstratoContexto {
		@Override
		public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
			if (!"}".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class PontoVirgula extends AbstratoContexto {
		@Override
		public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
			if (!";".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class Virgula extends AbstratoContexto {
		@Override
		public void separador(Compilador compilador, Token token) throws InstrucaoException {
			//
		}
	}

	public static class Identity extends AbstratoContexto {
		@Override
		public void identity(Compilador compilador, Token token) throws InstrucaoException {
			//
		}
	}

	public static class Texto extends AbstratoContexto {
		@Override
		public void string(Compilador compilador, Token token) throws InstrucaoException {
			//
		}
	}

	public static class Invalido extends AbstratoContexto {
	}
}