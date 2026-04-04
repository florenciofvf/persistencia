package br.com.persist.plugins.expressao.compl.biblio;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
import br.com.persist.plugins.expressao.compl.Indexador;
import br.com.persist.plugins.expressao.compl.Token;
import br.com.persist.plugins.expressao.compl.Token.Tipo;
import br.com.persist.plugins.expressao.compl.funcao.FuncaoConstantesContexto;
import br.com.persist.plugins.expressao.compl.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.compl.funcao.FuncaoNativaContexto;
import br.com.persist.plugins.expressao.compl.organiza.AliasContexto;
import br.com.persist.plugins.expressao.compl.organiza.PacoteContexto;
import br.com.persist.plugins.expressao.compl.salto.GotoContexto;

public class BibliotecaContexto extends Contexto {
	private FuncaoConstantesContexto funcaoConstantes;
	private final File file;

	public BibliotecaContexto(File file) {
		this.file = Objects.requireNonNull(file);
	}

	public File getFile() {
		return file;
	}

	public String getNome() {
		return file.getName();
	}

	public FuncaoConstantesContexto getFuncaoConstantes() {
		return funcaoConstantes;
	}

	@Context("biblioteca")
	@Doc({ "package;", "alias;", "const;", "defun;", "defun_native;" })
	@Override
	public void processar(Compilador compilador, Token token) throws ExpressaoException {
		if (token.isReservado()) {
			if (ExpressaoConstantes.PACKAGE.equals(token.getString())) {
				PacoteContexto pacote = new PacoteContexto();
				compilador.selecionar(pacote);
				addPackage(pacote);
			} else if (ExpressaoConstantes.ALIAS.equals(token.getString())) {
				AliasContexto alias = new AliasContexto();
				compilador.selecionar(alias);
				adicionar(alias);
			} else if (ExpressaoConstantes.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.selecionar(constante);
				adicionar(constante);
			} else if (ExpressaoConstantes.DEFUN.equals(token.getString())) {
				FuncaoContexto funcao = new FuncaoContexto();
				compilador.selecionar(funcao);
				adicionar(funcao);
			} else if (ExpressaoConstantes.DEFUN_NATIVE.equals(token.getString())) {
				FuncaoNativaContexto funcaoNativa = new FuncaoNativaContexto();
				compilador.selecionar(funcaoNativa);
				adicionar(funcaoNativa);
			} else {
				compilador.invalidar(token);
			}
		} else {
			compilador.invalidar(token);
		}
	}

	public void transferirConstantes() throws ExpressaoException {
		Token token = new Token(FuncaoConstantesContexto.NOME_FUNCAO_CONSTANTES, Tipo.VIRTUAL, -1);
		funcaoConstantes = new FuncaoConstantesContexto(token);
		adicionar(funcaoConstantes);
		List<ConstanteContexto> lista = getListaConstantes();
		for (ConstanteContexto item : lista) {
			remove(item);
			funcaoConstantes.adicionar(item);
		}
	}

	private List<ConstanteContexto> getListaConstantes() {
		List<ConstanteContexto> lista = new ArrayList<>();
		for (Contexto item : componentes) {
			if (item instanceof ConstanteContexto) {
				lista.add((ConstanteContexto) item);
			}
		}
		return lista;
	}

	protected void addPackage(PacoteContexto c) throws ExpressaoException {
		List<PacoteContexto> lista = getListaPacote();
		if (!lista.isEmpty()) {
			throw new ExpressaoException("erro.package.multiplo");
		}
		adicionar(c);
	}

	public void checarPackage() throws ExpressaoException {
		List<PacoteContexto> lista = getListaPacote();
		if (lista.isEmpty()) {
			throw new ExpressaoException("erro.package.inexistente");
		}
		if (lista.size() > 1) {
			throw new ExpressaoException("erro.package.multiplo");
		}
	}

	public void checarAlias() throws ExpressaoException {
		List<AliasContexto> lista = getListaAlias();
		for (int i = 0; i < lista.size(); i++) {
			AliasContexto item = lista.get(i);
			int totalBiblio = getTotal(item.getBiblioteca(), lista, true);
			if (totalBiblio > 1) {
				throw new ExpressaoException("erro.alias.multiplo_biblio", item.getBiblioteca());
			}
			int totalAlias = getTotal(item.getAlias(), lista, false);
			if (totalAlias > 1) {
				throw new ExpressaoException("erro.alias.multiplo_alias", item.getAlias());
			}
		}
	}

	private int getTotal(String procurado, List<AliasContexto> lista, boolean biblio) throws ExpressaoException {
		int total = 0;
		for (AliasContexto item : lista) {
			if (biblio) {
				if (item.getBiblioteca().equals(procurado)) {
					total++;
				}
			} else {
				if (item.getAlias().equals(procurado)) {
					total++;
				}
			}
		}
		return total;
	}

	private List<PacoteContexto> getListaPacote() {
		List<PacoteContexto> lista = new ArrayList<>();
		for (Contexto item : componentes) {
			if (item instanceof PacoteContexto) {
				lista.add((PacoteContexto) item);
			}
		}
		return lista;
	}

	private List<AliasContexto> getListaAlias() {
		List<AliasContexto> lista = new ArrayList<>();
		for (Contexto item : componentes) {
			if (item instanceof AliasContexto) {
				lista.add((AliasContexto) item);
			}
		}
		return lista;
	}

	private Map<String, String> getMapaAlias() throws ExpressaoException {
		Map<String, String> map = new HashMap<>();
		for (AliasContexto item : getListaAlias()) {
			map.put(item.getAlias(), item.getBiblioteca());
		}
		return map;
	}

	private List<Contexto> getListaFuncoes() {
		List<Contexto> lista = new ArrayList<>();
		for (Contexto item : componentes) {
			if ((item instanceof FuncaoContexto) || (item instanceof FuncaoNativaContexto)) {
				lista.add(item);
			}
		}
		return lista;
	}

	public PacoteContexto getPackage() throws ExpressaoException {
		checarPackage();
		return getListaPacote().get(0);
	}

	public void salvarEstruturas(PrintWriter pw) throws ExpressaoException {
		getPackage().salvar(pw);
		pw.println();

		for (Contexto item : getListaAlias()) {
			item.salvar(pw);
			pw.println();
		}

		Map<String, String> mapAlias = getMapaAlias();

		for (Contexto item : getListaFuncoes()) {
			processarFuncao(item, mapAlias, pw);
			pw.println();
		}
	}

	private void processarFuncao(Contexto funcao, Map<String, String> mapAlias, PrintWriter pw)
			throws ExpressaoException {
		funcao.configurarAliasInvocacao(mapAlias);
		funcao.configurarSaltos();

		List<Contexto> contextos = new ArrayList<>();
		funcao.listar(contextos);

		normalizarGoto(contextos);

		Indexador indexador = new Indexador();
		for (Contexto item : contextos) {
			item.indexar(indexador);
		}

		funcao.salvar(pw);

		for (Contexto item : contextos) {
			item.salvar(pw);
		}
	}

	private void normalizarGoto(List<Contexto> contextos) {
		for (int i = 0; i < contextos.size(); i++) {
			Contexto c = contextos.get(i);
			if (c instanceof GotoContexto) {
				checarSeguinte((GotoContexto) c, contextos, i + 1);
			}
		}
		Iterator<Contexto> it = contextos.iterator();
		while (it.hasNext()) {
			Contexto c = it.next();
			if (c instanceof GotoContexto && ((GotoContexto) c).isDispensavel()) {
				it.remove();
			}
		}
		while (penultimo(contextos)) {
			excluirPenultimo(contextos);
		}
	}

	private void checarSeguinte(GotoContexto gotoContexto, List<Contexto> contextos, int indice) {
		if (indice < contextos.size()) {
			Contexto c = contextos.get(indice);
			if (c instanceof GotoContexto) {
				GotoContexto proximo = (GotoContexto) c;
				if (proximo.getDestino() == gotoContexto.getDestino()) {
					proximo.setDispensavel(true);
				}
			}
		}
	}

	private boolean penultimo(List<Contexto> contextos) {
		int size = contextos.size();
		return size >= 2 && contextos.get(size - 2) instanceof GotoContexto;
	}

	private void excluirPenultimo(List<Contexto> contextos) {
		int size = contextos.size();
		contextos.remove(size - 2);
	}
}