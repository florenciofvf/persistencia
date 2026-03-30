package br.com.persist.plugins.expressao.compl.biblio;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
				compilador.setSelecionado(pacote);
				addPackage(pacote);
			} else if (ExpressaoConstantes.ALIAS.equals(token.getString())) {
				AliasContexto alias = new AliasContexto();
				compilador.setSelecionado(alias);
				add(alias);
			} else if (ExpressaoConstantes.CONST.equals(token.getString())) {
				ConstanteContexto constante = new ConstanteContexto();
				compilador.setSelecionado(constante);
				add(constante);
			} else if (ExpressaoConstantes.DEFUN.equals(token.getString())) {
				FuncaoContexto funcao = new FuncaoContexto();
				compilador.setSelecionado(funcao);
				add(funcao);
			} else if (ExpressaoConstantes.DEFUN_NATIVE.equals(token.getString())) {
				FuncaoNativaContexto funcaoNativa = new FuncaoNativaContexto();
				compilador.setSelecionado(funcaoNativa);
				add(funcaoNativa);
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
		add(funcaoConstantes);
		List<ConstanteContexto> lista = getListaConstantes();
		for (ConstanteContexto item : lista) {
			remove(item);
			funcaoConstantes.add(item);
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
		add(c);
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
		}
		pw.println();
		for (Contexto item : getListaFuncoes()) {
			salvarFuncao(item, pw);
		}
	}

	private void salvarFuncao(Contexto funcao, PrintWriter pw) throws ExpressaoException {
		pw.println();
		funcao.salvar(pw);
		funcao.configurarSaltos();

		List<Contexto> contextos = new ArrayList<>();
		funcao.listar(contextos);

		Indexador indexador = new Indexador();
		for (Contexto item : contextos) {
			item.indexar(indexador);
		}

		for (Contexto item : contextos) {
			item.indexar(indexador);
		}
	}
}