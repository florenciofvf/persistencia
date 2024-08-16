package br.com.persist.plugins.mapa;

public class MapaException extends Exception {
	private static final long serialVersionUID = 1L;

	public MapaException(Throwable cause) {
		super(cause);
	}

	public MapaException(String message, Throwable cause) {
		super(message, cause);
	}

	public MapaException(String string, boolean ehChave) {
		super(ehChave ? MapaMensagens.getString(string) : string);
	}

	public MapaException(String chave) {
		this(chave, true);
	}

	public MapaException(String chave, Object... argumentos) {
		super(MapaMensagens.getString(chave, argumentos));
	}
}