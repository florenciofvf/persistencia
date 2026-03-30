package br.com.persist.plugins.expressao.compl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.compl.instrucoes.InstrucoesContexto;

public abstract class Contexto {
	protected final List<Contexto> componentes;
	protected final List<Contexto> pilhaLocal;
	protected int indiceEstado;
	protected Contexto parent;
	protected Token token;

	protected Contexto(Token token) {
		componentes = new ArrayList<>();
		pilhaLocal = new ArrayList<>();
		this.token = token;
	}

	protected void checarIndiceEstado(Compilador compilador, Object[] array, Token token) throws ExpressaoException {
		if (indiceEstado >= array.length) {
			compilador.invalidar(token);
		}
	}

	protected Contexto() {
		this(null);
	}

	public Token getToken() {
		return token;
	}

	protected boolean isEmpty() {
		return componentes.isEmpty();
	}

	protected int getSize() {
		return componentes.size();
	}

	public Contexto getParent() {
		return parent;
	}

	protected Contexto getUltimo() {
		return get(componentes.size() - 1);
	}

	public Contexto getPrimeiro() {
		return get(0);
	}

	protected Contexto get(int indice) {
		if (indice >= 0 && indice < componentes.size()) {
			return componentes.get(indice);
		}
		return null;
	}

	protected Contexto excluirUltimo() {
		Contexto ultimo = getUltimo();
		remove(ultimo);
		return ultimo;
	}

	protected void add(Contexto c) throws ExpressaoException {
		if (c != null) {
			if (c.parent != null) {
				c.parent.remove(c);
			}
			componentes.add(c);
			c.parent = this;
		} else {
			throw new ExpressaoException("erro.inclusao.nulo");
		}
	}

	protected void remove(Contexto c) {
		if (c != null && c.parent == this) {
			componentes.remove(c);
			c.parent = null;
		}
	}

	protected int getIndice(Contexto contexto) throws ExpressaoException {
		for (int i = 0; i < componentes.size(); i++) {
			if (componentes.get(i) == contexto) {
				return i;
			}
		}
		throw new ExpressaoException("erro.contexto.nao_contem_contexto");
	}

	public Contexto getApos(Contexto contexto) throws ExpressaoException {
		int indice = getIndice(contexto);
		if (indice + 1 < getSize()) {
			return get(indice + 1);
		}
		return null;
	}

	protected void processar(Compilador compilador, Token token) throws ExpressaoException {

	}

	protected void listar(List<Contexto> lista) {
		listarPre(lista);
		for (Contexto item : componentes) {
			item.listar(lista);
		}
		listarPos(lista);
	}

	protected void listarPre(List<Contexto> lista) {
	}

	protected void listarPos(List<Contexto> lista) {
	}

	protected void configurarSaltos() throws ExpressaoException {
		configurarSaltosPre();
		for (Contexto item : componentes) {
			item.configurarSaltos();
		}
		configurarSaltosPos();
	}

	protected void configurarSaltosPre() throws ExpressaoException {
	}

	protected void configurarSaltosPos() throws ExpressaoException {
	}

	protected void empilharLocal(List<Contexto> lista) {
		empilharLocalPre(lista);
		for (Contexto item : componentes) {
			item.empilharLocal(lista);
		}
		empilharLocalPos(lista);
	}

	public void empilharLocalIni() {
		pilhaLocal.clear();
		empilharLocal(pilhaLocal);
	}

	public List<Contexto> getPilhaLocal() {
		return pilhaLocal;
	}

	protected void empilharLocalPre(List<Contexto> lista) {
	}

	protected void empilharLocalPos(List<Contexto> lista) {
	}

	protected void checarVazioInstrucoes() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.instrucoes.vazio");
		}
	}

	protected void checarVazioExpressao() throws ExpressaoException {
		if (isEmpty()) {
			throw new ExpressaoException("erro.expressao.vazio");
		}
	}

	protected void salvar(PrintWriter pw) throws ExpressaoException {

	}

	public class Chave implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isChave()) {
				Contexto.this.token = token;
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	protected class AbreParentese implements TokenExec {
		final boolean comSalto;

		public AbreParentese(boolean comSalto) {
			this.comSalto = comSalto;
		}

		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto(comSalto);
				compilador.setSelecionado(expressao);
				add(expressao);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	public class AbreChave implements TokenExec {
		final byte estrutura;

		public AbreChave(byte estrutura) {
			this.estrutura = estrutura;
		}

		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto(estrutura);
				compilador.setSelecionado(instrucoes);
				add(instrucoes);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	public class PontoEVirgula implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				compilador.setSelecionado(parent);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}