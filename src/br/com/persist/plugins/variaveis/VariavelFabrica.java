package br.com.persist.plugins.variaveis;

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

public class VariavelFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new VariavelPaginaServico();
	}

	private class VariavelPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new VariavelContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new VariavelServico());
	}

	private class VariavelServico extends AbstratoServico {
		@Override
		public void visivelFormulario(Formulario formulario) {
			VariavelProvedor.inicializar();
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuVariavel(formulario));
		return lista;
	}

	private class MenuVariavel extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuVariavel(Formulario formulario) {
			super(Constantes.LABEL_VARIAVEIS, Icones.VAR);

			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new VariavelContainer(null, formulario)));
			formularioAcao.setActionListener(e -> VariavelFormulario.criar(formulario));
			dialogoAcao.setActionListener(e -> VariavelDialogo.criar(formulario));
		}
	}
}