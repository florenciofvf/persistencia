package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public abstract class Container extends AbstratoContexto {
	protected final List<Container> componentes;
	protected NegativoContexto negativo;
	protected int pontoDeslocamento;
	protected Contexto contexto;
	protected int sequencia;
	protected Container pai;
	protected Token token;

	protected Container() {
		componentes = new ArrayList<>();
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Container getPai() {
		return pai;
	}

	public int getPontoDeslocamento() {
		return pontoDeslocamento;
	}

	protected CorpoContexto getCorpoContexto(Container c) {
		while (c != null) {
			if (c instanceof CorpoContexto) {
				break;
			}
			c = c.pai;
		}
		return (CorpoContexto) c;
	}

	protected WhileContexto getWhileContextoGoto() {
		if (pai instanceof CorpoContexto) {
			CorpoContexto corpo = (CorpoContexto) pai;
			if (corpo.pai instanceof WhileContexto) {
				return (WhileContexto) corpo.pai;
			}
		}
		return null;
	}

	protected IFContexto getIFContextoGoto() {
		if (pai instanceof CorpoContexto) {
			CorpoContexto corpo = (CorpoContexto) pai;
			if (corpo.pai instanceof IFContexto) {
				return (IFContexto) corpo.pai;
			}
		}
		return null;
	}

	protected WhileContexto getWhileContextoIFEq() {
		if (pai instanceof WhileContexto) {
			return (WhileContexto) pai;
		}
		return null;
	}

	protected IFContexto getIFContextoIFEq() {
		if (pai instanceof IFContexto) {
			return (IFContexto) pai;
		}
		return null;
	}

	public BibliotecaContexto getBiblioteca() {
		Container c = this;
		while (c != null) {
			if (c instanceof BibliotecaContexto) {
				break;
			}
			c = c.pai;
		}
		return (BibliotecaContexto) c;
	}

	protected FuncaoContexto getFuncao() {
		Container c = this;
		while (c != null) {
			if (c instanceof FuncaoContexto) {
				break;
			}
			c = c.pai;
		}
		return (FuncaoContexto) c;
	}

	protected boolean ehParametro(String id) throws InstrucaoException {
		return getParametroContexto(id) != null;
	}

	protected ParametroContexto getParametroContexto(String id) throws InstrucaoException {
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			throw new InstrucaoException("erro.funcao_parent", id);
		}
		ParametrosContexto parametros = funcao.getParametros();
		return parametros.getParametro(id);
	}

	public List<Container> getComponentes() {
		return componentes;
	}

	public int getSize() {
		return componentes.size();
	}

	public boolean isEmpty() {
		return componentes.isEmpty();
	}

	protected int getIndice(Container c) {
		for (int i = 0; i < componentes.size(); i++) {
			if (componentes.get(i) == c) {
				return i;
			}
		}
		return -1;
	}

	public Container get(int indice) {
		if (indice >= 0 && indice < getSize()) {
			return componentes.get(indice);
		}
		return null;
	}

	public Container excluir(int indice) {
		Container c = get(indice);
		if (c != null) {
			return excluir(c);
		}
		return null;
	}

	public void clear() {
		while (getSize() > 0) {
			excluir(0);
		}
	}

	public Container excluirUltimo() {
		return excluir(getSize() - 1);
	}

	public Container getPrimeiro() {
		return get(0);
	}

	public Container getUltimo() {
		return get(getSize() - 1);
	}

	public Container excluir(Container c) {
		boolean excluido = false;
		if (c.pai == this) {
			excluido = componentes.remove(c);
			c.pai = null;
		}
		return excluido ? c : null;
	}

	public void adicionar(Container c) {
		if (c.pai != null) {
			c.pai.excluir(c);
		}
		componentes.add(c);
		c.pai = this;
	}

	public void negativar(Contexto c) {
		if (c instanceof OperadorContexto) {
			OperadorContexto operador = (OperadorContexto) c;
			if ("-".equals(operador.getId())) {
				negativo = new NegativoContexto();
			}
		}
	}

	public void estruturar() {
		estruturarImpl();
		for (Container c : componentes) {
			c.estruturar();
		}
	}

	protected void estruturarImpl() {
	}

	public void indexar(Indexador indexador) {
		for (Container c : componentes) {
			c.indexar(indexador);
		}
	}

	public void indexarNegativo(Indexador indexador) {
		if (negativo != null) {
			negativo.indexar(indexador);
		}
	}

	public void desviar() throws InstrucaoException {
		desviarImpl();
		for (Container c : componentes) {
			c.desviar();
		}
	}

	protected void desviarImpl() throws InstrucaoException {
	}

	public void validar() throws InstrucaoException {
		validarImpl();
		for (Container c : componentes) {
			c.validar();
		}
	}

	protected void validarImpl() throws InstrucaoException {
	}

	public void salvar(PrintWriter pw) throws InstrucaoException {
		for (Container c : componentes) {
			c.salvar(pw);
		}
	}

	public void salvarNegativo(PrintWriter pw) {
		if (negativo != null) {
			negativo.salvar(pw);
		}
	}

	void print(PrintWriter pw, String... strings) {
		pw.print(sequencia + InstrucaoConstantes.ESPACO);
		for (String string : strings) {
			pw.print(InstrucaoConstantes.ESPACO + string);
		}
		pw.println();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}