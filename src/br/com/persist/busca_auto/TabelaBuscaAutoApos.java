package br.com.persist.busca_auto;

public class TabelaBuscaAutoApos {
	private final String apelido;
	private final String nome;

	public TabelaBuscaAutoApos(String apelido, String nome) {
		this.apelido = apelido.trim();
		this.nome = nome.trim();
	}

	public boolean igual(TabelaBuscaAutoApos tabela) {
		return apelido.equals(tabela.apelido) && nome.equals(tabela.nome);
	}

	public String getApelido() {
		return apelido;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}