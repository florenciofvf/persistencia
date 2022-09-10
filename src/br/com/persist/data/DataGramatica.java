package br.com.persist.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DataGramatica {
	private DataGramatica() {
	}

	public static Tipo criarTipo(String stringJSON) throws DataException {
		AtomicReference<Tipo> ref = new AtomicReference<>();
		DataToken dataToken = new DataToken(stringJSON);
		List<Token> tokens = dataToken.getTokens();
		Tipo selecionado = null;
		for (Token token : tokens) {
			if (token.isChaveIni()) {
				selecionado = processarCharIni(ref, selecionado, token);
			} else if (token.isColcheteIni()) {
				selecionado = processarColcheteIni(ref, selecionado, token);
			} else if (token.isChaveFim() || token.isColcheteFim()) {
				selecionado = processarFim(selecionado, token);
			} else if (token.isVirgula()) {
				processarVirgula(selecionado, token);
			} else if (token.isSepAtributos()) {
				processarSepAtributos(selecionado, token);
			} else if (ehTipoAtomico(token)) {
				processarAtomico(selecionado, token);
			} else {
				throw new DataException("Token invalido >>> " + token);
			}
		}
		return ref.get();
	}

	private static Tipo processarCharIni(AtomicReference<Tipo> ref, Tipo selecionado, Token token)
			throws DataException {
		Tipo novoObjeto = new Objeto();
		if (selecionado instanceof Array) {
			((Array) selecionado).addElemento(novoObjeto);
			selecionado = novoObjeto;
		} else if (selecionado instanceof Objeto) {
			((Objeto) selecionado).processar(novoObjeto);
			selecionado = novoObjeto;
		} else if (selecionado == null) {
			if (ref.get() != null) {
				throw new DataException("Chave ini invalido >>> " + token);
			}
			selecionado = novoObjeto;
			ref.set(novoObjeto);
		} else {
			throw new DataException("Chave ini invalido >>> " + token);
		}
		return selecionado;
	}

	private static Tipo processarColcheteIni(AtomicReference<Tipo> ref, Tipo selecionado, Token token)
			throws DataException {
		Tipo novoArray = new Array();
		if (selecionado instanceof Array) {
			((Array) selecionado).addElemento(novoArray);
			selecionado = novoArray;
		} else if (selecionado instanceof Objeto) {
			((Objeto) selecionado).processar(novoArray);
			selecionado = novoArray;
		} else if (selecionado == null) {
			if (ref.get() != null) {
				throw new DataException("Colchete ini invalido >>> " + token);
			}
			selecionado = novoArray;
			ref.set(novoArray);
		} else {
			throw new DataException("Colchete ini invalido >>> " + token);
		}
		return selecionado;
	}

	private static Tipo processarFim(Tipo selecionado, Token token) throws DataException {
		if (selecionado != null) {
			selecionado = selecionado.getPai();
		} else {
			throw new DataException("Selecionado invalido >>> " + token);
		}
		return selecionado;
	}

	private static void processarVirgula(Tipo selecionado, Token token) throws DataException {
		if (selecionado instanceof Array) {
			((Array) selecionado).preElemento();
		} else if (selecionado instanceof Objeto) {
			((Objeto) selecionado).preAtributo();
		} else {
			throw new DataException("Token invalido virgula >>> " + token);
		}
	}

	private static void processarSepAtributos(Tipo selecionado, Token token) throws DataException {
		if (selecionado instanceof Objeto) {
			((Objeto) selecionado).checkDoisPontos();
		} else {
			throw new DataException("Objeto invalido >>> " + token);
		}
	}

	private static void processarAtomico(Tipo selecionado, Token token) throws DataException {
		Tipo atomico = Token.criarAtomico(token);
		if (selecionado instanceof Array) {
			((Array) selecionado).addElemento(atomico);
		} else if (selecionado instanceof Objeto) {
			((Objeto) selecionado).processar(atomico);
		} else {
			throw new DataException("Token invalido >>> " + token);
		}
	}

	private static boolean ehTipoAtomico(Token token) {
		return token.isBoolean() || token.isNumero() || token.isString() || token.isNull();
	}
}