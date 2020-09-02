package br.com.persist.container;

import br.com.persist.componente.Panel;
import br.com.persist.fichario.Pagina;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;

public abstract class AbstratoContainer extends Panel implements Pagina {
	private static final long serialVersionUID = 1L;
	protected final Formulario formulario;

	public AbstratoContainer(Formulario formulario) {
		this.formulario = formulario;
	}

	public abstract void setJanela(IJanela janela);

	public Formulario getFormulario() {
		return formulario;
	}

	/*protected void destacarEmFormulario() {
	}

	protected void clonarEmFormulario() {
	}

	protected void abrirEmFormulario() {
	}

	protected void retornoAoFichario() {
	}

	public String classeFabricaEContainer(Class<?> fabrica, Class<?> container, Object... detalhes) {
		StringBuilder sb = new StringBuilder(Constantes.III);
		sb.append(fabrica.getName());
		sb.append(Constantes.SEP);
		sb.append(container.getName());

		for (Object detalhe : detalhes) {
			sb.append(detalhe);
		}

		return sb.toString();
	}*/
}