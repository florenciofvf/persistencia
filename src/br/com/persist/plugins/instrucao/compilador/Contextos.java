package br.com.persist.plugins.instrucao.compilador;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Contextos {
	public static final AbreParenteses ABRE_PARENTESES = new AbreParenteses();
	public static final AbreColchetes ABRE_COLCHETES = new AbreColchetes();
	public static final PontoVirgula PONTO_VIRGULA = new PontoVirgula();
	public static final AbreChaves ABRE_CHAVES = new AbreChaves();
	public static final Separador SEPARADOR = new Separador();

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

	public static class AbreChaves extends AbstratoContexto {
		@Override
		public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
			if (!"{".equals(token.getString())) {
				compilador.invalidar(token);
			}
		}
	}

	public static class AbreColchetes extends AbstratoContexto {
		@Override
		public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
			if (!"[".equals(token.getString())) {
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

	public static class Separador extends AbstratoContexto {
		@Override
		public void separador(Compilador compilador, Token token) throws InstrucaoException {
			//
		}
	}
}