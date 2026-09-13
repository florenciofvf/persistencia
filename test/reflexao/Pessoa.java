package reflexao;

public class Pessoa {
	private final String nome;
	private Mes mesParaFerias;

	public Pessoa(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public Mes getMesParaFerias() {
		return mesParaFerias;
	}

	public void setMesParaFerias(Mes mesParaFerias) {
		this.mesParaFerias = mesParaFerias;
	}

	@Override
	public String toString() {
		return nome + " tira férias em " + mesParaFerias;
	}
}