package br.com.persist.plugins.mapa;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class MapaFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Preferencias.addOutraPreferencia(MapaPreferencia.class);
		Util.criarDiretorio(MapaConstantes.MAPAS);
	}

	@Override
	public AbstratoConfiguracao getConfiguracao(Formulario formulario) {
		return new MapaConfiguracao(formulario);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new MapaPaginaServico();
	}

	private class MapaPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new MapaContainer(null, formulario, null, stringPersistencia);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuMapa(formulario));
		return lista;
	}

	private class MenuMapa extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuMapa(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.BOLA_VERDE);
			setText(MapaMensagens.getString(MapaConstantes.LABEL_MAPA));
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new MapaContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> MapaFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> MapaDialogo.criar(formulario));
		}
	}
}