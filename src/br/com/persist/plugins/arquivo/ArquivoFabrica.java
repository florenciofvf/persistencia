package br.com.persist.plugins.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.componente.Action;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;

public class ArquivoFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new ArquivoPaginaServico();
	}

	private class ArquivoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			if (SeparadorArquivo.STRING_PERSISTENCIA.equals(stringPersistencia)) {
				return new SeparadorArquivo();
			}
			return new ArquivoContainer(null, formulario);
		}
	}

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new ArquivoServico());
	}

	private class ArquivoServico extends AbstratoServico {
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuArquivo(formulario));
		return lista;
	}

	private class MenuArquivo extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;
		private Action separadorAcao = Action.actionMenu("label.separador", null);

		private MenuArquivo(Formulario formulario) {
			super(Constantes.LABEL_ARQUIVOS, Icones.EXPANDIR, false);
			addMenuItem(true, separadorAcao);
			ficharioAcao.setActionListener(e -> formulario.adicionarPagina(new ArquivoContainer(null, formulario)));
			separadorAcao.setActionListener(e -> formulario.adicionarPagina(new SeparadorArquivo()));
			formularioAcao.setActionListener(e -> ArquivoFormulario.criar(formulario));
		}
	}
}