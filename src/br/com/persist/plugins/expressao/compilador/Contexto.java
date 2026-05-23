package br.com.persist.plugins.expressao.compilador;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.BibliotecaContexto;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.funcao.IFuncaoContexto;
import br.com.persist.plugins.expressao.instrucoes.ExpressaoContexto;
import br.com.persist.plugins.expressao.instrucoes.InstrucoesContexto;
import br.com.persist.plugins.expressao.local.LocalContexto;
import br.com.persist.plugins.expressao.negativo.NegativoContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;

public abstract class Contexto {
	protected NegativoContexto negativoContexto;
	protected final List<Contexto> componentes;
	protected final List<Contexto> pilhaLocal;
	public static final String THIS = "this";
	protected int indiceEstado;
	protected Contexto parent;
	protected String prefixo;
	protected String biblio;
	protected String metodo;
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

	protected String getPrefixo() {
		return prefixo;
	}

	public void setPrefixo(String prefixo) {
		this.prefixo = prefixo;
	}

	protected String getBiblio() {
		return biblio;
	}

	public void setBiblio(String biblio) {
		this.biblio = biblio;
	}

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
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

	public void setIndice(int indice) {
		this.indice = indice;
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

	protected Contexto excluirPrimeiro() {
		Contexto primeiro = getPrimeiro();
		remove(primeiro);
		return primeiro;
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
		return getApos(posicao);
	}

	public Contexto getApos2(Contexto contexto) throws ExpressaoException {
		int posicao = getPosicao(contexto);
		Contexto apos = getApos(posicao);
		while (apos != null) {
			if (apos.isDeclaracaoFuncao()) {
				posicao = getPosicao(apos);
				apos = getApos(posicao);
			} else {
				return apos;
			}
		}
		return null;
	}

	private Contexto getApos(int posicao) {
		if (posicao + 1 < getSize()) {
			return get(posicao + 1);
		}
		return null;
	}

	protected void selecionadoVia(TokenManager tokenManager, Contexto contexto) throws ExpressaoException {
	}

	protected void processarPre(TokenManager tokenManager, Token token) throws ExpressaoException {
	}

	protected void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
	}

	public boolean isDeclaracaoFuncao() throws ExpressaoException {
		return false;
	}

	public boolean isDeclaracaoMapa() throws ExpressaoException {
		return false;
	}

	public boolean retornoGarantido() throws ExpressaoException {
		return false;
	}

	protected void checarIndiceEstado(TokenManager tokenManager, Object[] array, Token token)
			throws ExpressaoException {
		if (indiceEstado >= array.length) {
			tokenManager.invalidar(token);
		}
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

	protected void listarNegativo(List<Contexto> lista) {
		if (negativoContexto != null) {
			lista.add(negativoContexto);
		}
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

	protected void empilharLocalPre(List<Contexto> lista) {
	}

	protected void empilharLocalPos(List<Contexto> lista) {
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

	public void indexar(Indexador indexador) {
		indice = indexador.get1();
	}

	public void indexarNegativo(Indexador indexador) {
		if (negativoContexto != null) {
			negativoContexto.indexar(indexador);
		}
	}

	protected void ajusteFuncoesInternas(Indexador indexador) {
		ajusteFuncoesInternasPre(indexador);
		for (Contexto item : componentes) {
			item.ajusteFuncoesInternas(indexador);
		}
		ajusteFuncoesInternasPos(indexador);
	}

	protected void ajusteFuncoesInternasPre(Indexador indexador) {
	}

	protected void ajusteFuncoesInternasPos(Indexador indexador) {
	}

	protected void ajusteChavesEInvocacoes(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException {
		ajusteChavesEInvocacoesPre(mapaAlias, cache);
		for (Contexto item : componentes) {
			item.ajusteChavesEInvocacoes(mapaAlias, cache);
		}
		ajusteChavesEInvocacoesPos(mapaAlias, cache);
	}

	protected void ajusteChavesEInvocacoesPre(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException {
	}

	protected void ajusteChavesEInvocacoesPos(Map<String, AliasContexto> mapaAlias, CacheBiblioteca cache)
			throws ExpressaoException {
	}

	public void salvar(PrintWriter pw) throws ExpressaoException {
	}

	protected void listarFuncoes(List<Contexto> lista) {
		listarFuncoesPre(lista);
		for (Contexto item : componentes) {
			item.listarFuncoes(lista);
		}
		listarFuncoesPos(lista);
	}

	protected void listarFuncoesPre(List<Contexto> lista) {
	}

	protected void listarFuncoesPos(List<Contexto> lista) {
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
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isChave()) {
				Contexto.this.token = token;
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	public class IniExpressao implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreParentese()) {
				ExpressaoContexto expressao = new ExpressaoContexto();
				tokenManager.selecionar(expressao);
				adicionar(expressao);
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	public class IniInstrucoes implements TokenExec {
		private final boolean incondicional;

		public IniInstrucoes(boolean incondicional) {
			this.incondicional = incondicional;
		}

		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isAbreChave()) {
				InstrucoesContexto instrucoes = new InstrucoesContexto(incondicional);
				tokenManager.selecionar(instrucoes);
				adicionar(instrucoes);
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	public class PontoEVirgula implements TokenExec {
		public void processar(TokenManager tokenManager, Token token) throws ExpressaoException {
			if (token.isPontoEVirgula()) {
				tokenManager.selecionarParentDe(Contexto.this);
				indiceEstado++;
			} else {
				tokenManager.invalidar(token);
			}
		}
	}

	public BibliotecaContexto getBibliotecaContexto() {
		Contexto c = this;
		while (c != null) {
			if (c instanceof BibliotecaContexto) {
				return (BibliotecaContexto) c;
			}
			c = c.parent;
		}
		return null;
	}

	/*protected List<String> checarSeEhParametroDeFuncao(String string, AtomicBoolean sucesso) {
		List<String> lista = new ArrayList<>();
		Contexto c = this;
		sucesso.set(false);
		while (c != null) {
			if (c instanceof FuncaoContexto) {
				FuncaoContexto funcao = (FuncaoContexto) c;
				lista.add(funcao.getNome());
				if (funcao.getParametros().contem(string)) {
					sucesso.set(true);
					break;
				}
			}
			c = c.getParent();
		}
		return lista;
	}

	protected List<String> checarSeEhLocalDeFuncao(String string, AtomicBoolean sucesso) {
		List<String> lista = new ArrayList<>();
		Contexto c = this;
		sucesso.set(false);
		while (c != null) {
			if (c instanceof FuncaoContexto) {
				FuncaoContexto funcao = (FuncaoContexto) c;
				lista.add(funcao.getNome());
				InstrucoesContexto instrucoes = funcao.getInstrucoesContexto();
				LocalContexto localContx = instrucoes.getLocalContexto(string);
				if (localContx != null) {
					sucesso.set(true);
					break;
				}
			}
			c = c.getParent();
		}
		return lista;
	}*/

	protected List<String> listarHierarquiaFuncao() {
		List<String> lista = new ArrayList<>();
		Contexto c = parent;
		while (c != null) {
			if (c instanceof FuncaoContexto) {
				FuncaoContexto funcao = (FuncaoContexto) c;
				lista.add(funcao.getNome());
			}
			c = c.getParent();
		}
		return lista;
	}

	protected IFuncaoContexto getFuncaoNomeOriginal(String nome) {
		Contexto c = this;
		while (c != null) {
			if (c instanceof IFuncaoContexto) {
				IFuncaoContexto funcao = (IFuncaoContexto) c;
				if (funcao.isNomeOriginal(nome)) {
					return funcao;
				}
			}
			c = c.getParent();
		}
		return null;
	}

	protected String montarString(List<String> lista) {
		StringBuilder builder = new StringBuilder();
		for (String item : lista) {
			if (builder.length() > 0) {
				builder.append("$");
			}
			builder.append(item);
		}
		return builder.toString();
	}
}