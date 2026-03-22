package br.com.persist.plugins.expressao.compilador;

public class AliasContexto extends Contexto {
	private Token pacote;
	private Token chave;

	@Context("alias_para_biblioteca")
	@Doc({ "alias chaveN chave;" })
	@Override
	public void processar(Compilador compilador, Token token) {
		if (token.isChaveN()) {
			if (pacote != null) {
				compilador.invalidar(token, "pacote_ja_definido");
			} else {
				pacote = token;
			}
		} else if (token.isChave()) {
			if (chave != null) {
				compilador.invalidar(token, "chave_ja_definida");
			} else {
				chave = token;
			}
		} else if (token.isPontoEVirgula()) {
			if (pacote == null) {
				compilador.invalidar(token, "pacote_nao_definido");
			} else if (chave == null) {
				compilador.invalidar(token, "chave_nao_definida");
			} else {
				compilador.setSelecionado(parent);
			}
		} else {
			compilador.invalidar(token);
		}
	}
}