package br.com.persist.plugins.checagem;

public class Bloco {
	private final StringBuilder preString;
	private final StringBuilder string;
	private final Modulo modulo;
	private boolean paraString;
	private Sentenca sentenca;
	private boolean privado;
	private final String id;

	public Bloco(Modulo modulo, String id) {
		preString = new StringBuilder();
		string = new StringBuilder();
		this.modulo = modulo;
		this.id = id;
	}

	public void append(String s) {
		if (paraString) {
			string.append(s);
		} else {
			preString.append(s);
		}
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

	public String getPreString() {
		return preString.toString();
	}

	public String getString() {
		return string.toString();
	}

	public boolean isPrivado() {
		return privado;
	}

	public void setParaString(boolean paraString) {
		this.paraString = paraString;
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