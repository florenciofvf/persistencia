package br.com.persist.plugins.metadado;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class MetadadoFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new MetadadoPaginaServico();
	}

	private class MetadadoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new MetadadoContainer(null, formulario, null);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuMetadado(formulario));
		return lista;
	}

	private class MenuMetadado extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuMetadado(Formulario formulario) {
			super(Constantes.LABEL_METADADOS, Icones.CAMPOS, false);

			ficharioAcao
					.setActionListener(e -> formulario.adicionarPagina(new MetadadoContainer(null, formulario, null)));
			formularioAcao.setActionListener(e -> MetadadoFormulario.criar(formulario, (Conexao) null));
		}
	}
}