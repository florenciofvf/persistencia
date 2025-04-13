package br.com.persist.plugins.biblio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class BiblioFabrica extends AbstratoFabricaContainer {
	@Override
	public PaginaServico getPaginaServico() {
		return new BiblioPaginaServico();
	}

	private class BiblioPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new BiblioContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new BiblioServico());
	}

	private class BiblioServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuBiblio(formulario));
		return lista;
	}

	private class MenuBiblio extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuBiblio(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.COR);
			setText(BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO));
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new BiblioContainer(null, formulario)));
			formularioAcao.setActionListener(e -> BiblioFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> BiblioDialogo.criar(formulario));
		}
	}
}