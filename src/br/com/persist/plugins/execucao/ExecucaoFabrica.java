package br.com.persist.plugins.execucao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.formulario.Formulario;

public class ExecucaoFabrica extends AbstratoFabricaContainer {
	@Override
	public void inicializar() {
		Util.criarDiretorio(ExecucaoConstantes.EXECUCOES);
	}

	@Override
	public PaginaServico getPaginaServico() {
		return new ExecucaoPaginaServico();
	}

	private class ExecucaoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			return new ExecucaoContainer(null, formulario, null);
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		List<JMenuItem> lista = new ArrayList<>();
		lista.add(new MenuExecucao(formulario));
		return lista;
	}

	private class MenuExecucao extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuExecucao(Formulario formulario) {
			super(Constantes.LABEL_VAZIO, Icones.PANEL);
			setText(ExecucaoMensagens.getString(ExecucaoConstantes.LABEL_EXECUCOES));
			ficharioAcao
					.setActionListener(e -> formulario.adicionarPagina(new ExecucaoContainer(null, formulario, null)));
			formularioAcao.setActionListener(e -> ExecucaoFormulario.criar(formulario, Constantes.VAZIO));
			dialogoAcao.setActionListener(e -> ExecucaoDialogo.criar(formulario));
		}
	}
}