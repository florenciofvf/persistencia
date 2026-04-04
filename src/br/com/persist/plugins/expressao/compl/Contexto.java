package br.com.persist.plugins.expressao.compl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.compl.instrucoes.InstrucoesContexto;

public abstract class Contexto {
	protected NegativoContexto negativoContexto;
	protected final List<Contexto> componentes;
	protected final List<Contexto> pilhaLocal;
	protected int indiceEstado;
	protected Contexto parent;
	protected Token token;
	protected int indice;

	protected Contexto(Token token) {
		componentes = new ArrayList<>();
		pilhaLocal = new ArrayList<>();
		this.token = token;
	}

	protected Contexto() {
		this(null);
	}

	public NegativoContexto getNegativoContexto() {
		return negativoContexto;
	}

	public void negativar(Token token) {
		if (token != null && "-".equals(token.getString())) {
			negativoContexto = new NegativoContexto();
		}
	}

	public void setNegativoContexto(NegativoContexto negativoContexto) {
		if (negativoContexto != null) {
			this.negativoContexto = negativoContexto;
		}
	}

	public int getIndice() {
		return indice;
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

	public Contexto excluirUltimo() {
		Contexto ultimo = getUltimo();
		remove(ultimo);
		return ultimo;
	}

	public void adicionar(Contexto c) throws ExpressaoException {
		if (c != null) {
			adicionar2(c);
		} else {
			throw new ExpressaoException("erro.inclusao.nulo");
		}
	}

	protected void adicionar2(Contexto c) {
		if (c.parent != null) {
			c.parent.remove(c);
		}
		componentes.add(c);
		c.parent = this;
	}

	protected void remove(Contexto c) {
		if (c != null && c.parent == this) {
			componentes.remove(c);
			c.parent = null;
		}
	}

	protected int getPosicao(Contexto contexto) throws ExpressaoException {
		for (int i = 0; i < componentes.size(); i++) {
			if (componentes.get(i) == contexto) {
				return i;
			}
		}
		throw new ExpressaoException("erro.contexto.nao_contem_contexto");
	}

	public Contexto getApos(Contexto contexto) throws ExpressaoException {
		int posicao = getPosicao(contexto);
		if (posicao + 1 < getSize()) {
			return get(posicao + 1);
		}
		return null;
	}

	protected void selecionadoVia(Compilador compilador, Contexto contexto) throws ExpressaoException {
	}

	protected void processarPre(Compilador compilador, Token token) throws ExpressaoException {
	}

	protected void processar(Compilador compilador, Token token) throws ExpressaoException {
	}

	protected void checarIndiceEstado(Compilador compilador, Object[] array, Token token) throws ExpressaoException {
		if (indiceEstado >= array.length) {
			compilador.invalidar(token);
		}
	}

	public void configurarAliasInvocacao(Map<String, String> mapa) throws ExpressaoException {
		configurarAliasInvocacaoPre(mapa);
		for (Contexto item : componentes) {
			item.configurarAliasInvocacao(mapa);
		}
		configurarAliasInvocacaoPos(mapa);
	}

	protected void configurarAliasInvocacaoPre(Map<String, String> mapa) throws ExpressaoException {
	}

	protected void configurarAliasInvocacaoPos(Map<String, String> mapa) throws ExpressaoException {
	}

	public void listar(List<Contexto> lista) {
		listarPre(lista);
		for (Contexto item : componentes) {
			item.listar(lista);
		}
		listarPos(lista);
	}

	protected void listarNegativo(List<Contexto> lista) {
		if (negativoContexto != null) {
			lista.add(negativoContexto);
		}
	}

	protected void listarPre(List<Contexto> lista) {
	}

	protected void listarPos(List<Contexto> lista) {
	}

	public void configurarSaltos() throws ExpressaoException {
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

	protected void empilharLocalNegativo(List<Contexto> lista) {
		if (negativoContexto != null) {
			lista.add(negativoContexto);
		}
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

	public void indexar(Indexador indexador) {
		indice = indexador.get1();
	}

	public void indexarNegativo(Indexador indexador) {
		if (negativoContexto != null) {
			negativoContexto.indexar(indexador);
		}
	}

	public void salvar(PrintWriter pw) throws ExpressaoException {
	}

	public void salvarNegativo(PrintWriter pw) throws ExpressaoException {
		if (negativoContexto != null) {
			negativoContexto.salvar(pw);
		}
	}

	public void print(PrintWriter pw, String... strings) {
		pw.print(indice + ExpressaoConstantes.ESPACO);
		for (String item : strings) {
			pw.print(ExpressaoConstantes.ESPACO + item);
		}
		pw.println();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ">>>" + token;
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

	public class AbreParentese implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				compilador.selecionar(expressao);
				adicionar(expressao);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	public class AbreChave implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto();
				compilador.selecionar(instrucoes);
				adicionar(instrucoes);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}

	public class PontoEVirgula implements TokenExec {
		public void processar(Compilador compilador, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				compilador.selecionarParentDe(Contexto.this);
				indiceEstado++;
			} else {
				compilador.invalidar(token);
			}
		}
	}
}