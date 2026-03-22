package br.com.persist.plugins.expressao.compilador;

public class PacoteContexto extends Contexto {
	private Token pacote;

	@Context("pacote_da_funcao")
	@Doc({ "package nome;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		if (token.isNomePacote()) {
			if (pacote != null) {
				compilador.invalidar(token, "pacote_ja_definido");
			} else {
				pacote = token;
			}
		} else if (token.isPontoEVirgula()) {
			if (pacote == null) {
				compilador.invalidar(token, "pacote_nao_definido");
			} else {
				compilador.setSelecionado(parent);
			}
		} else {
			compilador.invalidar(token);
		}
	}
}