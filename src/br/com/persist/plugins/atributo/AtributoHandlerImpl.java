package br.com.persist.plugins.atributo;

public class AtributoHandlerImpl implements AtributoHandler {
	private String ultima;
	private boolean sep;
	private Mapa raiz;
	private Mapa sel;

	@Override
	public void setString(String string) throws AtributoException {
		if (sep) {
			if (ultima == null) {
				throw new AtributoException(string, false);
			}
			sel.put(ultima, string);
			ultima = null;
			sep = false;
		} else {
			if (ultima != null) {
				throw new AtributoException(string, false);
			}
			ultima = string;
		}
	}

	@Override
	public void separador() {
		sep = true;
	}

	@Override
	public void iniMapa() throws AtributoException {
		Mapa mapa = new Mapa();
		if (raiz == null) {
			raiz = mapa;
		} else {
			if (sep) {
				if (ultima == null) {
					throw new AtributoException("iniMapa(): ultima == null", false);
				}
				sel.put(ultima, mapa);
				ultima = null;
				sep = false;
			} else {
				throw new AtributoException("iniMapa(): sep == false", false);
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