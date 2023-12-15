package br.com.persist.plugins.atributo;

public class AtributoHandlerImpl implements AtributoHander {
	private String ultima;
	private boolean sep;
	private Mapa raiz;
	private Mapa sel;

	@Override
	public void setString(String string) {
		if (sep) {
			if (ultima == null) {
				throw new IllegalStateException(string);
			}
			sel.put(ultima, string);
			ultima = null;
			sep = false;
		} else {
			if (ultima != null) {
				throw new IllegalStateException(string);
			}
			ultima = string;
		}
	}

	@Override
	public void separador() {
		sep = true;
	}

	@Override
	public void iniMapa() {
		Mapa mapa = new Mapa();
		if (raiz == null) {
			raiz = mapa;
		} else {
			if (sep) {
				if (ultima == null) {
					throw new IllegalStateException();
				}
				sel.put(ultima, mapa);
				ultima = null;
				sep = false;
			} else {
				throw new IllegalStateException();
			}
		}
		sel = mapa;
	}

	@Override
	public void fimMapa() {
		sel = sel.parent;
	}

	public Mapa getRaiz() {
		return raiz;
	}
}