package br.com.persist.plugins.expressao.compl.biblio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compl.Compilador;
import br.com.persist.plugins.expressao.compl.Context;
import br.com.persist.plugins.expressao.compl.Contexto;
import br.com.persist.plugins.expressao.compl.Doc;
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
		this.file = file;
	}

	public File getFile() {
		return file;
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
				add(pacote);
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
}