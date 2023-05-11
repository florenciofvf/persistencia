package br.com.persist.plugins.gera_plugin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class GeraPluginFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new GeraPluginPaginaServico();
	}

	private class GeraPluginPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new GeraPluginContainer(null, formulario);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuGeraPlugin(formulario));
		return lista;
	}

	private class MenuGeraPlugin extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuGeraPlugin(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.CRIAR2);
			setText(GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new GeraPluginContainer(null, formulario)));
			formularioAcao.setActionListener(e -> GeraPluginFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> GeraPluginDialogo.criar(formulario));
		}
	}
}