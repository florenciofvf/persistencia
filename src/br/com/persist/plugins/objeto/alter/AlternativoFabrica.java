package br.com.persist.plugins.objeto.alter;

import java.awt.Window;
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
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class AlternativoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Util.criarDiretorio(AlternativoConstantes.ALTERNATIVOS);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new AlternativoPaginaServico();
	}

	private class AlternativoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new AlternativoContainer(null, formulario, null);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new AlternativoServico());
	}

	private class AlternativoServico extends AbstratoServico {
		@Override
		public void windowOpenedHandler(Window window) {
			AlternativoProvedor.inicializar();
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuAlternativo(formulario));
		return lista;
	}

	private class MenuAlternativo extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAlternativo(Formulario formulario) {
			super(Constantes.LABEL_ALTERNATIVO, Icones.FILTRO);
			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new AlternativoContainer(null, formulario, null)));
			formularioAcao.setActionListener(e -> AlternativoFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> AlternativoDialogo.criar(formulario));
		}
	}
}