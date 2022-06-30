package br.com.persist.plugins.checagem;

public class Bloco {
	private Sentenca sentenca;
	private final String id;
	private String string;

	public Bloco(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Sentenca getSentenca() {
		return sentenca;
	}

	public void setSentenca(Sentenca sentenca) {
		this.sentenca = sentenca;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Object executar(Checagem checagem, Contexto ctx) throws ChecagemException {
		if (sentenca == null) {
			return null;
		}
		return sentenca.executar(checagem, this, ctx);
	}
}