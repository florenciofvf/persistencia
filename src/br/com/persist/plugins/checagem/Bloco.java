package br.com.persist.plugins.checagem;

public class Bloco {
	private final Modulo modulo;
	private Sentenca sentenca;
	private boolean privado;
	private final String id;
	private String string;

	public Bloco(Modulo modulo, String id) {
		this.modulo = modulo;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Modulo getModulo() {
		return modulo;
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

	public boolean isPrivado() {
		return privado;
	}

	public void setPrivado(boolean privado) {
		this.privado = privado;
	}

	public Object executar(Checagem checagem, Contexto ctx) throws ChecagemException {
		if (sentenca == null) {
			return null;
		}
		return sentenca.executar(checagem, this, ctx);
	}

	@Override
	public String toString() {
		return id + "\n" + string;
	}
}