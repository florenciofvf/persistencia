package br.com.persist.plugins.consulta;

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

public class ConsultaFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new ConsultaPaginaServico();
	}

	private class ConsultaPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ConsultaContainer(null, formulario, null, null);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuConsulta(formulario));
		return lista;
	}

	private class MenuConsulta extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuConsulta(Formulario formulario) {
			super(Constantes.LABEL_CONSULTA, Icones.TABELA);

			ficharioAcao.setActionListener(
					e -> formulario.adicionarPagina(new ConsultaContainer(null, formulario, null, null)));
			formularioAcao.setActionListener(e -> ConsultaFormulario.criar(formulario, null, null));
			dialogoAcao.setActionListener(e -> ConsultaDialogo.criar(formulario, null, null));
		}
	}
}