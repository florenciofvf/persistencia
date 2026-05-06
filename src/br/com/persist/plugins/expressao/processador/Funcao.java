package br.com.persist.plugins.expressao.processador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.funcao.IFuncaoContexto;

public class Funcao {
	private final Map<Integer, InstrucaoItem> mapaInstrucoes;
	private final List<Parametro> parametros;
	private final Biblioteca biblioteca;
	private InstrucaoItem ponteiro;
	private final String[] origem;
	private InstrucaoItem cabeca;
	private String biblioNativa;
	private InstrucaoItem cauda;
	private final String nome;
	private boolean tipoVoid;
	private Funcao parent;

	public Funcao(Biblioteca biblioteca, String nome, String[] origem) {
		this.biblioteca = Objects.requireNonNull(biblioteca);
		this.nome = Objects.requireNonNull(nome);
		this.mapaInstrucoes = new HashMap<>();
		this.parametros = new ArrayList<>();
		this.origem = origem;
	}

	public String[] getOrigem() {
		return origem;
	}

	public String getNome() {
		return nome;
	}

	public String getNomeOriginal() {
		char c = nome.charAt(0);
		if (c >= '0' && c <= '9') {
			int pos = nome.indexOf('_');
			return nome.substring(pos + 1);
		}
		return null;
	}

	protected void checarHierarquia() throws ExpressaoException {
		if (origem == null) {
			return;
		}
		if (parent == null) {
			throw new ExpressaoException("erro.funcao_sem_parent", nome, biblioteca.getNomeAbsoluto());
		}
		String nomeFuncaoParent = origem[0];
		boolean valido = nomeFuncaoParent.equals(parent.getNome()) || nomeFuncaoParent.equals(parent.getNomeOriginal());
		if (!valido) {
			throw new ExpressaoException("erro.funcao_checar_hierarquia", parent.getNome(), nome,
					biblioteca.getNomeAbsoluto());
		}
	}

	public Funcao clonarSemParent() throws ExpressaoException {
		Funcao clone = new Funcao(biblioteca, nome, origem);
		clone.biblioNativa = biblioNativa;
		clone.tipoVoid = tipoVoid;
		for (Parametro item : parametros) {
			clone.addParametro(item.clonar());
		}
		InstrucaoItem no = cabeca;
		while (no != null) {
			clone.addInstrucao(no.instrucao);
			no = no.proximo;
		}
		return clone;
	}

	public Funcao clonarComParent() throws ExpressaoException {
		Funcao funcaoParent = parent;
		Funcao clone = clonarSemParent();
		clone.setParent(funcaoParent);
		return clone;
	}

	public Funcao getParent() {
		return parent;
	}

	public void setParent(Funcao parent) {
		this.parent = parent;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public String getBiblioNativa() {
		return biblioNativa;
	}

	public void setBiblioNativa(String biblioNativa) {
		this.biblioNativa = biblioNativa;
	}

	public void setIndice(int indice) throws ExpressaoException {
		InstrucaoItem item = mapaInstrucoes.get(indice);
		if (item == null) {
			throw new ExpressaoException("erro.funcao_set_indice", getNome(), indice, biblioteca.getNomeAbsoluto());
		}
		ponteiro = item;
	}

	Instrucao proximaInstrucao() throws ExpressaoException {
		if (ponteiro == null) {
			throw new ExpressaoException("erro.funcao_sem_instrucao", getNome(), biblioteca.getNomeAbsoluto());
		}
		Instrucao resp = ponteiro.instrucao;
		ponteiro = ponteiro.proximo;
		return resp;
	}

	public void addParametro(String nome) throws ExpressaoException {
		InstrucaoUtil.checarParametro(nome);
		if (contemParametro(nome)) {
			throw new ExpressaoException("erro.parametro_existente", nome, getNome(), biblioteca.getNomeAbsoluto());
		}
		Parametro param = new Parametro(parametros.size(), nome);
		parametros.add(param);
	}

	public void addParametro(Parametro param) throws ExpressaoException {
		InstrucaoUtil.checarParametro(param.nome);
		if (contemParametro(param.nome)) {
			throw new ExpressaoException("erro.parametro_existente", param.nome, getNome(),
					biblioteca.getNomeAbsoluto());
		}
		parametros.add(param);
	}

	public Class<?>[] getTipoParametros() {
		Class<?>[] resp = new Class<?>[getTotalParametro()];
		for (int i = 0; i < resp.length; i++) {
			resp[i] = Object.class;
		}
		return resp;
	}

	public Object[] getValorParametros() {
		Object[] resp = new Object[getTotalParametro()];
		for (int i = 0; i < resp.length; i++) {
			Object valor = getValorParametro(i);
			resp[i] = valor;
		}
		return resp;
	}

	public List<Integer> getIndiceParametros() {
		List<Integer> resp = new ArrayList<>();
		for (Parametro item : parametros) {
			resp.add(item.getIndice());
		}
		return resp;
	}

	public void setValorParametro(int indice, Object valor) throws ExpressaoException {
		InstrucaoUtil.checarOperando(valor);
		parametros.get(indice).valor = valor;
	}

	public void setValorParametro(String nome, Object valor) throws ExpressaoException {
		int pos = getIndiceParametro(nome);
		setValorParametro(pos, valor);
	}

	public Object getValorParametro(int indice) {
		return parametros.get(indice).valor;
	}

	public Object getValorParametro(String nome) throws ExpressaoException {
		int pos = getIndiceParametro(nome);
		return getValorParametro(pos);
	}

	public int getTotalParametro() {
		return parametros.size();
	}

	public boolean contemParametro(String string) {
		return getParametro(string) != null;
	}

	public Parametro getParametro(String string) {
		for (Parametro item : parametros) {
			if (item.nome.equals(string)) {
				return item;
			}
		}
		return null;
	}

	private int getIndiceParametro(String nome) throws ExpressaoException {
		for (int i = 0; i < parametros.size(); i++) {
			Parametro item = parametros.get(i);
			if (item.nome.equals(nome)) {
				return i;
			}
		}
		throw new ExpressaoException("erro.parametro_inexistente", nome, getNome(), biblioteca.getNomeAbsoluto());
	}

	public void addInstrucao(Instrucao instrucao) throws ExpressaoException {
		if (instrucao == null) {
			return;
		}
		if (isNativo()) {
			throw new ExpressaoException("erro.funcao_nativa_add_inst", nome, biblioteca.getNomeAbsoluto());
		}
		InstrucaoItem no = new InstrucaoItem(instrucao);
		mapaInstrucoes.put(instrucao.indice, no);
		if (cabeca == null) {
			ponteiro = no;
			cabeca = no;
		}
		if (cauda != null) {
			cauda.proximo = no;
		}
		cauda = no;
	}

	public boolean isNativo() {
		return biblioNativa != null;
	}

	public boolean isTipoVoid() {
		return tipoVoid;
	}

	public void setTipoVoid(boolean tipoVoid) {
		this.tipoVoid = tipoVoid;
	}

	@Override
	public String toString() {
		return Integer.toHexString(hashCode()).toUpperCase() + ":" + (isNativo() ? "nativo " + biblioNativa + " " : "")
				+ nome + "(" + parametros + ")";
	}

	public String getInterface() {
		return nome + "(" + param(parametros) + ")";
	}

	private String param(List<Parametro> parametros) {
		StringBuilder sb = new StringBuilder();
		for (Parametro item : parametros) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(item.getNome());
		}
		return sb.toString();
	}

	public String getInterfaceInfo() {
		StringBuilder sb = new StringBuilder(getInterface());
		if (tipoVoid) {
			sb.append(" : " + IFuncaoContexto.VOID);
		}
		if (isNativo()) {
			sb.append(" [nativo]");
		}
		return sb.toString();
	}
}