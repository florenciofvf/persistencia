package br.com.persist.plugins.checagem;

public class Bloco {
	private final StringBuilder preString;
	private final StringBuilder posString;
	private final StringBuilder string;
	private boolean paraPre = true;
	private final Modulo modulo;
	private boolean paraString;
	private Sentenca sentenca;
	private boolean paraPos;
	private boolean privado;
	private final String id;

	public Bloco(Modulo modulo, String id) {
		preString = new StringBuilder();
		posString = new StringBuilder();
		string = new StringBuilder();
		this.modulo = modulo;
		this.id = id;
	}

	public void append(String s) {
		if (paraPre) {
			preString.append(s);
		} else if (paraString) {
			string.append(s);
		} else if (paraPos) {
			posString.append(s);
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

	public String getPosString() {
		return posString.toString();
	}

	public boolean isPrivado() {
		return privado;
	}

	public void setParaString(boolean paraString) {
		this.paraString = paraString;
	}

	public void setParaPre(boolean paraPre) {
		this.paraPre = paraPre;
	}

	public void setParaPos(boolean paraPos) {
		this.paraPos = paraPos;
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