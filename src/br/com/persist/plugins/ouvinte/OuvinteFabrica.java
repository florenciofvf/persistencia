package br.com.persist.plugins.ouvinte;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class OuvinteFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new OuvintePaginaServico();
	}

	private class OuvintePaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new OuvinteContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new OuvinteServico());
	}

	private class OuvinteServico extends AbstratoServico {
		@Override
		public void processar(Formulario formulario, Map<String, Object> args) {
			OuvinteFormulario form = formulario.getOuvinteFormulario();
			if (form != null) {
				form.processar(formulario, args);
			}
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuOuvinte(formulario));
		return lista;
	}

	private class MenuOuvinte extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuOuvinte(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.PANEL);
			setText(OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new OuvinteContainer(null, formulario)));
			formularioAcao.setActionListener(e -> OuvinteFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> OuvinteDialogo.criar(formulario));
		}
	}
}