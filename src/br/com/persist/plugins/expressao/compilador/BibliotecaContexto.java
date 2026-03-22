package br.com.persist.plugins.expressao.compilador;

import br.com.persist.plugins.expressao.ExpressaoConstantes;

public class BibliotecaContexto extends Contexto {
	@Context("biblioteca")
	@Doc({ "package;", "alias;", "const;", "defun;", "defun_native;" })
	@Override
	public void processar(Compilador compilador, Token token) {
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
}